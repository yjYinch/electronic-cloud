package com.sedwt.workflow.service.impl;

import com.sedwt.common.constant.ErrorCodeConstants;

import com.sedwt.common.entity.R;
import com.sedwt.common.entity.sys.SysUser;
import com.sedwt.common.exception.BusinessException;
import com.sedwt.common.remote.RemoteSystemHttp;
import com.sedwt.common.remote.system.RemoteUserHttp;
import com.sedwt.common.utils.DateUtils;
import com.sedwt.workflow.domain.Appraisal;
import com.sedwt.workflow.domain.AppraisalStatusEnum;
import com.sedwt.workflow.domain.Meeting;
import com.sedwt.workflow.mapper.AppraisalMapper;
import com.sedwt.workflow.mapper.CommentMapper;
import com.sedwt.workflow.mapper.MeetingMapper;
import com.sedwt.workflow.service.AppraisalService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author sedwt
 */
@Service
public class AppraisalServiceImpl implements AppraisalService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppraisalServiceImpl.class);
    private static final String MEETING_CREATION_RESULT = "RESULT";
    private static final String CONFLICTED_MEETING = "MEETING";
    private static final Integer MEETING_CREATION_FAILED = 0;
    private static final Integer MEETING_CREATION_SUCCEED = 1;
    private static final Integer MEETING_TIME_INVALID = 2;
    private static final Integer MEETING_CREATION_CONFLICTED = 3;
    private static final Integer APPRAISAL_STATUS_NOT_SUBMITTED = 0;
    private static final Integer APPRAISAL_STATUS_PRE_APPRAISAL = 1;
    private static final Integer APPRAISAL_STATUS_DURING_APPRAISAL = 2;
    private static final Integer APPRAISAL_STATUS_COMPLETED = 3;
    private static final Integer APPRAISAL_STATUS_CANCELED = 4;

    @Autowired
    private AppraisalMapper appraisalMapper;
    @Autowired
    private MeetingMapper meetingMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private RemoteUserHttp remoteUserHttp;
    @Autowired
    private RemoteSystemHttp remoteSystemHttp;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Value("${mq.topic}")
    private String topic;

    /**
     * 根据条件分页查询评审列表
     *
     * @param appraisal     评审信息
     * @param currentUserId 当前登录用户id
     * @return 分页列表
     */
    @Override
    public List<Appraisal> selectAppraisalList(Appraisal appraisal, Long currentUserId) {
        // 如果搜索条件不为空，去空格
        if (null != appraisal.getAppraisalTitle()) {
            appraisal.setAppraisalTitle(appraisal.getAppraisalTitle().trim());
        }
        if (null != appraisal.getCreatorName()) {
            appraisal.setCreatorName(appraisal.getCreatorName().trim());
        }
        // 超级管理员可以获取全部评审，普通用户只能参与自己发起或参与的评审
        if (currentUserId == 1) {
            // 如果是超级管理员，currentUserId设为null，mapper语句中将不会执行过滤，以显示所有评审
            currentUserId = null;
        }
        // 获取评审Entity分页列表
        List<Appraisal> appraisalList = appraisalMapper.selectAppraisalList(appraisal, currentUserId);
        // 如果是超级管理员，isOwner参数设为2，以在前端能够同时进行处理与评审操作
        if (null == currentUserId) {
            appraisalList.parallelStream().forEach(appraisalAdminView -> appraisalAdminView.setIsOwner(2));
        }
        return appraisalList;
    }

    /**
     * 根据评审id查询评审
     *
     * @param id 评审id
     * @return 评审对象信息
     */
    @Override
    public R selectAppraisalById(Long id, Long currentUserId) {
        if (null == id) {
            throw new BusinessException(ErrorCodeConstants.PARSE_PARAMETER_ERROR, "评审id不能为空！");
        }
        Appraisal appraisal = appraisalMapper.selectAppraisalById(id, currentUserId);
        if (null == appraisal) {
            return R.error(ErrorCodeConstants.RESOURCE_NOT_FOUND, "评审不存在！");
        }
        if (currentUserId == 1) {
            // 如果是超级管理员显示所有评审意见
            return R.data(appraisal);
        } else {
            List<SysUser> participants = appraisalMapper.selectParticipantsByAppraisalId(id);
            for (SysUser participant : participants) {
                if (currentUserId.equals(participant.getUserId()) && 0 == participant.getIsOwner()) {
                    // 如果是评审发起者显示所有评审意见
                    return R.data(appraisal);
                }
            }
        }
        // 如果是普通用户，只能看到自己的评审意见
        appraisal.getComments().removeIf(comment -> !comment.getUserId().equals(currentUserId));
        return R.data(appraisal);
    }

    /**
     * 根据id关闭评审
     *
     * @param appraisal 评审信息
     * @return 关闭结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R cancelAppraisalById(Appraisal appraisal) {
        if (null == appraisal.getAppraisalId()) {
            throw new BusinessException(ErrorCodeConstants.PARSE_PARAMETER_ERROR, "评审id不能为空！");
        }
        Appraisal currentAppraisal = appraisalMapper.selectAppraisalById(appraisal.getAppraisalId(), null);
        // 检查评审是否存在
        if (null == currentAppraisal) {
            return R.error(ErrorCodeConstants.RESOURCE_NOT_FOUND, "评审不存在！");
        }
        // 检查评审撤销原因
        if (null == appraisal.getRemark()) {
            return R.error(ErrorCodeConstants.PARSE_PARAMETER_ERROR, "评审撤销原因不能为空！");
        }

        // 查看当前评审状态
        switch (currentAppraisal.getStatus()) {
            // 关闭未提交的评审说明创建页面出了问题，此处尝试关闭就直接把它删除
            case 0:
                this.deleteById(appraisal.getAppraisalId());
                return R.error(ErrorCodeConstants.OPERATION_NOT_ALLOWED, "该评审未提交！");
            // 如果是已完成的评审
            case 3:
                return R.error(ErrorCodeConstants.OPERATION_NOT_ALLOWED, "已完成的评审不能再撤销！");
            // 如果是已撤销的评审
            case 4:
                return R.error(ErrorCodeConstants.OPERATION_NOT_ALLOWED, "已撤销的评审不能再撤销！");
            // 如果是可以撤销的评审（1-待评审 2-评审中）
            default:
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                appraisal.setHandleTime(df.format(new Date()));
                appraisalMapper.cancelAppraisalById(appraisal);
                meetingMapper.deleteAppraisalMeetings(appraisal.getAppraisalId());
                break;
        }

        currentAppraisal.setStatus(AppraisalStatusEnum.CANCELED.getVal());
        sendToMQ(currentAppraisal);
        return R.ok("撤销成功");
    }

    /**
     * 根据评审id删除评审
     *
     * @param id 评审id
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R deleteById(Long id) {
        if (null == id) {
            throw new BusinessException(ErrorCodeConstants.PARSE_PARAMETER_ERROR, "评审id不能为空！");
        }
        Appraisal appraisal = appraisalMapper.selectAppraisalById(id, null);
        // 检查评审是否存在
        if (null == appraisal) {
            return R.error(ErrorCodeConstants.RESOURCE_NOT_FOUND, "评审不存在！");
        }
        // 删除评审人员
        appraisalMapper.deleteAppraisalParticipants(id);
        // 删除会议
        meetingMapper.deleteAppraisalMeetings(id);
        // 删除评审评论
        commentMapper.deleteAppraisalComments(id);
        // 删除评审附件
        remoteSystemHttp.delete(id);
        // 删除评审
        appraisalMapper.deleteAppraisalById(id);
        return R.ok("删除成功！");
    }

    /**
     * 新增评审
     * 用来在点击创建评审后，创建一个空白评审单，并返回数据库自生成的id
     *
     * @param currentUserId 当前用户id（评审创建人id）
     * @return 新增评审的id
     */
    @Override
    public R addAppraisal(Long currentUserId) {
        Appraisal appraisal = new Appraisal();
        // 设置评审状态为 0-未提交
        appraisal.setStatus(APPRAISAL_STATUS_NOT_SUBMITTED);
        // 设置评审创建人
        appraisal.setCreateBy(currentUserId.toString());
        // xml中配置的useGeneratedKeys和keyProperty使该行代码执行后表中id填充回实例appraisal中
        // 返回刚创建的评审(包含数据库中的id)
        return appraisalMapper.addAppraisal(appraisal) > 0 ? R.data(appraisal)
                : R.error(ErrorCodeConstants.DATABASE_OPERATION_FAILED, "新增失败！");
    }

    /**
     * 创建/更新评审
     * 即是创建评审页面点击提交的结果，并非创建新数据，而是根据“新增评审”返回的id在数据库中进行修改
     * 或者更新评审
     *
     * @param appraisal     评审信息
     * @param currentUserId 当前用户的登录id（评审创建人）
     * @return 创建结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R editAppraisal(Appraisal appraisal, Long currentUserId) {
        // 检查必要参数是否为空
        this.isEditEmpty(appraisal);
        // 查询旧的评审信息
        Appraisal currentAppraisal = appraisalMapper.selectAppraisalById(appraisal.getAppraisalId(), null);
        appraisal.setEmailAddress(appraisalMapper.selectEmailById(currentUserId));
        // 检查评审是否存在
        if (null == currentAppraisal) {
            return R.error(ErrorCodeConstants.RESOURCE_NOT_FOUND, "评审不存在！");
        }
        // 检查评审标题是否重复
        if (appraisalMapper.countAppraisalTitle(appraisal.getAppraisalTitle()) > 0) {
            return R.error(ErrorCodeConstants.RESOURCE_EXISTED, "评审标题重复！");
        }

        // 获取当前评审状态
        switch (currentAppraisal.getStatus()) {
            // 如果是创建页面提交的初始评审
            case 0:
                // 若提交的参与评审人员为空，返回错误信息
                if (org.apache.commons.collections.CollectionUtils.isEmpty(appraisal.getParticipants())) {
                    return R.error(ErrorCodeConstants.PARSE_PARAMETER_ERROR, "参与评审人员不能为空！");
                }
                // 设置初始参与评审人数为0
                appraisal.setHandledPersons(0);
                // 设置评审状态为 1-待评审
                appraisal.setStatus(APPRAISAL_STATUS_PRE_APPRAISAL);
                // 将评审创建人加入评审人员表
                appraisalMapper.createAppraisalOwner(appraisal.getAppraisalId(), currentUserId);
                break;
            // 如果是已完成的评审
            case 3:
                return R.error(ErrorCodeConstants.OPERATION_NOT_ALLOWED, "已完成的评审不能再更新！");
            // 如果是已撤销的评审
            case 4:
                return R.error(ErrorCodeConstants.OPERATION_NOT_ALLOWED, "已撤销的评审不能再更新！");
            // 如果不是新评审或完成、撤销的评审，则更新评审内容
            default:
                // 传入的参与评审人员若不为空，代表更新评审时人员有变动
                if (CollectionUtils.isNotEmpty(appraisal.getParticipants())) {
                    // 此处删除原有参与评审人员，后面重新加入
                    appraisalMapper.deleteAppraisalParticipants(currentAppraisal.getAppraisalId());
                }
                break;
        }

        // 检查时间格式是否合法
        if (null == this.isValidTime(appraisal.getBeginTime()) || null == this.isValidTime(appraisal.getEndTime())) {
            throw new BusinessException(ErrorCodeConstants.PARAMETER_FORMAT_ERROR, "时间格式不合法");
        }

        // 查询会议创建结果是否冲突或失败
        HashMap<String, Object> meetingResult = this.createMeeting(appraisal);
        this.checkMeetingValidity(meetingResult);

        // 将参与评审的人员加入评审人员关联表
        // 如果参与评审的人员不为空，则情况为：
        // 1. 此时在新建初始评审
        // 2. 更新评审时人员有变动

        if (CollectionUtils.isEmpty(appraisal.getParticipants())) {
            throw new BusinessException(ErrorCodeConstants.RESOURCE_NOT_FOUND, "参与评审人员为空");
        }
        // 获取参与评审的人员
        List<SysUser> participants = appraisal.getParticipants();
        try {
            // 将参与评审的人员加入到评审人员表
            participants.forEach(participant -> {
                SysUser user = remoteUserHttp.selectSysUserByUsername(participant.getLoginName());
                // 如果组织者将自己加入了评审人员，返回错误
                if (user.getUserId().equals(currentUserId)) {
                    throw new BusinessException(ErrorCodeConstants.PERMISSION_ERROR, "你不能把自己也加到参与人员里！");
                }
                appraisalMapper.addAppraisalParticipant(appraisal.getAppraisalId(), user.getUserId());
            });
        } catch (NullPointerException e) {
            throw new BusinessException(ErrorCodeConstants.RESOURCE_NOT_FOUND, "参与评审人员创建失败！用户不存在！");
        } catch (BusinessException e) {
            throw new BusinessException(e.getCode(), e.getMessage());
        }
        // 设置总评审人数
        appraisal.setAppraisalPersons(participants.size());

        // TODO: 将评审附件加入附件表

        // 设置评审更新人
        appraisal.setUpdateBy(currentUserId.toString());
        // 如果更新失败
        if (appraisalMapper.updateAppraisal(appraisal) <= 0) {
            throw new BusinessException(ErrorCodeConstants.DATABASE_OPERATION_FAILED, "更新失败！");
        }

        // 发给mq处理邮件模块
        sendToMQ(appraisal);
        return R.ok("提交成功！");
    }

    /**
     * 更新评审流程状态
     *
     * @param appraisal     评审信息。status为 2 代表开始评审；status为 3 代表完成评审。
     * @param currentUserId 当前用户id
     * @return 更新结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R processAppraisal(Appraisal appraisal, Long currentUserId) {
        // 检查参数合法性
        this.isProcessEmpty(appraisal);
        Appraisal currentAppraisal = appraisalMapper.selectAppraisalById(appraisal.getAppraisalId(), null);
        // 检查评审是否存在
        if (null == currentAppraisal) {
            return R.error(ErrorCodeConstants.RESOURCE_NOT_FOUND, "评审不存在！");
        }

        // 查看当前评审状态
        switch (appraisal.getStatus()) {
            // 如果是选择开始评审
            case 2:
                if (!currentAppraisal.getStatus().equals(APPRAISAL_STATUS_PRE_APPRAISAL)) {
                    return R.error(ErrorCodeConstants.OPERATION_NOT_ALLOWED, "只允许开始未评审的评审！");
                } else {
                    // 检查是否有意见未处理
                    commentMapper.selectCommentsByAppraisalId(appraisal.getAppraisalId()).parallelStream().forEach(comment -> {
                        if (comment.getHandleStatus() == 0) {
                            throw new BusinessException(ErrorCodeConstants.OPERATION_NOT_ALLOWED, "还有意见没处理！！");
                        }
                    });
                    // 检查会议时间格式是否合法
                    if (null == this.isValidTime(appraisal.getBeginTime()) || null == this.isValidTime(appraisal.getEndTime())) {
                        throw new BusinessException(ErrorCodeConstants.PARAMETER_FORMAT_ERROR, "时间格式不合法！");
                    }
                    // 查询会议创建结果是否冲突或失败
                    appraisal.setRoomNumber(currentAppraisal.getRoomNumber());
                    HashMap<String, Object> meetingResult = this.createMeeting(appraisal);
                    this.checkMeetingValidity(meetingResult);
                    // 设置评审更新人
                    appraisal.setUpdateBy(currentUserId.toString());
                    appraisalMapper.updateAppraisal(appraisal);

                    // 将其设置为评审中状态
                    currentAppraisal.setStatus(2);
                }
                break;
            // 如果是选择完成评审
            case 3:
                if (!currentAppraisal.getStatus().equals(APPRAISAL_STATUS_DURING_APPRAISAL)) {
                    return R.error(ErrorCodeConstants.OPERATION_NOT_ALLOWED, "只允许完成评审中的评审！");
                } else {
                    // 检查是否有意见未处理
                    commentMapper.selectCommentsByAppraisalId(appraisal.getAppraisalId()).parallelStream().forEach(comment -> {
                        if (comment.getHandleStatus() == 3) {
                            throw new BusinessException(ErrorCodeConstants.OPERATION_NOT_ALLOWED, "还有再议的意见没处理！！");
                        }
                    });
                    // 设置评审更新人
                    appraisal.setUpdateBy(currentUserId.toString());
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    appraisal.setHandleTime(df.format(new Date()));
                    appraisalMapper.updateAppraisal(appraisal);

                    // 将其设置为完成评审状态
                    currentAppraisal.setStatus(3);
                }
                break;
            // 除此之外代表评审状态不对
            default:
                return R.error(ErrorCodeConstants.OPERATION_NOT_ALLOWED, "评审状态不对！评审流程只允许开始未评审的评审与完成评审中的评审。");
        }

        // 发给mq处理邮件模块
        sendToMQ(currentAppraisal);
        return R.ok("评审流程更新成功！");
    }

    /**
     * 创建会议
     *
     * @param appraisal 评审信息
     * @return HashMap创建结果
     * key：MEETING_CREATION_RESULT 表示创建结果: 0-创建失败(MEETING_CREATION_FAILED) 1-创建成功(MEETING_CREATION_SUCCEED) 2-会议时间冲突(MEETING_CREATION_CONFLICTED)
     * 若key：MEETING_CREATION_RESULT 为 2 (MEETING_CREATION_CONFLICTED)，则另有一key CONFLICTED_MEETING 返回冲突的会议地点和时间
     */
    private HashMap<String, Object> createMeeting(Appraisal appraisal) {
        HashMap<String, Object> result = new HashMap<>();
        // 设置默认成功
        result.put(MEETING_CREATION_RESULT, MEETING_CREATION_SUCCEED);
        Date meetingBeginTime = DateUtils.stringToDate(appraisal.getBeginTime(), "yyyy-MM-dd HH:mm:ss");
        Date meetingEndTime = DateUtils.stringToDate(appraisal.getEndTime(), "yyyy-MM-dd HH:mm:ss");
        // 判断会议结束时间是否在会议开始时间之前
        if (meetingEndTime.getTime() - meetingBeginTime.getTime() <= 0) {
            result.put(MEETING_CREATION_RESULT, MEETING_TIME_INVALID);
            return result;
        }

        List<Meeting> futureMeetings = meetingMapper.getFutureMeetings(appraisal);
        Meeting isMeetingConflict = this.isMeetingConflict(futureMeetings, meetingBeginTime, meetingEndTime);
        // 如果会议时间冲突
        if (null != isMeetingConflict) {
            result.put(MEETING_CREATION_RESULT, MEETING_CREATION_CONFLICTED);
            result.put(CONFLICTED_MEETING, isMeetingConflict);
            return result;
        }
        // 根据评审信息创建会议信息
        Meeting meeting = this.initialiseMeeting(appraisal);
        // 如果当前会议不存在，创建会议；会议存在，更新会议
        Meeting meetingExists = meetingMapper.selectMeetingByAppraisalId(appraisal.getAppraisalId());
        if (null == meetingExists) {
            result.put(MEETING_CREATION_RESULT, meetingMapper.createMeeting(meeting));
        } else {
            meeting.setMeetingId(meetingExists.getMeetingId());
            result.put(MEETING_CREATION_RESULT, meetingMapper.updateMeeting(meeting));
        }

        return result;
    }

    /**
     * 判定会议时间与已有会议时间是否冲突
     *
     * @param futureMeetings   未来已安排的会议列表
     * @param meetingBeginTime 会议开始时间
     * @param meetingEndTime   会议结束时间
     * @return 是否冲突。冲突则返回冲突的Meeting对象，没有冲突则返回null
     */
    private Meeting isMeetingConflict(List<Meeting> futureMeetings, Date meetingBeginTime, Date meetingEndTime) {
        for (Meeting futureMeeting : futureMeetings) {
            Date futureBeginTime = DateUtils.stringToDate(futureMeeting.getBeginTime(), "yyyy-MM-dd HH:mm:ss");
            Date futureEndTime = DateUtils.stringToDate(futureMeeting.getEndTime(), "yyyy-MM-dd HH:mm:ss");
            if (DateUtils.isEffectiveDate(meetingBeginTime, futureBeginTime, futureEndTime) ||
                    DateUtils.isEffectiveDate(meetingEndTime, futureBeginTime, futureEndTime)) {
                return futureMeeting;
            }
        }
        return null;
    }

    /**
     * 检查会议创建结果
     *
     * @param meetingResult 会议创建结果
     */
    private void checkMeetingValidity(HashMap<String, Object> meetingResult) {
        if (meetingResult.get(MEETING_CREATION_RESULT).equals(MEETING_CREATION_FAILED)) {
            throw new BusinessException(ErrorCodeConstants.DATABASE_OPERATION_FAILED, "会议创建失败！");
        } else if (meetingResult.get(MEETING_CREATION_RESULT).equals(MEETING_TIME_INVALID)) {
            throw new BusinessException(ErrorCodeConstants.PARAMETER_FORMAT_ERROR, "会议时间不对！");
        } else if (meetingResult.get(MEETING_CREATION_RESULT).equals(MEETING_CREATION_CONFLICTED)) {
            // 创建会议时间冲突返回内容
            Meeting conflictedMeeting = (Meeting) meetingResult.get(CONFLICTED_MEETING);
            String meetingRoom = "会议室";
            switch (conflictedMeeting.getRoomNumber()) {
                case 0:
                    meetingRoom = "大会议室";
                    break;
                case 1:
                    meetingRoom = "小会议室";
                    break;
                default:
                    break;
            }
            throw new BusinessException(ErrorCodeConstants.RESOURCE_UNAVAILABLE, "会议时间冲突！" + meetingRoom
                    + "已在" + conflictedMeeting.getBeginTime()
                    + " 到 " + conflictedMeeting.getEndTime() + "期间被占用");
        }
    }

    /**
     * 根据评审信息填充会议信息
     *
     * @param appraisal 评审信息
     * @return 会议信息
     */
    private Meeting initialiseMeeting(Appraisal appraisal) {
        Meeting meeting = new Meeting();
        meeting.setRoomNumber(appraisal.getRoomNumber());
        meeting.setBeginTime(appraisal.getBeginTime());
        meeting.setEndTime(appraisal.getEndTime());
        // TODO: 会议室状态如何变更
        meeting.setStatus(0);
        meeting.setCreateBy(appraisal.getCreateBy());
        meeting.setProjectId(appraisal.getAppraisalId());
        meeting.setRemark(appraisal.getRemark());
        return meeting;
    }

    /**
     * 检查时间是否符合规定格式
     *
     * @param string 时间字符串
     * @return 结果
     */
    private Date isValidTime(String string) {
        if (null == string) {
            return null;
        }
        try {
            return DateUtils.parseDate(string, "yyyy-MM-dd HH:mm:ss");
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 检查edit方法的必要参数是否为空
     *
     * @param appraisal 评审参数
     */
    private void isEditEmpty(Appraisal appraisal) {
        if (null == appraisal.getAppraisalId()) {
            throw new BusinessException(ErrorCodeConstants.PARSE_PARAMETER_ERROR, "评审id不能为空！");
        }
        if (null == appraisal.getAppraisalTitle() || "".equals(appraisal.getAppraisalTitle())) {
            throw new BusinessException(ErrorCodeConstants.PARSE_PARAMETER_ERROR, "评审标题不能为空！");
        }
        if (null == appraisal.getAppraisalDesc() || "".equals(appraisal.getAppraisalDesc())) {
            throw new BusinessException(ErrorCodeConstants.PARSE_PARAMETER_ERROR, "评审描述不能为空！");
        }
        if (null == appraisal.getBeginTime() || null == appraisal.getEndTime() || "".equals(appraisal.getBeginTime()) || "".equals(appraisal.getEndTime())) {
            throw new BusinessException(ErrorCodeConstants.PARSE_PARAMETER_ERROR, "会议时间不能为空！");
        }
        if (null == appraisal.getRoomNumber()) {
            throw new BusinessException(ErrorCodeConstants.PARSE_PARAMETER_ERROR, "会议地点不能为空！");
        }
    }

    /**
     * 检查process方法的必要参数是否为空
     *
     * @param appraisal 评审参数
     */
    private void isProcessEmpty(Appraisal appraisal) {
        if (null == appraisal.getAppraisalId()) {
            throw new BusinessException(ErrorCodeConstants.PARSE_PARAMETER_ERROR, "评审id不能为空！");
        }
        if (null == appraisal.getStatus()) {
            throw new BusinessException(ErrorCodeConstants.PARSE_PARAMETER_ERROR, "评审状态不能为空！");
        }
        if (appraisal.getStatus() == 2) {
            if (null == appraisal.getBeginTime()) {
                throw new BusinessException(ErrorCodeConstants.PARSE_PARAMETER_ERROR, "评审开始时间不能为空！");
            }
            if (null == appraisal.getEndTime()) {
                throw new BusinessException(ErrorCodeConstants.PARSE_PARAMETER_ERROR, "评审结束时间不能为空！");
            }
        }
    }

    /**
     * 通知mq
     * @param appraisal
     */
    private void sendToMQ(Appraisal appraisal){
        // 邮件发送成功与否不影响评审流程
        try {
            rocketMQTemplate.sendOneWay(topic, appraisal);
        } catch (MessagingException e) {
            LOGGER.error("发送消息到MQ异常, {}", e.getMessage());
        }
    }
}

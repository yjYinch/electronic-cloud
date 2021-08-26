package com.sedwt.workflow.service.impl;

import com.sedwt.common.constant.ErrorCodeConstants;
import com.sedwt.common.entity.R;
import com.sedwt.workflow.domain.Appraisal;
import com.sedwt.workflow.domain.Comment;
import com.sedwt.workflow.mapper.AppraisalMapper;
import com.sedwt.workflow.mapper.CommentMapper;
import com.sedwt.workflow.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author px
 * @create 2021-06-29 16:44
 */
@Service
public class CommentServiceImpl implements CommentService {

    private static final Integer COMMENT_STATUS_TOBE_HANDLED = 0;
    private static final Integer COMMENT_STATUS_ACCEPTED = 1;
    private static final Integer COMMENT_STATUS_UNACCEPTED = 2;
    private static final Integer COMMENT_STATUS_TOBE_DISCUSSED = 3;
    private static final Integer APPRAISAL_STATUS_NOT_SUBMITTED = 0;
    private static final Integer APPRAISAL_STATUS_PRE_APPRAISAL = 1;
    private static final Integer APPRAISAL_STATUS_DURING_APPRAISAL = 2;
    private static final Integer APPRAISAL_STATUS_COMPLETED = 3;
    private static final Integer APPRAISAL_STATUS_CANCELED = 4;


    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private AppraisalMapper appraisalMapper;

    /**
     * 提交评审评论
     *
     * @param comment 评审意见详情
     * @return 处理结果
     */
    @Override
    public R addComment(Comment comment) {
        Appraisal appraisal = appraisalMapper.selectAppraisalById(comment.getAppraisalId(), null);
        if (null == appraisal){
            return R.error(ErrorCodeConstants.RESOURCE_NOT_FOUND, "评审不存在！");
        }
        if (comment.getQuestionDesc() == null || "".equals(comment.getQuestionDesc())) {
            return R.error(ErrorCodeConstants.PARSE_PARAMETER_ERROR, "问题内容不能为空！");
        }
        if (comment.getQuestionLocate() == null || "".equals(comment.getQuestionLocate())) {
            return R.error(ErrorCodeConstants.PARSE_PARAMETER_ERROR, "问题位置不能为空！");
        }

        // 如果这是当前用户提出的第一个意见，增加参与评审的人数
        if (commentMapper.countCommentsByUserId(comment) <= 0 && !appraisal.getCreateBy().equals(String.valueOf(comment.getUserId()))){
            if(appraisalMapper.increaseHandledPersons(appraisal) <= 0){
                // 如果更新评审人数出现冲突，循环更新
                this.updateUntilSuccess(appraisal);
            }
        }

        commentMapper.addComment(comment);
        return R.ok("提交评论成功！");
    }

    /**
     * 评审意见处理
     * @param comment 评审意见详情
     * @return 处理结果
     */
    @Override
    public R handleComment(Comment comment) {
        Appraisal appraisal = appraisalMapper.selectAppraisalById(comment.getAppraisalId(), null);
        if (null == appraisal){
            return R.error(ErrorCodeConstants.RESOURCE_NOT_FOUND, "评审不存在！");
        }
        // 检查评审是否处于可以处理意见的状态（待评审、评审中）
        if (!appraisal.getStatus().equals(APPRAISAL_STATUS_PRE_APPRAISAL) &&
                !appraisal.getStatus().equals(APPRAISAL_STATUS_DURING_APPRAISAL)){
            switch (appraisal.getStatus()){
                case 0:
                    return R.error(ErrorCodeConstants.OPERATION_NOT_ALLOWED, "该评审未提交！");
                case 3:
                    return R.error(ErrorCodeConstants.OPERATION_NOT_ALLOWED, "已完成的评审无法处理意见！");
                case 4:
                    return R.error(ErrorCodeConstants.OPERATION_NOT_ALLOWED, "已撤销的评审无法处理意见！");
                default:
                    return R.error(ErrorCodeConstants.RESOURCE_UNAVAILABLE, "评审状态错误！");
            }
        }
        Comment currentComment = commentMapper.selectCommentById(comment);
        if (null == currentComment){
            return R.error(ErrorCodeConstants.RESOURCE_NOT_FOUND, "评审意见不存在！");
        }

        // 检查评审意见是否可以处理（待处理、再议）
        if(currentComment.getHandleStatus().equals(COMMENT_STATUS_TOBE_HANDLED) ||
                currentComment.getHandleStatus().equals(COMMENT_STATUS_TOBE_DISCUSSED)){
            // 检查评审意见状态是否合法（接受、不接受、再议）
            if(comment.getHandleStatus().equals(COMMENT_STATUS_ACCEPTED) ||
                    comment.getHandleStatus().equals(COMMENT_STATUS_UNACCEPTED) ||
                    comment.getHandleStatus().equals(COMMENT_STATUS_TOBE_DISCUSSED)){
                // 处理评审意见
                commentMapper.processComment(comment);
            }else{
                return R.error(ErrorCodeConstants.PARAMETER_FORMAT_ERROR, "提交的评审意见状态不对！");
            }
        }else{
            return R.error(ErrorCodeConstants.OPERATION_NOT_ALLOWED, "处理过的评审无法再处理！");
        }
        return R.ok("评审意见处理成功！");
    }

    /**
     * 更新失败冲突的处理
     * @param appraisal 评审信息
     */
    private void updateUntilSuccess(Appraisal appraisal){
        if(appraisalMapper.increaseHandledPersons(appraisal) == 0){
            updateUntilSuccess(appraisal);
        }
    }
}

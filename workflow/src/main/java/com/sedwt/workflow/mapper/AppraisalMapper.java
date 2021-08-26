package com.sedwt.workflow.mapper;


import com.sedwt.common.entity.sys.SysUser;
import com.sedwt.workflow.domain.Appraisal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AppraisalMapper {

    /**
     * 获取评审信息
     * @param loginUserId 当前用户id
     * @param appraisal 评审信息
     * @return 分页列表
     */
    List<Appraisal> selectAppraisalList(@Param("appraisal") Appraisal appraisal, @Param("loginUserId") Long loginUserId);

    /**
     * 根据id获取评审信息
     * @param appraisalId 评审id
     * @return 评审信息
     */
    Appraisal selectAppraisalById(@Param("appraisalId") Long appraisalId, @Param("loginUserId") Long loginUserId);

    /**
     * 根据评审id获取当前组织者的用户信息
     * @param userId
     * @return
     */
    String selectEmailById(long userId);

    /**
     * 更新评审信息
     * @param appraisal 评审信息
     * @return 更新结果
     */
    Integer updateAppraisal(Appraisal appraisal);

    /**
     * 更新参与评审的人数
     * @param appraisal 评审信息
     * @return 更新结果
     */
    Integer increaseHandledPersons(Appraisal appraisal);

    /**
     * 根据id撤销评审
     * @param appraisal 评审信息
     * @return 撤销结果
     */
    Integer cancelAppraisalById(Appraisal appraisal);

    /**
     * 根据评审id获取参与评审的用户列表
     * @param appraisalId 评审id
     * @return 参与评审用户列表
     */
    List<SysUser> selectParticipantsByAppraisalId(Long appraisalId);

    /**
     * 将评审参与人员加入评审人员表
     * @param appraisalId 评审id
     * @param userId 参与评审人员id
     * @return 新增结果
     */
    Integer addAppraisalParticipant(@Param("appraisalId") Long appraisalId, @Param("userId") Long userId);

    /**
     * 将评审创建人也加入评审用户表
     * @param appraisalId 评审id
     * @param ownerId 评审创建人id
     * @return 新增结果
     */
    Integer createAppraisalOwner(@Param("appraisalId") Long appraisalId, @Param("ownerId") Long ownerId);

    /**
     * 根据评审id删除评审人员表
     * @param appraisalId 评审id
     * @return 删除结果
     */
    Integer deleteAppraisalParticipants(Long appraisalId);

    /**
     * 根据评审id删除评审
     * @param appraisalId 评审id
     * @return 删除结果
     */
    Integer deleteAppraisalById(Long appraisalId);

    /**
     * 创建空白评审表单
     * xml中配置的useGeneratedKeys和keyProperty用以在新增后返回id，填充在AppraisalEntity参数中
     * @param appraisal 评审信息
     * @return 创建结果
     */
    Integer addAppraisal(Appraisal appraisal);

    Integer countAppraisalTitle(String appraisalTitle);
}

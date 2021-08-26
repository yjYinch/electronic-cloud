package com.sedwt.workflow.service;

import com.sedwt.common.entity.R;
import com.sedwt.workflow.domain.Appraisal;

import java.util.List;

public interface AppraisalService {

    /**
     * 根据条件分页查询评审列表
     * @param appraisal 评审信息
     * @param currentUserId 当前用户ID
     * @return 分页列表
     */
    List<Appraisal> selectAppraisalList(Appraisal appraisal, Long currentUserId);

    /**
     * 根据评审id查询评审
     * @param id 评审id
     * @return 评审对象信息
     */
    R selectAppraisalById(Long id, Long currentUserId);

    /**
     * 根据id关闭评审
     * @param appraisal 评审信息
     * @return 关闭结果
     */
    R cancelAppraisalById(Appraisal appraisal);

    /**
     * 根据评审id删除评审
     * @param id 评审id
     * @return 删除结果
     */
    R deleteById(Long id);

    /**
     * 新增评审
     * 用来在点击创建评审后，创建一个空白评审单，并返回数据库自生成的id
     * @param currentUserId 当前用户id（评审创建人id）
     * @return 新增评审的id
     */
    R addAppraisal(Long currentUserId);

    /**
     * 创建/更新评审
     * 即是创建评审页面点击提交的结果，并非创建新数据，而是根据“新增评审”返回的id在数据库中进行修改
     * 或者更新评审
     * @param appraisal 评审信息
     * @param currentUserId 当前用户的登录id（评审创建人）
     * @return 创建结果
     */
    R editAppraisal(Appraisal appraisal, Long currentUserId);

    /**
     * 更新评审流程状态
     * 0-未提交1-待评审2-评审中3-已完成4-已撤销5-已关闭
     * @param appraisal 评审信息。status为 2 代表开始评审；status为 3 代表完成评审。
     * @param currentUserId 当前用户id
     * @return 更新结果
     */
    R processAppraisal(Appraisal appraisal, Long currentUserId);
}

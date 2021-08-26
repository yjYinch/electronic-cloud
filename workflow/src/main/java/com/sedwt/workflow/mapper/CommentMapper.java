package com.sedwt.workflow.mapper;

import com.sedwt.workflow.domain.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    /**
     * 提交评审评论
     * @param comment 评审意见
     * @return 添加结果
     */
    Integer addComment(Comment comment);

    /**
     * 删除评审意见
     * @param appraisalId 评审id
     * @return 删除结果
     */
    Integer deleteAppraisalComments(Long appraisalId);

    /**
     * 获取评审意见详情
     * @param comment 评审意见信息
     * @return 评审意见
     */
    Comment selectCommentById(Comment comment);

    /**
     * 根据评审id获取评审意见列表
     * @param appraisalId 评审id
     * @return 评审意见列表
     */
    List<Comment> selectCommentsByAppraisalId(Long appraisalId);

    Integer countCommentsByUserId(Comment comment);
    /**
     * 处理评审意见
     * @param comment 评审意见信息
     * @return 删除结果
     */
    Integer processComment(Comment comment);
}

package com.sedwt.workflow.service;


import com.sedwt.common.entity.R;
import com.sedwt.workflow.domain.Comment;

/**
 * @author px
 * @create 2021-06-29 16:43
 */

public interface CommentService {

    /**
     * 提交评论
     * @param comment
     * @return
     */
    R addComment(Comment comment);

    /**
     * 评审意见处理
     * @param comment 评审意见详情
     * @return 处理结果
     */
    R handleComment(Comment comment);
}

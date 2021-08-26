package com.sedwt.workflow.controller;

import com.sedwt.common.entity.R;
import com.sedwt.common.utils.ServletUtils;
import com.sedwt.workflow.domain.Comment;
import com.sedwt.workflow.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author px
 * @create 2021-06-29 16:37
 */

@RestController
@RequestMapping("comment")
@Slf4j
public class CommentController {
    @Autowired
    private CommentService commentService;

    /**
     * 提交评审评论
     *
     * @param comment 评审意见详情
     * @return 评论的结果
     */
    @PostMapping("/create")
    public R create(@RequestBody Comment comment) {
        comment.setUserId(ServletUtils.getCurrentUserId());
        comment.setCreateBy(ServletUtils.getCurrentUserId() + "");
        return commentService.addComment(comment);

    }

    /**
     * 评审意见处理
     * @param comment 评审意见详情
     * @return 处理结果
     */
    @PostMapping("/handle")
    public R handle(@RequestBody Comment comment){
        return commentService.handleComment(comment);
    }

}

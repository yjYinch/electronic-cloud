package com.sedwt.workflow.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;

/**
 * @author : yj zhang
 * @since : 2021/8/18 15:06
 */
@Setter
@Getter
@Validated
public class MailMessage {
    /**
     * 邮件主题
     */
    private String subject;

    /**
     * 邮件内容
     */
    private String content;

    /**
     * 发送人
     */
    @Email(message = "email格式错误")
    private String fromEmail;

    /**
     * 抄送人，数组形式
     */
    private String[] ccEmails;

    /**
     * 收件人，数组形式
     */
    private String[] toEmails;
}

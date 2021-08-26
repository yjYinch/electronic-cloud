package com.sedwt.workflow.service;

import com.sedwt.workflow.domain.Appraisal;
import com.sedwt.workflow.domain.MailMessage;

/**
 * @author : yj zhang
 * @since : 2021/8/18 15:18
 */
public interface IMail {
    /**
     * 发送邮件
     * @param mailMessage
     */
    void sendEmail(MailMessage mailMessage);

    /**
     * 封装mail消息
     * @param appraisal
     * @return
     */
    MailMessage packMail(Appraisal appraisal);
}

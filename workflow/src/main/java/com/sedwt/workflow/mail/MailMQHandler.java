package com.sedwt.workflow.mail;

import com.alibaba.fastjson.JSON;
import com.sedwt.common.redis.RedisUtils;
import com.sedwt.workflow.domain.Appraisal;
import com.sedwt.workflow.domain.MailMessage;
import com.sedwt.workflow.service.impl.MailServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author : yj zhang
 * @since : 2021/8/18 15:50
 */
@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "${mq.group}", topic = "${mq.topic}")
public class MailMQHandler implements RocketMQListener<MessageExt> {
    @Autowired
    private MailServiceImpl mailService;

    @Override
    public void onMessage(MessageExt message) {
        log.info("接收到MQ消息，{}", message);
        String msgId = message.getMsgId();
        // 幂等性保证，防止重复发送邮件
        if (RedisUtils.get(msgId) != null) {
            log.info("消息[{}]重复", msgId);
            return;
        }
        try {
            Appraisal appraisal = JSON.parseObject(new String(message.getBody()), Appraisal.class);
            // 封装邮件内容到MailMessage
            MailMessage mailMessage = mailService.packMail(appraisal);
            mailService.sendEmail(mailMessage);
            RedisUtils.set(msgId, msgId);
        } catch (Exception e) {
            log.error("邮件发送异常", e);
        }
    }
}

package com.sedwt.workflow.service.impl;

import com.sedwt.common.entity.sys.SysUser;
import com.sedwt.workflow.domain.Appraisal;
import com.sedwt.workflow.domain.AppraisalStatusEnum;
import com.sedwt.workflow.domain.MailMessage;
import com.sedwt.workflow.domain.MailServerProperties;
import com.sedwt.workflow.domain.RoomEnum;
import com.sedwt.workflow.mail.MailFormat;
import com.sedwt.workflow.service.IMail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : yj zhang
 * @since : 2021/8/18 15:19
 */
@Service
public class MailServiceImpl implements IMail {
    private static final Logger LOGGER = LoggerFactory.getLogger(MailServiceImpl.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${ip.addr}")
    private String addr;

    /**
     * 发送邮件
     *
     * @param mailMessage
     */
    @Override
    public void sendEmail(MailMessage mailMessage) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            // 邮件主题
            mimeMessageHelper.setSubject(mailMessage.getSubject());
            // 邮件内容
            mimeMessageHelper.setText(mailMessage.getContent(), true);
            // 发件人email
            mimeMessageHelper.setFrom(mailMessage.getFromEmail());
            // 抄送人email
            if (mailMessage.getCcEmails() != null) {
                mimeMessageHelper.setCc(mailMessage.getCcEmails());
            }
            // 接收人email
            mimeMessageHelper.setTo(mailMessage.getToEmails());
            LOGGER.info("即将发送邮件，邮件内容===>{}", mailMessage);
            this.doSend(mimeMessage);
        } catch (MessagingException e) {
            LOGGER.error("发送邮件异常, {}", e.getMessage());
        }
    }

    /**
     * 邮件发送
     *
     * @param mimeMessage
     */
    private void doSend(MimeMessage mimeMessage) {
        mailSender.send(mimeMessage);
    }

    /**
     * 封装邮件信息内容
     * @param appraisal
     * @return
     */
    @Override
    public MailMessage packMail(Appraisal appraisal) {
        try {
            MailMessage mailMessage = new MailMessage();
            // 1. 邮件主题， 以评审标题为主题
            String title = appraisal.getAppraisalTitle();
            mailMessage.setSubject(title);

            // 2. 邮件发送人
            mailMessage.setFromEmail("njrd@sedwt.com.cn");

            // 3. 邮件接收人
            List<SysUser> participants = appraisal.getParticipants();
            if (!CollectionUtils.isEmpty(participants)) {
                List<String> emails = participants.stream().map(SysUser::getEmail).collect(Collectors.toList());
                mailMessage.setToEmails(emails.toArray(new String[0]));
            }

            // 4. 邮件内容填写
            // 参与人名单
            List<String> userNames = participants.stream().map(SysUser::getUserName).collect(Collectors.toList());
            String user = userNames.toString().replaceAll("\\[", "")
                                              .replaceAll("]", "");

            // 会议地点
            String roomAddress = appraisal.getRoomNumber() == RoomEnum.WIDE_ROOM.getVal() ?
                    RoomEnum.WIDE_ROOM.getDesc() : RoomEnum.SMALL_ROOM.getDesc();
            // 状态
            String statusDesc = AppraisalStatusEnum.getDescByVal(appraisal.getStatus());
            // 内容
            String appraisalDesc = appraisal.getAppraisalDesc();
            // 邮件的url
            String url = assembleURL(appraisal.getAppraisalId().intValue());
            // 邮箱的模板内容
            String content = MailFormat.formatHtml(title, appraisal.getBeginTime(), appraisal.getEndTime(), user,
                    roomAddress, statusDesc, appraisalDesc, url);
            mailMessage.setContent(content);

            return mailMessage;
        } catch (Exception e) {
            LOGGER.error("封装邮件内容异常", e);
            return null;
        }
    }

    /**
     * 根据评审id拼接对应的URL, 比如：http://10.11.2.183:8000/review/preView?id=123
     * @param appraisalId
     * @return
     */
    public String assembleURL(int appraisalId) {
        return "http://" + addr + "/review/preView?id=" + appraisalId;
    }
}

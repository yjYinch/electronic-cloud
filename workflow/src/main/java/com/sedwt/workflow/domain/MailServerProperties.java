package com.sedwt.workflow.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author : yj zhang
 * @since : 2021/8/19 15:05
 */
@Setter
@Getter
@Component
public class MailServerProperties {
    @Value("spring.mail.username")
    private String mailAddress;
}

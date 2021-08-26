package com.sedwt.workflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * @author : yj zhang
 * @since : 2021/8/24 14:56
 */
@SpringBootApplication(scanBasePackages = {"com.sedwt"})
public class WorkflowMain {
    /**
     * 手动注册RestTemplate，否则@Autowired注入失败
     * @param builder
     * @return
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    public static void main(String[] args) {
        SpringApplication.run(WorkflowMain.class, args);
    }
}

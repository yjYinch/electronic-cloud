package com.sedwt.common.aspect;

import com.sedwt.common.annotation.RemoteUrl;
import com.sedwt.common.constant.Constants;
import com.sedwt.common.constant.ErrorCodeConstants;
import com.sedwt.common.exception.URLException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;



/**
 * @author : yj zhang
 * @since : 2021/8/26 9:59
 */
@Aspect
@Component
@Slf4j
public class DestinationAspect {
    @Autowired
    private RestTemplate restTemplate;

    @Around("@annotation(com.sedwt.common.annotation.RemoteUrl)")
    public Object sendRemoteRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        RemoteUrl remoteUrlAnnotation = method.getAnnotation(RemoteUrl.class);
        if (remoteUrlAnnotation == null) {
            return joinPoint.proceed();
        }
        // 获取url
        String url = remoteUrlAnnotation.value();
        // 请求类型，POST该是GET
        String requestType = remoteUrlAnnotation.requestType();
        // 校验url
        if (StringUtils.isBlank(url) || !url.startsWith("/")) {
            throw new URLException("@RemoteUrl注解的value值url不符合要求", ErrorCodeConstants.PARSE_PARAMETER_ERROR);
        }
        // 校验请求类型
        if (Constants.POST.equals(requestType) || Constants.GET.equals(requestType)) {
            throw new URLException("错误的请求方式，请求类型不是POST请求或GET请求", ErrorCodeConstants.METHOD_NOT_ALLOWED);
        }

        // 获取方法的参数
        Object[] args = joinPoint.getArgs();
        return null;
    }
}

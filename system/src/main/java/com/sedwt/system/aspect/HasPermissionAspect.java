package com.sedwt.system.aspect;

import com.sedwt.common.annotation.HasPermissions;
import com.sedwt.common.constant.Constants;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @author : yj zhang
 * @since : 2021/8/24 15:15
 */
@Aspect
@Component
public class HasPermissionAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(HasPermissionAspect.class);

    /**
     * com.sedwt.system.controller包及其子包的所有类定义为切入点
     */
    @Pointcut("execution(public * com.sedwt.system.controller..*.*(..))")
    public void authenticatePoint(){
    }

    @Before("authenticatePoint()")
    public void authenticate(JoinPoint joinPoint) throws Exception {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        HasPermissions hasPermissions = method.getAnnotation(HasPermissions.class);
        if (hasPermissions == null) {
            return;
        }
        // 获取request请求对象
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (requestAttributes == null) {
            throw new Exception("请求对象为空");
        }
        HttpServletRequest request = requestAttributes.getRequest();

        // method2: 根据token也可以

        // 网关层封装好，userId
        String userId = request.getHeader(Constants.CURRENT_ID);
        if (StringUtils.isBlank(userId)) {
            // 抛异常 还是返回实体信息？
        }

    }
}

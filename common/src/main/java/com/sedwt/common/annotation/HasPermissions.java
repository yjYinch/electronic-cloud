package com.sedwt.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <br>
 * 权限校验注解，具体实现见{@link}
 * </br>
 *
 *
 * @author : yj zhang
 * @since : 2021/8/24 15:08
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface HasPermissions {
    String value() default "";
}

package com.sedwt.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhang yijun
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RemoteUrl {
    /**
     * url路径，
     * 比如，http://x.x.x.x:3000/path/path2
     * 则value=/path/path2
     *
     * @return value
     */
    String value();

    /**
     * 请求类型，GET请求/POST请求
     *
     * @return requestType
     */
    String requestType();
}

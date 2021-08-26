package com.sedwt.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author : yj zhang
 * @since : 2021/8/26 10:21
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class URLException extends RuntimeException {
    protected final String code;
    protected final String message;

    public URLException(String code, String message) {
        this.code = code;
        this.message = message;
    }
}

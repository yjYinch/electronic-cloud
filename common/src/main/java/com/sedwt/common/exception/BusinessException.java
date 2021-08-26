package com.sedwt.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务异常
 * 
 * @author sedwt
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BusinessException extends RuntimeException {
	private static final long serialVersionUID = 1L;

    protected final String code;

	protected final String message;

    public BusinessException(String code, String message) {
        this.code = code;
        this.message = message;
	}

    public BusinessException(String message, String code, Throwable e) {
		super(message, e);
		this.message = message;
        this.code = code;
	}
}

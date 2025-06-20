package com.star.swiftcommon.exception;

import lombok.Getter;

/**
 * 业务异常
 *
 * @author SHOOTING_STAR_C
 */
@Getter
public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

}

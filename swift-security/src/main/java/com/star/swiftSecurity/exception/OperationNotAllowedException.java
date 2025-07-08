package com.star.swiftSecurity.exception;

import lombok.Getter;

/**
 * 不允许操作
 *
 * @author SHOOTING_STAR_C
 */
@Getter
public class OperationNotAllowedException extends RuntimeException {
    public OperationNotAllowedException(String message) {
        super(message);
    }
}

package com.star.swiftSecurity.exception;

import lombok.Getter;

/**
 * 重复用户异常
 *
 * @author SHOOTING_STAR_C
 */
@Getter
public class DuplicateEntityException extends RuntimeException {
    public DuplicateEntityException(String message) {
        super(message);
    }
}

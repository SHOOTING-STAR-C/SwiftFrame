package com.star.swiftSecurity.exception;

/**
 * 无效的令牌
 *
 * @author SHOOTING_STAR_C
 */
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}

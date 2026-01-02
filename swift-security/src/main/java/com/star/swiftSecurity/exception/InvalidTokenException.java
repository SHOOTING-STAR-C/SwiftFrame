package com.star.swiftSecurity.exception;

import com.star.swiftSecurity.constant.TokenReCode;

/**
 * 无效的令牌
 *
 * @author SHOOTING_STAR_C
 */
public class InvalidTokenException extends RuntimeException {
    private final TokenReCode tokenReCode;

    public InvalidTokenException(TokenReCode tokenReCode) {
        super(tokenReCode.getMessage());
        this.tokenReCode = tokenReCode;
    }

    public InvalidTokenException(TokenReCode tokenReCode, String message) {
        super(message);
        this.tokenReCode = tokenReCode;
    }

    public TokenReCode getTokenReCode() {
        return tokenReCode;
    }
}

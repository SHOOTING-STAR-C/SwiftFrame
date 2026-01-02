package com.star.swiftSecurity.constant;

import lombok.Getter;

/**
 * Token相关状态码
 *
 * @author SHOOTING_STAR_C
 */
@Getter
public enum TokenReCode {
    // Token相关业务异常 (TOK-4XX-XX)
    TOKEN_NOT_FOUND("TOK-404-01", "TOKEN失效"),
    TOKEN_EXPIRED("TOK-401-01", "Token已过期"),
    TOKEN_INVALID("TOK-401-02", "Token无效");
    private final String code;
    private final String message;

    TokenReCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}

package com.star.swiftSecurity.constant;

import lombok.Getter;

/**
 * Token相关状态码
 *
 * @author SHOOTING_STAR_C
 */
@Getter
public enum TokenReCode {
    // 用户相关业务异常 (UER-4XX-01)
    TOKEN_NOT_FOUND("TOK-404-01", "TOKEN失效");
    private final String code;
    private final String message;

    TokenReCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}

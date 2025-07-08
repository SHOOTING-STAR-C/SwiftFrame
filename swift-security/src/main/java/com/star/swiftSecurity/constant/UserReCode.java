package com.star.swiftSecurity.constant;

import lombok.Getter;

/**
 * 用户相关返回状态码
 *
 * @author SHOOTING_STAR_C
 */
@Getter
public enum UserReCode {

    // 用户相关业务异常 (UER-4XX-01)
    USER_NOT_FOUND("UER-404-01", "用户不存在"),
    USER_DISABLED("UER-403-01", "用户不可用"),
    USER_DUPLICATE("UER-500-05", "用户已存在"),
    ;

    private final String code;
    private final String message;

    UserReCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}

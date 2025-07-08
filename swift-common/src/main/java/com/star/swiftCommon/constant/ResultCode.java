package com.star.swiftCommon.constant;

import lombok.Getter;

/**
 * 状态码枚举（XXX-XXX-XX格式）
 *
 * @author SHOOTING_STAR_C
 */
@Getter
public enum ResultCode {

    // 成功状态码
    SUCCESS("PUB-200-00", "操作成功"),

    // 程序异常 (PUB-5XX-XX)
    INTERNAL_SERVER_ERROR("PUB-500-00", "服务器内部错误"),
    SERVICE_UNAVAILABLE("PUB-503-00", "服务不可用"),
    DATABASE_ERROR("PUB-500-01", "数据库操作异常"),
    ENCRYPT_ERROR("PUB-500-02", "加解密异常"),
    NETWORK_ERROR("PUB-505-00", "网络通信异常"),
    RESOURCE_NOT_FOUND("PUB-404-00", "资源不存在"),

    // 业务异常 (BUS-4XX-XX)
    BAD_REQUEST("BUS-400-00", "请求参数错误"),
    UNAUTHORIZED("BUS-401-00", "未授权访问"),
    FORBIDDEN("BUS-403-00", "禁止访问"),
    NOT_FOUND("BUS-404-00", "资源不存在"),
    METHOD_NOT_ALLOWED("BUS-405-00", "请求方法不允许"),
    BUSINESS_NOT_ALLOWED("BUS-405-00", "业务不允许");


    private final String code;
    private final String message;

    ResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}

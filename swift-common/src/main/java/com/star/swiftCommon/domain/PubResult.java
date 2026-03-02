package com.star.swiftCommon.domain;

import com.star.swiftCommon.constant.ResultCode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 统一返回体
 *
 * @param <T>
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Data
public class PubResult<T> {
    private String code; // 状态码
    private String msg;  // 消息
    private T data;      // 响应数据

    /**
     * 成功响应
     *
     * @param data 响应数据
     */
    public static <T> PubResult<T> success(T data) {
        return new PubResult<>(ResultCode.SUCCESS, data);
    }

    /**
     * 成功响应（无数据）
     *
     * @return PubResult
     */
    public static <T> PubResult<T> success() {
        return new PubResult<>(ResultCode.SUCCESS);
    }

    /**
     * 失败响应（使用默认服务器错误码）
     *
     * @return PubResult
     */
    public static <T> PubResult<T> error() {
        return new PubResult<>(ResultCode.INTERNAL_SERVER_ERROR);
    }

    /**
     * 失败响应（自定义消息，使用默认服务器错误码）
     *
     * @param msg 错误消息
     * @return PubResult
     */
    public static <T> PubResult<T> error(String msg) {
        return new PubResult<>(ResultCode.INTERNAL_SERVER_ERROR, msg);
    }

    /**
     * 失败响应（自定义错误码和消息）
     *
     * @param code 错误码
     * @param msg  错误消息
     * @return PubResult
     */
    public static <T> PubResult<T> error(String code, String msg) {
        return new PubResult<>(code, msg, null);
    }

    /**
     * 失败响应（使用 ResultCode 枚举）
     *
     * @param resultCode 错误码枚举
     * @param msg        错误消息（可选，为 null 时使用枚举默认消息）
     * @return PubResult
     */
    public static <T> PubResult<T> error(ResultCode resultCode, String msg) {
        return new PubResult<>(resultCode.getCode(), msg != null ? msg : resultCode.getMessage(), null);
    }

    /**
     * 失败响应（使用 ResultCode 枚举）
     *
     * @param resultCode 错误码枚举
     * @return PubResult
     */
    public static <T> PubResult<T> error(ResultCode resultCode) {
        return new PubResult<>(resultCode);
    }

    public PubResult(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public PubResult(ResultCode resultCode, String msg) {
        this.code = resultCode.getCode();
        this.msg = msg;
        this.data = null;
    }

    public PubResult(ResultCode resultCode, T data) {
        this.code = resultCode.getCode();
        this.msg = resultCode.getMessage();
        this.data = data;
    }

    public PubResult(ResultCode resultCode) {
        this.code = resultCode.getCode();
        this.msg = resultCode.getMessage();
        this.data = null;
    }
}
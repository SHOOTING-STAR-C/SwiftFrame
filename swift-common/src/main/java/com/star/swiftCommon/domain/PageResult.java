package com.star.swiftCommon.domain;

import com.star.swiftCommon.constant.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应结果
 *
 * @param <T> 数据类型
 * @author SHOOTING_STAR_C
 */
@Data
@NoArgsConstructor
public class PageResult<T> {
    private String code;      // 状态码
    private String msg;       // 消息
    private List<T> records;  // 数据列表
    private long total;       // 总记录数
    private long current;     // 当前页码
    private long size;        // 每页大小

    /**
     * 成功返回分页数据
     *
     * @param records 数据列表
     * @param total   总记录数
     * @param current 当前页码
     * @param size    每页大小
     * @return PageResult
     */
    public static <T> PageResult<T> success(List<T> records, long total, long current, long size) {
        return new PageResult<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), records, total, current, size);
    }

    /**
     * 成功返回分页数据（默认使用 ResultCode）
     *
     * @param records 数据列表
     * @param total   总记录数
     * @return PageResult
     */
    public static <T> PageResult<T> success(List<T> records, long total) {
        return new PageResult<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), records, total, 1L, records.size());
    }

    /**
     * 失败响应
     *
     * @param code 状态码
     * @param msg  错误消息
     * @return PageResult
     */
    public static <T> PageResult<T> error(String code, String msg) {
        return new PageResult<>(code, msg, null, 0L, 0L, 0L);
    }

    /**
     * 失败响应
     *
     * @param msg 错误消息
     * @return PageResult
     */
    public static <T> PageResult<T> error(String msg) {
        return new PageResult<>(ResultCode.INTERNAL_SERVER_ERROR.getCode(), msg, null, 0L, 0L, 0L);
    }

    public PageResult(String code, String msg, List<T> records, long total, long current, long size) {
        this.code = code;
        this.msg = msg;
        this.records = records;
        this.total = total;
        this.current = current;
        this.size = size;
    }
}

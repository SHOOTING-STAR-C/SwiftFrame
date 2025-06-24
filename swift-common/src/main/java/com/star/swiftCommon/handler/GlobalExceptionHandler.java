package com.star.swiftCommon.handler;

import com.star.swiftCommon.constant.ResultCode;
import com.star.swiftCommon.domain.PubResult;
import com.star.swiftCommon.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常处理
 *
 * @author SHOOTING_STAR_C
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    // 处理业务异常
    @ExceptionHandler(BusinessException.class)
    public PubResult<?> handleBusinessException(BusinessException e) {
        return PubResult.error(ResultCode.BUSINESS_NOT_ALLOWED, e.getMessage());
    }

    // 处理未找到资源异常
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public PubResult<?> handleResException(Exception e) {
        log.error(e.getMessage(), e);
        return PubResult.error(ResultCode.RESOURCE_NOT_FOUND, e.getMessage());
    }

    // 兜底异常处理
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public PubResult<?> handleOtherException(Exception e) {
        log.error(e.getMessage(), e);
        // 生产环境应记录日志而不返回详细错误
        return PubResult.error();
    }
}

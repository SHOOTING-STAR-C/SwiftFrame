package com.star.swiftCommon.handler;

import com.star.swiftCommon.constant.ResultCode;
import com.star.swiftCommon.domain.PubResult;
import com.star.swiftCommon.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;


/**
 * 全局异常处理
 *
 * @author SHOOTING_STAR_C
 */
@Order
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 处理参数校验异常
     *
     * @param e MethodArgumentNotValidException
     * @return PubResult<?>
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public PubResult<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        StringBuilder errorMsg = new StringBuilder("参数验证失败：");
        e.getBindingResult().getFieldErrors().forEach(error -> errorMsg.append(error.getDefaultMessage()).append("；"));
        // 移除最后一个分号
        if (!errorMsg.isEmpty()) {
            errorMsg.deleteCharAt(errorMsg.length() - 1);
        }
        log.error(errorMsg.toString());
        return PubResult.error(ResultCode.BAD_REQUEST, errorMsg.toString());
    }

    /**
     * 处理业务异常
     *
     * @param e BusinessException
     * @return PubResult<?>
     */
    @ExceptionHandler(BusinessException.class)
    public PubResult<?> handleBusinessException(BusinessException e) {
        log.error(e.getMessage(), e);
        return PubResult.error(ResultCode.BUSINESS_NOT_ALLOWED, e.getMessage());
    }

    /**
     * 处理未找到资源异常
     *
     * @param e NoResourceFoundException
     * @return PubResult<?>
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public PubResult<?> handleResException(Exception e) {
        log.error(e.getMessage(), e);
        return PubResult.error(ResultCode.RESOURCE_NOT_FOUND, e.getMessage());
    }

    /**
     * 兜底异常处理
     *
     * @param e Exception
     * @return PubResult<?>
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public PubResult<?> handleOtherException(Exception e) {
        log.error(e.getMessage(), e);
        // 生产环境应记录日志而不返回详细错误
        return PubResult.error();
    }
}

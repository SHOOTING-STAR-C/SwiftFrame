package com.star.swiftencrypt.handler;

import com.star.swiftcommon.constant.ResultCode;
import com.star.swiftcommon.domain.PubResult;
import com.star.swiftcommon.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 拦截全局的加密异常
 *
 * @author SHOOTING_STAR_C
 */
@RestControllerAdvice
@Slf4j
public class EncryptExceptionHandler {
    // 处理加密异常
    @ExceptionHandler(BusinessException.class)
    public PubResult<?> handleBusinessException(BusinessException e) {
        return PubResult.error(ResultCode.ENCRYPT_ERROR, e.getMessage());
    }
}

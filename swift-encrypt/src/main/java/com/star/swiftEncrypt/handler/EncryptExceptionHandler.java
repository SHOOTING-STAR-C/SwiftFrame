package com.star.swiftEncrypt.handler;

import com.star.swiftCommon.constant.ResultCode;
import com.star.swiftCommon.domain.PubResult;
import com.star.swiftEncrypt.exception.CryptoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 拦截全局的加密异常
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class EncryptExceptionHandler {
    /**
     * 处理加密异常
     *
     * @param e CryptoException
     * @return PubResult<?>
     */
    @ExceptionHandler(CryptoException.class)
    public PubResult<?> handleCryptoException(CryptoException e) {
        log.error(e.getMessage(), e);
        return PubResult.error(ResultCode.ENCRYPT_ERROR, e.getMessage());
    }
}

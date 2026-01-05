package com.star.swiftSecurity.handler;

import com.star.swiftCommon.domain.PubResult;
import com.star.swiftCommon.constant.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Security模块全局异常处理器
 * 处理Spring Security相关的异常
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Order(1)
@RestControllerAdvice
public class SecurityExceptionHandler {

    /**
     * 处理授权拒绝异常
     * 用于方法级别的权限检查（@PreAuthorize等）
     *
     * @param e AuthorizationDeniedException
     * @return PubResult<?>
     */
    @ExceptionHandler(AuthorizationDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public PubResult<?> handleAuthorizationDeniedException(AuthorizationDeniedException e) {
        log.error("权限拒绝: {}", e.getMessage());
        return PubResult.error(ResultCode.FORBIDDEN, "权限不足，无法访问该资源");
    }
}

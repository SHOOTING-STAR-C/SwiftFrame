package com.star.swiftSecurity.handler;

import com.star.swiftCommon.domain.PubResult;
import com.star.swiftSecurity.constant.TokenReCode;
import com.star.swiftSecurity.constant.UserReCode;
import com.star.swiftSecurity.exception.BusinessException;
import com.star.swiftSecurity.exception.DuplicateEntityException;
import com.star.swiftSecurity.exception.InvalidTokenException;
import com.star.swiftSecurity.exception.OperationNotAllowedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.security.auth.login.AccountLockedException;

/**
 * 处理用户角色权限相关的异常
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class UserExceptionHandler {
    /**
     * 用户相关异常
     * @param  e InternalAuthenticationServiceException
     * @return PubResult<?>
     */
    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public PubResult<?> handleInternalAuthenticationServiceException(InternalAuthenticationServiceException e) {
        log.error(e.getMessage());
        return PubResult.error(UserReCode.USER_DUPLICATE.getCode(), e.getMessage());
    }

    /**
     * 处理用户异常
     *
     * @param e DuplicateEntityException
     * @return PubResult<?>
     */
    @ExceptionHandler(DuplicateEntityException.class)
    public PubResult<?> handleUserException(DuplicateEntityException e) {
        log.error(e.getMessage());
        return PubResult.error(UserReCode.USER_DUPLICATE.getCode(), e.getMessage());
    }

    /**
     * 处理角色权限异常
     *
     * @param e OperationNotAllowedException
     * @return PubResult<?>
     */
    @ExceptionHandler(OperationNotAllowedException.class)
    public PubResult<?> handleNotAllowedException(OperationNotAllowedException e) {
        log.error(e.getMessage());
        return PubResult.error(UserReCode.USER_NOT_FOUND.getCode(), e.getMessage());
    }

    /**
     * token相关异常
     *
     * @param e InvalidTokenException
     * @return PubResult<?>
     */
    @ExceptionHandler(InvalidTokenException.class)
    public PubResult<?> handleInvalidTokenException(InvalidTokenException e) {
        log.error(e.getMessage());
        return PubResult.error(TokenReCode.TOKEN_NOT_FOUND.getCode(), e.getMessage());
    }

    /**
     * 业务异常
     *
     * @param e BusinessException
     * @return PubResult<?>
     */
    @ExceptionHandler(BusinessException.class)
    public PubResult<?> handleBusinessException(BusinessException e) {
        log.error(e.getMessage());
        return PubResult.error(UserReCode.USER_NOT_FOUND.getCode(), e.getMessage());
    }

    /**
     * 账户不可用
     *
     * @param e AccountLockedException
     * @return PubResult<?>
     */
    @ExceptionHandler(AccountLockedException.class)
    public PubResult<?> handleAccountLockedException(AccountLockedException e) {
        log.error(e.getMessage());
        return PubResult.error(UserReCode.USER_DISABLED.getCode(), e.getMessage());
    }
}

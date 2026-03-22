package com.star.swiftSecurity.service;

import com.star.swiftSecurity.constant.TokenConstants;
import com.star.swiftSecurity.constant.TokenReCode;
import com.star.swiftSecurity.utils.JwtUtil;
import com.star.swiftredis.service.TokenStorageService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * JWT 验证服务
 * 封装 JWT 鉴权逻辑，供 Filter 和 WebSocket 复用
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtValidator {

    private final JwtUtil jwtUtil;
    private final TokenStorageService tokenStorageService;

    /**
     * JWT 验证结果
     */
    public record ValidationResult(boolean valid, Long userId, TokenReCode errorCode, String errorMessage) {
        public static ValidationResult success(Long userId) {
            return new ValidationResult(true, userId, null, null);
        }

        public static ValidationResult fail(TokenReCode errorCode, String errorMessage) {
            return new ValidationResult(false, null, errorCode, errorMessage);
        }
    }

    /**
     * 验证 JWT Token
     * 与 JwtAuthenticationFilter 中的鉴权逻辑保持一致
     *
     * @param token JWT Token
     * @return 验证结果
     */
    public ValidationResult validate(String token) {
        if (token == null || token.isEmpty()) {
            return ValidationResult.fail(TokenReCode.TOKEN_INVALID, "缺少 token");
        }

        Long userId;

        // 1. 提取用户ID，捕获JWT过期异常
        try {
            userId = jwtUtil.extractUserId(token);
        } catch (ExpiredJwtException e) {
            log.warn("JWT 验证失败: token 已过期");
            return ValidationResult.fail(TokenReCode.TOKEN_EXPIRED, "token 已过期");
        } catch (Exception e) {
            log.error("JWT 验证失败: token 解析错误, {}", e.getMessage());
            return ValidationResult.fail(TokenReCode.TOKEN_INVALID, "token 解析错误");
        }

        // 2. 验证JWT签名
        try {
            if (!jwtUtil.validateToken(token)) {
                return ValidationResult.fail(TokenReCode.TOKEN_INVALID, "token 无效");
            }
        } catch (ExpiredJwtException e) {
            log.warn("JWT 验证失败: token 已过期");
            return ValidationResult.fail(TokenReCode.TOKEN_EXPIRED, "token 已过期");
        } catch (Exception e) {
            log.error("JWT 验证失败: token 验证错误, {}", e.getMessage());
            return ValidationResult.fail(TokenReCode.TOKEN_INVALID, "token 验证错误");
        }

        // 3. 校验Redis中的令牌有效性
        if (!tokenStorageService.validateToken(TokenConstants.accessToken, userId, token)) {
            return ValidationResult.fail(TokenReCode.TOKEN_NOT_FOUND, "token 在 Redis 中不存在或已失效");
        }

        return ValidationResult.success(userId);
    }
}
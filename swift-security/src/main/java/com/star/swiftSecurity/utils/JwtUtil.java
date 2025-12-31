package com.star.swiftSecurity.utils;

import com.star.swiftSecurity.constant.TokenConstants;
import com.star.swiftSecurity.entity.SwiftUserDetails;
import com.star.swiftSecurity.properties.JwtProperties;
import com.star.swiftredis.service.TokenStorageService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(JwtProperties.class)
public class JwtUtil {

    private final JwtProperties jwtProperties;

    private SecretKey key;

    private final TokenStorageService tokenStorageService;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    /**
     * 生成访问令牌
     *
     * @param userDetails 用户信息
     * @return token
     */
    public String generateAccessToken(SwiftUserDetails userDetails) {
        Map<String, String> claims = new HashMap<>();
        claims.put("sub", userDetails.getUserId().toString());//用户唯一标识
        claims.put("email", userDetails.getEmail());
        claims.put("username", userDetails.getUsername());
        claims.put("iat", String.valueOf(System.currentTimeMillis()));//发行时间
        claims.put("aud", userDetails.getAuthorities().toString());//令牌受众
        claims.put("jti", String.valueOf(System.currentTimeMillis()));
        claims.put("locale", "zh_CN");
        String accessToken = buildToken(claims, userDetails.getUserId().toString(), jwtProperties.getExpiration());
        tokenStorageService.storeToken(TokenConstants.accessToken, userDetails.getUsername(), accessToken, jwtProperties.getExpiration());
        return accessToken;
    }

    /**
     * 刷新令牌
     *
     * @param userDetails 用户信息
     * @return token
     */
    public String generateRefreshToken(SwiftUserDetails userDetails) {
        Map<String, String> claims = new HashMap<>();
        String refreshToken = buildToken(claims, userDetails.getUserId().toString(), jwtProperties.getRefreshExpiration());
        tokenStorageService.storeToken(TokenConstants.refreshToken, userDetails.getUsername(), refreshToken, jwtProperties.getRefreshExpiration());
        return refreshToken;
    }

    /**
     * 生成 Token
     *
     * @param claims     声明
     * @param subject    主题
     * @param expiration 有效期
     * @return Token
     */
    private String buildToken(Map<String, String> claims, String subject, long expiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    /**
     * 提取用户名
     *
     * @param token token
     * @return 用户名
     */
    public String extractUsername(String token) {
        return extractClaim(token).getSubject();
    }

    /**
     * 提取用户认证信息
     *
     * @param token token
     * @return 用户信息
     */
    public Claims extractClaim(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 验证token
     *
     * @param token token
     * @return boolean
     */
    public boolean validateToken(String token) {
        try {
            extractClaim(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT validation error: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 移除token（登出）
     *
     * @param type  type
     * @param token token
     */
    public void removeToken(String type, String token) {
        tokenStorageService.removeToken(type, token);
    }
}

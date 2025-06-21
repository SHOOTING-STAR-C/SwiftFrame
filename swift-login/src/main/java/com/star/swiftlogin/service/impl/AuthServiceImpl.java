package com.star.swiftlogin.service.impl;

import com.star.swiftjwt.properties.JwtProperties;
import com.star.swiftjwt.utils.JwtUtil;
import com.star.swiftlogin.service.AuthService;
import com.star.swiftredis.service.TokenStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * 登录认证
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final TokenStorageService tokenStorageService;
    private final UserDetailsService userDetailsService;

    /**
     * 验证用户身份
     * @param username 用户名
     * @param password 密码
     * @return Token
     */
    public String authenticate(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtUtil.generateAccessToken(userDetails);

        // 存储到Redis
        tokenStorageService.storeToken(
                userDetails.getUsername(),
                accessToken,
                jwtProperties.getExpiration()
        );

        return accessToken;
    }

    /**
     * 刷新Token
     *
     * @param refreshToken 待刷新token
     * @return newAccessToken
     */
    public String refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String username = jwtUtil.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String newAccessToken = jwtUtil.generateAccessToken(userDetails);

        // 更新Redis中的令牌
        tokenStorageService.storeToken(
                username,
                newAccessToken,
                jwtProperties.getExpiration()
        );

        return newAccessToken;
    }
}

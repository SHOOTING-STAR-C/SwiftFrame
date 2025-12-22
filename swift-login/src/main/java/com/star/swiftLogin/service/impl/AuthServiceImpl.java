package com.star.swiftLogin.service.impl;

import com.star.swiftCommon.domain.PubResult;
import com.star.swiftSecurity.domain.JwtToken;
import com.star.swiftSecurity.entity.SwiftUserDetails;
import com.star.swiftSecurity.exception.InvalidTokenException;
import com.star.swiftSecurity.service.SwiftUserService;
import com.star.swiftSecurity.utils.JwtUtil;
import com.star.swiftLogin.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.security.auth.login.AccountLockedException;

/**
 * 登录认证
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    @Lazy
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final SwiftUserService userService;

    /**
     * 登录
     *
     * @param username username
     * @param password password
     * @return PubResult<Map < String, String>>
     */
    @Override
    public PubResult<JwtToken> login(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        SwiftUserDetails userDetails = (SwiftUserDetails) authentication.getPrincipal();

        // 记录登录成功
        userService.recordLoginSuccess(
                userDetails.getUserId(),
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                        .getRequest().getRemoteAddr()
        );

        // 生成令牌
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);
        return PubResult.success(new JwtToken(accessToken, refreshToken));
    }

    /**
     * 刷新token
     *
     * @param refreshToken refreshToken
     * @return PubResult<Map < String, String>>
     */
    @Override
    public PubResult<JwtToken> refreshToken(String refreshToken) throws AccountLockedException {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new InvalidTokenException("无效的刷新令牌");
        }

        String username = jwtUtil.extractUsername(refreshToken);
        SwiftUserDetails user = userService.loadUserByUsername(username);

        // 检查用户状态
        if (!user.isAccountNonLocked() || !user.isEnabled()) {
            throw new AccountLockedException("账户不可用");
        }

        String newAccessToken = jwtUtil.generateAccessToken(user);

        return PubResult.success(new JwtToken(newAccessToken, null)); // 不返回新refresh token
    }

    /**
     * 登出
     *
     * @param token token
     */
    @Override
    public void logoutUser(String type, String token) {
        jwtUtil.removeToken(type, token);
    }

    /**
     * 创建用户
     *
     * @param userDetails userDetails
     * @return SwiftUserDetails
     */
    @Override
    public SwiftUserDetails createUser(SwiftUserDetails userDetails) {
        return userService.createUser(userDetails);
    }
}

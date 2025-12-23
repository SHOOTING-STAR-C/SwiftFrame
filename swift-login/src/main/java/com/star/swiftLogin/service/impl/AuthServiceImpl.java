package com.star.swiftLogin.service.impl;

import com.star.swiftCommon.domain.PubResult;
import com.star.swiftSecurity.domain.JwtToken;
import com.star.swiftSecurity.entity.SwiftUserDetails;
import com.star.swiftSecurity.exception.InvalidTokenException;
import com.star.swiftEncrypt.properties.CryptoEncryptProperties;
import com.star.swiftEncrypt.utils.RsaUtil;
import com.star.swiftSecurity.service.SwiftUserService;
import com.star.swiftSecurity.utils.JwtUtil;
import com.star.swiftLogin.service.AuthService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
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
    private final CryptoEncryptProperties cryptoEncryptProperties;
    private final Validator validator;

    /**
     * 登录
     *
     * @param username username
     * @param password password
     * @return PubResult<Map < String, String>>
     */
    @Override
    public PubResult<JwtToken> login(String username, String password) {
        // RSA 解密逻辑
        String decryptUsername = username;
        String decryptPassword = password;
        try {
            decryptUsername = RsaUtil.decrypt(username, cryptoEncryptProperties.getRsaPrivateKey());
            decryptPassword = RsaUtil.decrypt(password, cryptoEncryptProperties.getRsaPrivateKey());
        } catch (Exception e) {
            log.error("RSA 解密失败: {}", e.getMessage());
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(decryptUsername, decryptPassword)
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
        // 注册字段 RSA 解密
        try {
            userDetails.setUsername(RsaUtil.decrypt(userDetails.getUsername(), cryptoEncryptProperties.getRsaPrivateKey()));
            userDetails.setPassword(RsaUtil.decrypt(userDetails.getPassword(), cryptoEncryptProperties.getRsaPrivateKey()));
            if (userDetails.getEmail() != null) {
                userDetails.setEmail(RsaUtil.decrypt(userDetails.getEmail(), cryptoEncryptProperties.getRsaPrivateKey()));
            }
            if (userDetails.getPhone() != null) {
                userDetails.setPhone(RsaUtil.decrypt(userDetails.getPhone(), cryptoEncryptProperties.getRsaPrivateKey()));
            }
        } catch (Exception e) {
            log.error("注册信息解密失败: {}", e.getMessage());
            // 如果解密失败，保留原值。实际生产环境下建议抛出业务异常。
        }

        // 解密后手动校验
        validateUserDetails(userDetails);

        return userService.createUser(userDetails);
    }

    /**
     * 手动校验用户信息
     *
     * @param userDetails 用户信息
     */
    private void validateUserDetails(SwiftUserDetails userDetails) {
        var violations = validator.validate(userDetails);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}

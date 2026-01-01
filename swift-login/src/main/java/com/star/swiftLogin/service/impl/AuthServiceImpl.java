package com.star.swiftLogin.service.impl;

import com.star.swiftCommon.domain.PubResult;
import com.star.swiftSecurity.domain.JwtToken;
import com.star.swiftSecurity.entity.SwiftUserDetails;
import com.star.swiftSecurity.exception.InvalidTokenException;
import com.star.swiftSecurity.service.CryptoService;
import com.star.swiftSecurity.service.SwiftUserService;
import com.star.swiftSecurity.utils.JwtUtil;
import com.star.swiftLogin.service.AuthService;
import com.star.swiftMail.service.MailService;
import com.star.swiftredis.service.VerificationCodeService;
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
    private final CryptoService cryptoService;
    private final Validator validator;
    private final MailService mailService;
    private final VerificationCodeService verificationCodeService;

    /**
     * 登录
     *
     * @param username username
     * @param password password
     * @return PubResult<Map < String, String>>
     */
    @Override
    public PubResult<JwtToken> login(String username, String password) {
        // 解密用户名和密码
        String decryptUsername = username;
        String decryptPassword = password;
        try {
            decryptUsername = cryptoService.decryptUsername(username);
            decryptPassword = cryptoService.decryptPassword(password);
        } catch (Exception e) {
            log.error("解密失败: {}", e.getMessage());
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
        // 解密注册字段
        try {
            String[] decryptedInfo = cryptoService.decryptRegistrationInfo(
                    userDetails.getUsername(),
                    userDetails.getPassword(),
                    userDetails.getEmail(),
                    userDetails.getPhone()
            );
            
            userDetails.setUsername(decryptedInfo[0]);
            userDetails.setPassword(decryptedInfo[1]);
            if (decryptedInfo[2] != null) {
                userDetails.setEmail(decryptedInfo[2]);
            }
            if (decryptedInfo[3] != null) {
                userDetails.setPhone(decryptedInfo[3]);
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

    /**
     * 发送忘记密码验证码
     *
     * @param email 邮箱
     * @return PubResult<String>
     */
    @Override
    public PubResult<String> sendForgotPasswordCode(String email) {
        // 查找用户
        SwiftUserDetails user = userService.getUserByEmail(email);
        if (user == null) {
            return PubResult.error("该邮箱未注册");
        }

        // 生成6位验证码
        String verificationCode = String.format("%06d", (int) (Math.random() * 1000000));

        // 存储验证码，有效期5分钟
        verificationCodeService.storeVerificationCode(email, verificationCode, 300);

        // 发送邮件
        mailService.sendVerificationCode(email, verificationCode, "重置密码");

        return PubResult.success("验证码已发送至您的邮箱");
    }

    /**
     * 重置密码
     *
     * @param email            邮箱
     * @param verificationCode 验证码
     * @param newPassword      新密码
     * @return PubResult<String>
     */
    @Override
    public PubResult<String> resetPassword(String email, String verificationCode, String newPassword) {
        // 验证验证码
        if (!verificationCodeService.verifyVerificationCode(email, verificationCode)) {
            return PubResult.error("验证码错误或已过期");
        }

        // 查找用户
        SwiftUserDetails user = userService.getUserByEmail(email);
        if (user == null) {
            return PubResult.error("该邮箱未注册");
        }

        // 解密新密码
        String decryptedPassword = newPassword;
        try {
            decryptedPassword = cryptoService.decryptPassword(newPassword);
        } catch (Exception e) {
            log.error("密码解密失败: {}", e.getMessage());
            return PubResult.error("密码格式错误");
        }

        // 重置密码
        userService.changePassword(user.getUserId(), decryptedPassword);

        // 删除验证码
        verificationCodeService.deleteVerificationCode(email);

        return PubResult.success("密码重置成功");
    }
}

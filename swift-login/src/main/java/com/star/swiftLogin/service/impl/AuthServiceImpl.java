package com.star.swiftLogin.service.impl;

import com.star.swiftCommon.domain.PubResult;
import com.star.swiftSecurity.constant.TokenReCode;
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
     * 刷新token（Token轮换机制）
     *
     * @param refreshToken refreshToken
     * @return PubResult<JwtToken>
     */
    @Override
    public PubResult<JwtToken> refreshToken(String refreshToken) {
        // 验证旧的refreshToken
        log.debug("refreshToken: {}", refreshToken);
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new InvalidTokenException(TokenReCode.TOKEN_INVALID);
        }

        Long userId = jwtUtil.extractUserId(refreshToken);
        
        // 检查Redis中是否存在该refreshToken
        if (!jwtUtil.validateTokenInRedis("refresh", userId, refreshToken)) {
            throw new InvalidTokenException(TokenReCode.TOKEN_INVALID);
        }
        
        SwiftUserDetails user = userService.loadUserByUserId(userId);

        // 检查用户状态
        if (!user.isAccountNonLocked() || !user.isEnabled()) {
            return PubResult.error("账户已被锁定或禁用，无法刷新Token");
        }

        // 生成新的token对
        String newAccessToken = jwtUtil.generateAccessToken(user);
        String newRefreshToken = jwtUtil.generateRefreshToken(user);

        // 将旧的refreshToken标记为失效（从Redis删除）
        jwtUtil.removeToken("refresh", refreshToken);

        return PubResult.success(new JwtToken(newAccessToken, newRefreshToken));
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
        // 解密邮箱、验证码和密码
        String decryptedEmail;
        String decryptedCode;
        String decryptedPassword;
        
        try {
            decryptedEmail = cryptoService.decrypt(email);
            decryptedCode = cryptoService.decrypt(verificationCode);
            decryptedPassword = cryptoService.decryptPassword(newPassword);
        } catch (Exception e) {
            log.error("重置密码信息解密失败: {}", e.getMessage());
            return PubResult.error("信息格式错误");
        }

        // 手动校验解密后的参数
        try {
            // 创建临时对象用于校验
            com.star.swiftLogin.dto.ResetPasswordRequest resetRequest = 
                new com.star.swiftLogin.dto.ResetPasswordRequest();
            resetRequest.setEmail(decryptedEmail);
            resetRequest.setVerificationCode(decryptedCode);
            resetRequest.setNewPassword(decryptedPassword);
            
            var violations = validator.validate(resetRequest);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
        } catch (ConstraintViolationException e) {
            log.error("重置密码参数校验失败: {}", e.getMessage());
            return PubResult.error("参数校验失败: " + e.getMessage());
        }

        // 验证验证码
        if (!verificationCodeService.verifyVerificationCode(decryptedEmail, decryptedCode)) {
            return PubResult.error("验证码错误或已过期");
        }

        // 查找用户
        SwiftUserDetails user = userService.getUserByEmail(decryptedEmail);
        if (user == null) {
            return PubResult.error("该邮箱未注册");
        }

        // 重置密码
        userService.changePassword(user.getUserId(), decryptedPassword);

        // 删除验证码
        verificationCodeService.deleteVerificationCode(decryptedEmail);

        return PubResult.success("密码重置成功");
    }
}

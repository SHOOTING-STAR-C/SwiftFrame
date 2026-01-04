package com.star.swiftLogin.service;

import com.star.swiftCommon.domain.PubResult;
import com.star.swiftSecurity.domain.JwtToken;
import com.star.swiftSecurity.entity.SwiftUserDetails;

import javax.security.auth.login.AccountLockedException;


/**
 * 校验
 *
 * @author SHOOTING_STAR_C
 */
public interface AuthService {
    /**
     * 登录
     *
     * @param username username
     * @param password password
     * @return PubResult<Map < String, String>>
     */
    PubResult<JwtToken> login(String username, String password);


    /**
     * 刷新token
     *
     * @param refreshToken refreshToken
     * @return PubResult<JwtToken>
     */
    PubResult<JwtToken> refreshToken(String refreshToken);

    /**
     * 登出
     *
     * @param token token
     */
    void logoutUser(String type, String token);

    /**
     * 创建用户
     *
     * @param userDetails userDetails
     * @return SwiftUserDetails
     */
    SwiftUserDetails createUser(SwiftUserDetails userDetails);

    /**
     * 发送忘记密码验证码
     *
     * @param email 邮箱
     * @return PubResult<String>
     */
    PubResult<String> sendForgotPasswordCode(String email);

    /**
     * 重置密码
     *
     * @param email            邮箱
     * @param verificationCode 验证码
     * @param newPassword      新密码
     * @return PubResult<String>
     */
    PubResult<String> resetPassword(String email, String verificationCode, String newPassword);
}

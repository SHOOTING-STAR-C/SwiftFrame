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
     * @return PubResult<Map < String, String>>
     */
    PubResult<JwtToken> refreshToken(String refreshToken) throws AccountLockedException;

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
}

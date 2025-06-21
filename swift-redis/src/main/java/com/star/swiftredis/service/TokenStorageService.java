package com.star.swiftredis.service;

/**
 * token缓存服务
 *
 * @author SHOOTING_STAR_C
 */
public interface TokenStorageService {
    /**
     * 持久化Token
     *
     * @param username   用户名
     * @param token      token
     * @param expiration 有效期
     */
    void storeToken(String username, String token, long expiration);

    /**
     * 获取Token
     *
     * @param username 用户名
     * @return Token
     */
    String getToken(String username);

    /**
     * 移除Token
     *
     * @param username 用户名
     */
    void removeToken(String username);

    /**
     * 校验Token
     *
     * @param username 用户名
     * @param token    Token
     * @return boolean
     */
    boolean validateToken(String username, String token);
}

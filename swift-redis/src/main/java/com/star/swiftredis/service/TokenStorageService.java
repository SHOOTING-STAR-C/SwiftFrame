package com.star.swiftredis.service;

/**
 * token缓存服务
 *
 * @author SHOOTING_STAR_C
 */
public interface TokenStorageService {
    /**
     * @param type       类型
     * @param username   用户名
     * @param token      token
     * @param expiration 有效期
     */
    void storeToken(String type, String username, String token, long expiration);

    /**
     * 获取Token
     *
     * @param username 用户名
     * @return Token
     */
    String getToken(String type, String username);

    /**
     * 移除Token
     *
     * @param username 用户名
     */
    void removeToken(String type, String username);

    /**
     * 校验Token
     *
     * @param type     类型
     * @param username 用户名
     * @param token    Token
     * @return boolean
     */
    boolean validateToken(String type, String username, String token);
}

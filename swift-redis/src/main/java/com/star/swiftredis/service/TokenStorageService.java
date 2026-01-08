package com.star.swiftredis.service;

/**
 * token缓存服务
 *
 * @author SHOOTING_STAR_C
 */
public interface TokenStorageService {
    /**
     * @param type       类型
     * @param userId     用户Id
     * @param token      token
     * @param expiration 有效期
     */
    void storeToken(String type,Long userId, String token, long expiration);

    /**
     * 获取Token
     *
     * @param userId 用户ID
     * @return Token
     */
    String getToken(String type, Long userId);

    /**
     * 移除Token
     *
     * @param userId 用户ID
     */
    void removeToken(String type, Long userId);

    /**
     * 校验Token
     *
     * @param type  类型
     * @param userId 用户ID
     * @param token Token
     * @return boolean
     */
    boolean validateToken(String type, Long userId, String token);
}

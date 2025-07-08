package com.star.swiftredis.service.impl;

import com.star.swiftCommon.properties.CommonProperties;
import com.star.swiftredis.service.TokenStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Token缓存服务
 *
 * @author SHOOTING_STAR_C
 */
@Service
@RequiredArgsConstructor
public class TokenStorageServiceImpl implements TokenStorageService {

    private final RedisTemplate<String, String> redisTemplate;
    private final CommonProperties commonProperties;

    /**
     * 持久化Token
     *
     * @param type       类型
     * @param username   用户名
     * @param token      token
     * @param expiration 有效期
     */
    public void storeToken(String type, String username, String token, long expiration) {
        redisTemplate.opsForValue().set(
                commonProperties.getName() + "_" + type + "_" + username,
                token,
                expiration,
                TimeUnit.MILLISECONDS);
    }

    /**
     * 获取Token
     *
     * @param username 用户名
     * @return Token
     */
    public String getToken(String type, String username) {
        return redisTemplate.opsForValue().get(commonProperties.getName() + "_" + type + "_" + username);
    }

    /**
     * 移除Token
     *
     * @param username 用户名
     */
    public void removeToken(String type, String username) {
        redisTemplate.delete(commonProperties.getName() + "_" + type + "_" + username);
    }

    /**
     * 校验Token
     *
     * @param username 用户名
     * @param token    Token
     * @return boolean
     */
    public boolean validateToken(String type, String username, String token) {
        String storedToken = getToken(type, username);
        return storedToken != null && storedToken.equals(token);
    }
}

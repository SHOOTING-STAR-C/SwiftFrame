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
     * @param userId     用户ID
     * @param token      token
     * @param expiration 有效期
     */
    public void storeToken(String type, Long userId, String token, long expiration) {
        redisTemplate.opsForValue().set(
                commonProperties.getName() + "_" + type + "_" + userId,
                token,
                expiration,
                TimeUnit.MILLISECONDS);
    }

    /**
     * 获取Token
     *
     * @param userId 用户ID
     * @return Token
     */
    public String getToken(String type, Long userId) {
        return redisTemplate.opsForValue().get(commonProperties.getName() + "_" + type + "_" + userId);
    }

    /**
     * 移除Token
     *
     * @param userId 用户ID
     */
    public void removeToken(String type, Long userId) {
        redisTemplate.delete(commonProperties.getName() + "_" + type + "_" + userId);
    }

    /**
     * 校验Token
     *
     * @param userId 用户ID
     * @param token    Token
     * @return boolean
     */
    public boolean validateToken(String type, Long userId, String token) {
        String storedToken = getToken(type, userId);
        return storedToken != null && storedToken.equals(token);
    }
}

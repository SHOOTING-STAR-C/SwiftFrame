package com.star.swiftredis.service.impl;

import com.star.swiftCommon.properties.CommonProperties;
import com.star.swiftredis.service.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现
 *
 * @author SHOOTING_STAR_C
 */
@Service
@RequiredArgsConstructor
public class VerificationCodeServiceImpl implements VerificationCodeService {

    private final RedisTemplate<String, String> redisTemplate;
    private final CommonProperties commonProperties;

    private static final String VERIFICATION_CODE_PREFIX = "verify_code_";

    /**
     * 存储验证码
     *
     * @param key       键
     * @param code      验证码
     * @param expiration 过期时间（秒）
     */
    @Override
    public void storeVerificationCode(String key, String code, long expiration) {
        redisTemplate.opsForValue().set(
                commonProperties.getName() + "_" + VERIFICATION_CODE_PREFIX + key,
                code,
                expiration,
                TimeUnit.SECONDS
        );
    }

    /**
     * 获取验证码
     *
     * @param key 键
     * @return 验证码
     */
    @Override
    public String getVerificationCode(String key) {
        return redisTemplate.opsForValue().get(commonProperties.getName() + "_" + VERIFICATION_CODE_PREFIX + key);
    }

    /**
     * 验证验证码
     *
     * @param key  键
     * @param code 验证码
     * @return 是否有效
     */
    @Override
    public boolean verifyVerificationCode(String key, String code) {
        String storedCode = getVerificationCode(key);
        return storedCode != null && storedCode.equals(code);
    }

    /**
     * 删除验证码
     *
     * @param key 键
     */
    @Override
    public void deleteVerificationCode(String key) {
        redisTemplate.delete(commonProperties.getName() + "_" + VERIFICATION_CODE_PREFIX + key);
    }
}

package com.star.swiftredis.service;

/**
 * 验证码服务
 *
 * @author SHOOTING_STAR_C
 */
public interface VerificationCodeService {

    /**
     * 存储验证码
     *
     * @param key       键
     * @param code      验证码
     * @param expiration 过期时间（秒）
     */
    void storeVerificationCode(String key, String code, long expiration);

    /**
     * 获取验证码
     *
     * @param key 键
     * @return 验证码
     */
    String getVerificationCode(String key);

    /**
     * 验证验证码
     *
     * @param key  键
     * @param code 验证码
     * @return 是否有效
     */
    boolean verifyVerificationCode(String key, String code);

    /**
     * 删除验证码
     *
     * @param key 键
     */
    void deleteVerificationCode(String key);
}

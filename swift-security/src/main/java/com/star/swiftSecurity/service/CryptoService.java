package com.star.swiftSecurity.service;

/**
 * 加密服务接口
 * 封装加密/解密逻辑，业务层只关心"做什么"，不关心"怎么做"
 *
 * @author SHOOTING_STAR_C
 */
public interface CryptoService {

    /**
     * 解密字符串
     *
     * @param ciphertext 密文
     * @return 明文
     */
    String decrypt(String ciphertext);

    /**
     * 加密字符串
     *
     * @param plaintext 明文
     * @return 密文
     */
    String encrypt(String plaintext);

    /**
     * 解密用户名（登录时使用）
     *
     * @param encryptedUsername 加密的用户名
     * @return 明文用户名
     */
    String decryptUsername(String encryptedUsername);

    /**
     * 解密密码（登录时使用）
     *
     * @param encryptedPassword 加密的密码
     * @return 明文密码
     */
    String decryptPassword(String encryptedPassword);

    /**
     * 解密注册信息
     *
     * @param encryptedUsername 加密的用户名
     * @param encryptedPassword 加密的密码
     * @param encryptedEmail    加密的邮箱（可选）
     * @param encryptedPhone    加密的手机号（可选）
     * @return 解密后的信息数组 [username, password, email, phone]
     */
    String[] decryptRegistrationInfo(String encryptedUsername, String encryptedPassword,
                                     String encryptedEmail, String encryptedPhone);
}

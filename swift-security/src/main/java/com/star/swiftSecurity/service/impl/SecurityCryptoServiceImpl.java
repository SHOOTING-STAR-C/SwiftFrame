package com.star.swiftSecurity.service.impl;

import com.star.swiftEncrypt.properties.CryptoEncryptProperties;
import com.star.swiftEncrypt.utils.RsaUtil;
import com.star.swiftSecurity.service.CryptoService;
import com.star.swiftSecurity.service.DecryptionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 加密服务实现
 * 封装 RSA 加密/解密逻辑，为业务层提供抽象接口
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityCryptoServiceImpl implements CryptoService {

    private final CryptoEncryptProperties cryptoEncryptProperties;

    @Override
    public String decrypt(String ciphertext) {
        try {
            return RsaUtil.decrypt(ciphertext, cryptoEncryptProperties.getRsaPrivateKey());
        } catch (Exception e) {
            log.error("解密失败：{}", e.getMessage());
            throw new RuntimeException("解密失败", e);
        }
    }

    @Override
    public String encrypt(String plaintext) {
        try {
            return RsaUtil.encrypt(plaintext, cryptoEncryptProperties.getRsaPublicKey());
        } catch (Exception e) {
            log.error("加密失败：{}", e.getMessage());
            throw new RuntimeException("加密失败", e);
        }
    }

    @Override
    public String decryptUsername(String encryptedUsername) {
        try {
            return RsaUtil.decrypt(encryptedUsername, cryptoEncryptProperties.getRsaPrivateKey());
        } catch (Exception e) {
            log.error("用户名解密失败：{}", e.getMessage());
            throw new RuntimeException("用户名解密失败", e);
        }
    }

    @Override
    public String decryptPassword(String encryptedPassword) {
        try {
            return RsaUtil.decrypt(encryptedPassword, cryptoEncryptProperties.getRsaPrivateKey());
        } catch (Exception e) {
            log.error("密码解密失败：{}", e.getMessage());
            throw new RuntimeException("密码解密失败", e);
        }
    }

    @Override
    public DecryptionResult decryptRegistrationInfo(String encryptedUsername, String encryptedPassword,
                                           String encryptedEmail, String encryptedPhone) {
        try {
            return DecryptionResult.builder()
                .username(RsaUtil.decrypt(encryptedUsername, cryptoEncryptProperties.getRsaPrivateKey()))
                .password(RsaUtil.decrypt(encryptedPassword, cryptoEncryptProperties.getRsaPrivateKey()))
                .email((encryptedEmail != null && !encryptedEmail.isEmpty())
                    ? RsaUtil.decrypt(encryptedEmail, cryptoEncryptProperties.getRsaPrivateKey()) : null)
                .phone((encryptedPhone != null && !encryptedPhone.isEmpty())
                    ? RsaUtil.decrypt(encryptedPhone, cryptoEncryptProperties.getRsaPrivateKey()) : null)
                .build();
        } catch (Exception e) {
            log.error("注册信息解密失败：{}", e.getMessage());
            throw new RuntimeException("注册信息解密失败", e);
        }
    }
}
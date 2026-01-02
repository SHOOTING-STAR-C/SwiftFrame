package com.star.swiftEncrypt.encryptor;

import com.star.swiftEncrypt.service.CryptoService;
import com.star.swiftEncrypt.exception.CryptoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.stereotype.Component;

/**
 * 自定义 Jasypt 加密器（支持 AES 和 RSA 两种模式）
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Component("SwiftJasyptEncryptor")
@RequiredArgsConstructor
public class SwiftJasyptEncryptor implements StringEncryptor {
    private static final String AES_PREFIX = "AES:";
    private static final String RSA_PREFIX = "RSA:";

    private final CryptoService cryptoService;

    /**
     * 加密
     *
     * @param message 待加密明文
     * @return 密文
     */
    @Override
    public String encrypt(String message) {
        if (message == null || message.isEmpty()) return message;
        return cryptoService.encryptWithAES(message);
    }

    /**
     * 解密
     *
     * @param encryptedMessage 密文
     * @return 明文
     */
    @Override
    public String decrypt(String encryptedMessage) {
        if (encryptedMessage == null || encryptedMessage.isEmpty())
            return encryptedMessage;

        try {
            if (encryptedMessage.startsWith(RSA_PREFIX)) {
                return cryptoService.decryptWithRSA(
                        encryptedMessage.substring(RSA_PREFIX.length())
                );
            } else if (encryptedMessage.startsWith(AES_PREFIX)) {
                return cryptoService.decryptWithAES(
                        encryptedMessage.substring(AES_PREFIX.length())
                );
            } else {
                // 自动检测模式
                return autoDecrypt(encryptedMessage);
            }
        } catch (Exception e) {
            throw new CryptoException("解密失败: " + encryptedMessage, e);
        }
    }

    private String autoDecrypt(String encryptedMessage) {
        try {
            // 先尝试 AES 解密
            return cryptoService.decryptWithAES(encryptedMessage);
        } catch (Exception aesEx) {
            try {
                // 再尝试 RSA 解密
                return cryptoService.decryptWithRSA(encryptedMessage);
            } catch (Exception rsaEx) {
                throw new CryptoException("无法解密: " + encryptedMessage, rsaEx);
            }
        }
    }
}

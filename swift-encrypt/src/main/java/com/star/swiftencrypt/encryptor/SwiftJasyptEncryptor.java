package com.star.swiftencrypt.encryptor;

import com.star.swiftencrypt.exception.CryptoException;
import com.star.swiftencrypt.service.CryptoService;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 自定义 Jasypt 加密器（支持 AES 和 RSA 两种模式）
 *
 * @author SHOOTING_STAR_C
 */
@Component("SwiftJasyptEncryptor")
public class SwiftJasyptEncryptor implements StringEncryptor {
    private static final String AES_PREFIX = "AES:";
    private static final String RSA_PREFIX = "RSA:";

    @Autowired
    private CryptoService cryptoService;


    @Override
    public String encrypt(String message) {
        if (message == null || message.isEmpty()) return message;

        // 判断加密模式
        if (cryptoService.isRsaModeEnabled()) {
            return RSA_PREFIX + cryptoService.encryptWithRSA(message);
        } else {
            return AES_PREFIX + cryptoService.encryptWithAES(message);
        }
    }

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

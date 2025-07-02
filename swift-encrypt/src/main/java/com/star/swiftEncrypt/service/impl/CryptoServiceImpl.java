package com.star.swiftEncrypt.service.impl;

import com.star.swiftEncrypt.exception.CryptoException;
import com.star.swiftEncrypt.properties.CryptoEncryptProperties;
import com.star.swiftEncrypt.service.CryptoService;
import com.star.swiftEncrypt.utils.AesGmcUtil;
import com.star.swiftEncrypt.utils.RsaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * 加密服务（整合AES和RSA）
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CryptoServiceImpl implements CryptoService {

    private final CryptoEncryptProperties cryptoEncryptProperties;

    /**
     * 使用AES加密数据
     */
    public String encryptWithAES(String data) {
        try {
            return AesGmcUtil.encrypt(data, cryptoEncryptProperties.getAesKey());
        } catch (Exception e) {
            throw new CryptoException("AES加密失败", e);
        }
    }

    /**
     * 使用AES解密数据
     */
    public String decryptWithAES(String encryptedData) {
        try {
            return AesGmcUtil.decrypt(encryptedData, cryptoEncryptProperties.getAesKey());
        } catch (Exception e) {
            throw new CryptoException("AES解密失败", e);
        }
    }

    /**
     * 使用RSA加密数据
     */
    public String encryptWithRSA(String data) {
        try {
            return RsaUtil.encrypt(data, cryptoEncryptProperties.getRsaPublicKey());
        } catch (Exception e) {
            throw new CryptoException("RSA加密失败", e);
        }
    }

    /**
     * 使用RSA解密数据
     */
    public String decryptWithRSA(String encryptedData) {
        try {
            return RsaUtil.decrypt(encryptedData, cryptoEncryptProperties.getRsaPrivateKey());
        } catch (Exception e) {
            throw new CryptoException("RSA解密失败", e);
        }
    }

    /**
     * 混合加密：使用RSA加密AES密钥 + AES加密数据
     *
     * @return 格式：RSA(AES密钥) + "|" + AES(数据)
     */
    public String hybridEncrypt(String data) {
        try {
            String encryptedAesKey = RsaUtil.encrypt(cryptoEncryptProperties.getAesKey(), cryptoEncryptProperties.getRsaPublicKey());
            String encryptedData = AesGmcUtil.encrypt(data, cryptoEncryptProperties.getAesKey());
            return encryptedAesKey + "|" + encryptedData;
        } catch (Exception e) {
            throw new CryptoException("混合加密失败", e);
        }
    }

    /**
     * 混合解密
     */
    public String hybridDecrypt(String encrypted) {
        try {
            String[] parts = encrypted.split("\\|", 2);
            if (parts.length != 2) throw new IllegalArgumentException("无效的加密格式");

            String decryptedAesKey = RsaUtil.decrypt(parts[0], cryptoEncryptProperties.getRsaPrivateKey());
            return AesGmcUtil.decrypt(parts[1], decryptedAesKey);
        } catch (Exception e) {
            throw new CryptoException("混合解密失败", e);
        }
    }

    /**
     * 判断加密模式
     *
     * @return boolean
     */
    public boolean isRsaModeEnabled() {
        return "rsa".equalsIgnoreCase(cryptoEncryptProperties.getMode());
    }


}

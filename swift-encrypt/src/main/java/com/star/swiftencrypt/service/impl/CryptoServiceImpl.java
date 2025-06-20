package com.star.swiftencrypt.service.impl;

import com.star.swiftencrypt.exception.CryptoException;
import com.star.swiftencrypt.properties.CryptoEncryptProperties;
import com.star.swiftencrypt.service.CryptoService;
import com.star.swiftencrypt.util.AESUtil;
import com.star.swiftencrypt.util.RSAUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.util.Base64;

/**
 * 加密服务（整合AES和RSA）
 *
 * @author SHOOTING_STAR_C
 */
@Service
@Slf4j
public class CryptoServiceImpl implements CryptoService {

    @Autowired
    private CryptoEncryptProperties cryptoEncryptProperties;

    /**
     * 使用AES加密数据
     */
    public String encryptWithAES(String data) {
        try {
            return AESUtil.encrypt(data, cryptoEncryptProperties.getAesKey());
        } catch (Exception e) {
            throw new CryptoException("AES加密失败", e);
        }
    }

    /**
     * 使用AES解密数据
     */
    public String decryptWithAES(String encryptedData) {
        try {
            return AESUtil.decrypt(encryptedData, cryptoEncryptProperties.getAesKey());
        } catch (Exception e) {
            throw new CryptoException("AES解密失败", e);
        }
    }

    /**
     * 使用RSA加密数据
     */
    public String encryptWithRSA(String data) {
        try {
            return RSAUtil.encrypt(data, cryptoEncryptProperties.getRsaPublicKey());
        } catch (Exception e) {
            throw new CryptoException("RSA加密失败", e);
        }
    }

    /**
     * 使用RSA解密数据
     */
    public String decryptWithRSA(String encryptedData) {
        try {
            return RSAUtil.decrypt(encryptedData, cryptoEncryptProperties.getRsaPrivateKey());
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
            String encryptedAesKey = RSAUtil.encrypt(cryptoEncryptProperties.getAesKey(), cryptoEncryptProperties.getRsaPublicKey());
            String encryptedData = AESUtil.encrypt(data, cryptoEncryptProperties.getAesKey());
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

            String decryptedAesKey = RSAUtil.decrypt(parts[0], cryptoEncryptProperties.getRsaPrivateKey());
            return AESUtil.decrypt(parts[1], decryptedAesKey);
        } catch (Exception e) {
            throw new CryptoException("混合解密失败", e);
        }
    }

    /**
     * 生成RSA密钥对（Base64编码）
     */
    public KeyPair generateRSAKeyPair() throws Exception {
        return RSAUtil.generateKeyPair();
    }


    /**
     * 生成并设置新的RSA密钥对
     *
     * @throws Exception
     */
    public void generateAndSetNewRsaKeys() throws Exception {
        KeyPair keyPair = generateRSAKeyPair();
        String rsaPublicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String rsaPrivateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        cryptoEncryptProperties.getRsa().setKeys(rsaPublicKey, rsaPrivateKey);
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

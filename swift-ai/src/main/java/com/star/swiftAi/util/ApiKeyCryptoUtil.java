package com.star.swiftAi.util;

import com.star.swiftEncrypt.service.CryptoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * API密钥加密/解密工具类
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApiKeyCryptoUtil {

    private final CryptoService cryptoService;

    /**
     * 直接解密API密钥字符串
     * 用于解密存储在数据库中的加密API密钥
     *
     * @param encryptedApiKey 加密的API密钥字符串
     * @return 解密后的API密钥
     */
    public String decryptApiKeyString(String encryptedApiKey) {
        if (encryptedApiKey == null || encryptedApiKey.isEmpty()) {
            return encryptedApiKey;
        }

        try {
            // 使用RSA解密
            return cryptoService.decryptWithRSA(encryptedApiKey);
        } catch (Exception e) {
            log.error("解密API密钥失败", e);
            throw new RuntimeException("解密API密钥失败: " + e.getMessage(), e);
        }
    }
}

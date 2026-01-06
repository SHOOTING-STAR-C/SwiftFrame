package com.star.swiftAi.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.star.swiftEncrypt.service.CryptoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

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
    private final ObjectMapper objectMapper;

    /**
     * 加密API密钥
     * 从providerConfig中提取apiKey字段并加密
     *
     * @param providerConfig 提供商配置JSON字符串
     * @return 加密后的配置JSON字符串
     */
    public String encryptApiKey(String providerConfig) {
        if (providerConfig == null || providerConfig.isEmpty()) {
            return providerConfig;
        }

        try {
            Map<String, Object> configMap = objectMapper.readValue(
                providerConfig,
                new TypeReference<Map<String, Object>>() {}
            );

            // 检查是否有apiKey字段
            if (configMap.containsKey("apiKey")) {
                String apiKey = configMap.get("apiKey").toString();
                if (!apiKey.isEmpty()) {
                    // 使用混合加密（RSA+AES）
                    String encryptedApiKey = cryptoService.hybridEncrypt(apiKey);
                    configMap.put("apiKey", encryptedApiKey);
                    configMap.put("apiKeyEncrypted", true);
                    log.debug("API密钥已加密");
                }
            }

            return objectMapper.writeValueAsString(configMap);
        } catch (Exception e) {
            log.error("加密API密钥失败", e);
            throw new RuntimeException("加密API密钥失败: " + e.getMessage(), e);
        }
    }

    /**
     * 解密API密钥
     * 从providerConfig中提取加密的apiKey字段并解密
     *
     * @param providerConfig 提供商配置JSON字符串
     * @return 解密后的配置JSON字符串
     */
    public String decryptApiKey(String providerConfig) {
        if (providerConfig == null || providerConfig.isEmpty()) {
            return providerConfig;
        }

        try {
            Map<String, Object> configMap = objectMapper.readValue(
                providerConfig,
                new TypeReference<Map<String, Object>>() {}
            );

            // 检查是否有加密标记
            boolean isEncrypted = Boolean.TRUE.equals(configMap.get("apiKeyEncrypted"));
            
            if (isEncrypted && configMap.containsKey("apiKey")) {
                String encryptedApiKey = configMap.get("apiKey").toString();
                if (!encryptedApiKey.isEmpty()) {
                    // 使用混合解密
                    String decryptedApiKey = cryptoService.hybridDecrypt(encryptedApiKey);
                    configMap.put("apiKey", decryptedApiKey);
                    configMap.remove("apiKeyEncrypted");
                    log.debug("API密钥已解密");
                }
            }

            return objectMapper.writeValueAsString(configMap);
        } catch (Exception e) {
            log.error("解密API密钥失败", e);
            throw new RuntimeException("解密API密钥失败: " + e.getMessage(), e);
        }
    }

    /**
     * 提取并解密API密钥
     * 从providerConfig中提取解密后的API密钥
     *
     * @param providerConfig 提供商配置JSON字符串
     * @return 解密后的API密钥
     */
    public String extractDecryptedApiKey(String providerConfig) {
        if (providerConfig == null || providerConfig.isEmpty()) {
            return null;
        }

        try {
            Map<String, Object> configMap = objectMapper.readValue(
                providerConfig,
                new TypeReference<Map<String, Object>>() {}
            );

            if (!configMap.containsKey("apiKey")) {
                return null;
            }

            String apiKey = configMap.get("apiKey").toString();
            boolean isEncrypted = Boolean.TRUE.equals(configMap.get("apiKeyEncrypted"));

            if (isEncrypted && !apiKey.isEmpty()) {
                return cryptoService.hybridDecrypt(apiKey);
            }

            return apiKey;
        } catch (Exception e) {
            log.error("提取解密API密钥失败", e);
            throw new RuntimeException("提取解密API密钥失败: " + e.getMessage(), e);
        }
    }
}

package com.star.swiftencrypt.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 加载aes|ras配置
 *
 * @author SHOOTING_STAR_C
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "crypto")
public class CryptoEncryptProperties {
    private String mode; // 加密模式
    private AESProperties aes; // 嵌套AES配置
    private RSAProperties rsa; // 嵌套RSA配置

    // 添加便捷访问方法
    public String getAesKey() {
        return aes != null ? aes.getKey() : null;
    }

    public String getRsaPublicKey() {
        return rsa != null ? rsa.getPublicKey() : null;
    }

    public String getRsaPrivateKey() {
        return rsa != null ? rsa.getPrivateKey() : null;
    }
}

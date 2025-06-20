package com.star.swiftencrypt.config;

import com.star.swiftencrypt.encryptor.SwiftJasyptEncryptor;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableEncryptableProperties
public class JasyptConfig {
    /**
     * 配置自定义加密器为主加密器
     */
    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor(SwiftJasyptEncryptor swiftJasyptEncryptor) {
        return swiftJasyptEncryptor;
    }
}

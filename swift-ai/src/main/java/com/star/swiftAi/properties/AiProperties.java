package com.star.swiftAi.properties;

import com.star.swiftAi.enums.AiProvider;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * AI 配置属性
 */
@Data
@ConfigurationProperties("swift.ai")
public class AiProperties {

    /**
     * 是否启用 AI 模块
     */
    private boolean enabled = true;

    /**
     * 默认服务商
     */
    private AiProvider defaultProvider = AiProvider.CUSTOM;

    /**
     * AI 服务商配置
     */
    private Map<String, ProviderConfig> providers = new HashMap<>();

    /**
     * AI 服务商配置
     */
    @Data
    public static class ProviderConfig {

        /**
         * 是否启用该服务商
         */
        private boolean enabled = false;

        /**
         * API 密钥
         */
        private String apiKey;

        /**
         * Base URL，如果为空则使用服务商默认值
         */
        private String baseUrl;

        /**
         * 默认模型
         */
        private String model;

        /**
         * 超时时间（秒）
         */
        private Long timeout = 30L;

        /**
         * 默认温度值
         */
        private Double temperature = 0.7;

        /**
         * 最大重试次数
         */
        private Integer maxRetries = 3;
    }
}

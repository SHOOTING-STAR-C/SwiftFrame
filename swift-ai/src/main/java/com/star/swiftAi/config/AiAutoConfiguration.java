package com.star.swiftAi.config;

import com.star.swiftAi.client.AiClient;
import com.star.swiftAi.client.AiClientManager;
import com.star.swiftAi.client.OpenAiCompatibleClient;
import com.star.swiftAi.enums.AiProvider;
import com.star.swiftConfig.config.ConfigAutoConfiguration;
import com.star.swiftConfig.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * AI模块自动配置类
 * 负责配置AI客户端管理器和相关Bean
 * 在配置模块加载完成后加载，使用配置模块的逻辑获取AI配置
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@AutoConfiguration
@AutoConfigureAfter(ConfigAutoConfiguration.class)
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "swift.ai", name = "enabled", havingValue = "true", matchIfMissing = true)
public class AiAutoConfiguration {

    private final SysConfigService sysConfigService;

    /**
     * 配置AI客户端管理器
     * 从配置模块获取AI相关配置
     */
    @Bean
    public AiClientManager aiClientManager() {
        log.info("开始配置AI客户端管理器...");
        
        Map<String, AiClient> clients = new HashMap<>();
        
        // 从配置模块获取AI服务商配置
        Map<String, String> aiConfigs = sysConfigService.getConfigMapByType("AI");
        
        // 获取默认服务商配置
        String defaultProviderCode = aiConfigs.getOrDefault("default.provider", "custom");
        AiProvider defaultProvider;
        try {
            defaultProvider = AiProvider.valueOf(defaultProviderCode.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("未知的默认服务商: {}, 使用默认值: CUSTOM", defaultProviderCode);
            defaultProvider = AiProvider.CUSTOM;
        }
        
        // 遍历配置的服务商，创建对应的客户端
        // 配置键格式: ai.{provider}.enabled, ai.{provider}.apiKey, ai.{provider}.baseUrl, ai.{provider}.model
        aiConfigs.forEach((key, value) -> {
            if (key.startsWith("ai.") && key.endsWith(".enabled") && "true".equalsIgnoreCase(value)) {
                String providerCode = key.substring(3, key.length() - 8); // 去掉 "ai." 和 ".enabled"
                
                try {
                    AiClient client = createAiClient(providerCode, aiConfigs);
                    clients.put(providerCode.toLowerCase(), client);
                    log.info("已配置AI客户端: {}", providerCode);
                } catch (Exception e) {
                    log.error("配置AI客户端失败: {}", providerCode, e);
                }
            }
        });
        
        if (clients.isEmpty()) {
            log.warn("未配置任何启用的AI客户端");
        }
        
        return new AiClientManager(clients, defaultProvider);
    }

    /**
     * 创建AI客户端实例
     * 从配置模块获取该服务商的配置
     */
    private AiClient createAiClient(String providerCode, Map<String, String> configs) {
        String prefix = "ai." + providerCode + ".";
        
        String apiKey = configs.get(prefix + "apiKey");
        String baseUrl = configs.get(prefix + "baseUrl");
        String model = configs.get(prefix + "model");
        String timeoutStr = configs.get(prefix + "timeout");
        String temperatureStr = configs.get(prefix + "temperature");
        String maxRetriesStr = configs.get(prefix + "maxRetries");
        
        Long timeout = timeoutStr != null ? Long.parseLong(timeoutStr) : 30L;
        Double temperature = temperatureStr != null ? Double.parseDouble(temperatureStr) : 0.7;
        Integer maxRetries = maxRetriesStr != null ? Integer.parseInt(maxRetriesStr) : 3;
        
        // 目前只支持OpenAI兼容的客户端
        // 后续可以根据不同的providerCode创建不同类型的客户端
        OpenAiCompatibleClient.Config config = OpenAiCompatibleClient.Config.builder()
                .provider(AiProvider.valueOf(providerCode.toUpperCase()))
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .model(model)
                .timeout(Duration.ofSeconds(timeout))
                .maxRetries(maxRetries)
                .defaultTemperature(temperature)
                .build();
        
        return new OpenAiCompatibleClient(config);
    }
}

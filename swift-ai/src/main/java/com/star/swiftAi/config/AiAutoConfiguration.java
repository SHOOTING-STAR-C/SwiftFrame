package com.star.swiftAi.config;

import com.star.swiftAi.client.AiClient;
import com.star.swiftAi.client.AiClientManager;
import com.star.swiftAi.client.OpenAiCompatibleClient;
import com.star.swiftAi.enums.AiProvider;
import com.star.swiftConfig.service.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * AI 自动配置类
 * 从配置表读取配置并创建和管理 AI 客户端实例
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass(AiClient.class)
public class AiAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AiClientManager aiClientManager(SysConfigService sysConfigService) {
        Map<String, AiClient> clients = new HashMap<>();
        AiProvider defaultProvider = AiProvider.CUSTOM;

        try {
            // 从配置表读取AI模块启用状态
            String enabled = sysConfigService.getConfigValue("swift.ai.enabled");
            if (enabled == null || !Boolean.parseBoolean(enabled)) {
                log.warn("AI模块未启用");
                return new AiClientManager(clients, defaultProvider);
            }

            // 读取默认服务商
            String defaultProviderStr = sysConfigService.getConfigValue("swift.ai.default-provider");
            if (defaultProviderStr != null) {
                defaultProvider = AiProvider.valueOf(defaultProviderStr);
            }

            // OpenAI 配置
            if (isProviderEnabled(sysConfigService, "openai")) {
                try {
                    AiClient openaiClient = createClientFromConfig(sysConfigService, "openai", AiProvider.OPENAI);
                    if (openaiClient != null && openaiClient.isAvailable()) {
                        clients.put("openai", openaiClient);
                        log.info("OpenAI client initialized successfully");
                    } else {
                        log.warn("OpenAI client health check failed");
                    }
                } catch (Exception e) {
                    log.error("Failed to initialize OpenAI client", e);
                }
            }

            // DeepSeek 配置
            if (isProviderEnabled(sysConfigService, "deepseek")) {
                try {
                    AiClient deepseekClient = createClientFromConfig(sysConfigService, "deepseek", AiProvider.DEEPSEEK);
                    if (deepseekClient != null && deepseekClient.isAvailable()) {
                        clients.put("deepseek", deepseekClient);
                        log.info("DeepSeek client initialized successfully");
                    } else {
                        log.warn("DeepSeek client health check failed");
                    }
                } catch (Exception e) {
                    log.error("Failed to initialize DeepSeek client", e);
                }
            }

            // 自定义服务商配置
            if (isProviderEnabled(sysConfigService, "custom")) {
                try {
                    AiClient customClient = createClientFromConfig(sysConfigService, "custom", AiProvider.CUSTOM);
                    if (customClient != null && customClient.isAvailable()) {
                        clients.put("custom", customClient);
                        log.info("Custom AI client initialized successfully");
                    } else {
                        log.warn("Custom AI client health check failed");
                    }
                } catch (Exception e) {
                    log.error("Failed to initialize custom AI client", e);
                }
            }

            if (clients.isEmpty()) {
                log.warn("No AI providers are configured or all providers failed health check");
            }

            log.info("AI配置从配置表加载完成，默认服务商: {}", defaultProvider);
        } catch (Exception e) {
            log.error("从配置表加载AI配置失败", e);
        }

        return new AiClientManager(clients, defaultProvider);
    }

    /**
     * 检查服务商是否启用
     */
    private boolean isProviderEnabled(SysConfigService sysConfigService, String providerName) {
        String enabled = sysConfigService.getConfigValue("swift.ai.providers." + providerName + ".enabled");
        return enabled != null && Boolean.parseBoolean(enabled);
    }

    /**
     * 从配置表创建AI客户端
     */
    private AiClient createClientFromConfig(SysConfigService sysConfigService, String providerName, AiProvider provider) {
        String prefix = "swift.ai.providers." + providerName;
        
        String apiKey = sysConfigService.getConfigValue(prefix + ".api-key");
        String baseUrl = sysConfigService.getConfigValue(prefix + ".base-url");
        String model = sysConfigService.getConfigValue(prefix + ".model");
        
        String timeoutStr = sysConfigService.getConfigValue(prefix + ".timeout");
        Long timeout = timeoutStr != null ? Long.parseLong(timeoutStr) : 30L;
        
        String temperatureStr = sysConfigService.getConfigValue(prefix + ".temperature");
        Double temperature = temperatureStr != null ? Double.parseDouble(temperatureStr) : 0.7;
        
        String maxRetriesStr = sysConfigService.getConfigValue(prefix + ".max-retries");
        Integer maxRetries = maxRetriesStr != null ? Integer.parseInt(maxRetriesStr) : 3;

        return createOpenAiCompatibleClient(provider, apiKey, baseUrl, model, timeout, temperature, maxRetries);
    }

    private AiClient createOpenAiCompatibleClient(AiProvider provider, String apiKey, String baseUrl, 
                                                   String model, Long timeout, Double temperature, 
                                                   Integer maxRetries) {
        String finalBaseUrl = Optional.ofNullable(baseUrl)
                .filter(s -> !s.trim().isEmpty())
                .orElse(provider.getDefaultBaseUrl());

        String finalModel = Optional.ofNullable(model)
                .filter(s -> !s.trim().isEmpty())
                .orElse(getDefaultModel(provider));

        Duration durationTimeout = Optional.ofNullable(timeout)
                .map(Duration::ofSeconds)
                .orElse(Duration.ofSeconds(30));

        return new OpenAiCompatibleClient(OpenAiCompatibleClient.Config.builder()
                .apiKey(apiKey)
                .baseUrl(finalBaseUrl)
                .model(finalModel)
                .provider(provider)
                .timeout(durationTimeout)
                .defaultTemperature(Optional.ofNullable(temperature).orElse(0.7))
                .maxRetries(Optional.ofNullable(maxRetries).orElse(3))
                .build());
    }

    private String getDefaultModel(AiProvider provider) {
        switch (provider) {
            case OPENAI:
                return "gpt-4o";
            case DEEPSEEK:
                return "deepseek-chat";
            case CUSTOM:
            default:
                return "custom-model";
        }
    }
}

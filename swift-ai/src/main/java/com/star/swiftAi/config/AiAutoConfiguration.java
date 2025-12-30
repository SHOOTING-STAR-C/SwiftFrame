package com.star.swiftAi.config;

import com.star.swiftAi.client.AiClient;
import com.star.swiftAi.client.AiClientManager;
import com.star.swiftAi.client.OpenAiCompatibleClient;
import com.star.swiftAi.enums.AiProvider;
import com.star.swiftAi.properties.AiProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * AI 自动配置类
 * 根据配置创建和管理 AI 客户端实例
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass(AiClient.class)
@ConditionalOnProperty(prefix = "swift.ai", value = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(AiProperties.class)
public class AiAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AiClientManager aiClientManager(AiProperties aiProperties) {
        Map<String, AiClient> clients = new HashMap<>();

        // OpenAI 配置
        AiProperties.ProviderConfig openaiConfig = aiProperties.getProviders().get("openai");
        if (openaiConfig != null && openaiConfig.isEnabled()) {
            try {
                AiClient openaiClient = createOpenAiCompatibleClient(
                        AiProvider.OPENAI,
                        openaiConfig.getApiKey(),
                        openaiConfig.getBaseUrl(),
                        openaiConfig.getModel(),
                        openaiConfig.getTimeout(),
                        openaiConfig.getTemperature(),
                        openaiConfig.getMaxRetries()
                );
                if (openaiClient.isAvailable()) {
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
        AiProperties.ProviderConfig deepseekConfig = aiProperties.getProviders().get("deepseek");
        if (deepseekConfig != null && deepseekConfig.isEnabled()) {
            try {
                AiClient deepseekClient = createOpenAiCompatibleClient(
                        AiProvider.DEEPSEEK,
                        deepseekConfig.getApiKey(),
                        deepseekConfig.getBaseUrl(),
                        deepseekConfig.getModel(),
                        deepseekConfig.getTimeout(),
                        deepseekConfig.getTemperature(),
                        deepseekConfig.getMaxRetries()
                );
                if (deepseekClient.isAvailable()) {
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
        AiProperties.ProviderConfig customConfig = aiProperties.getProviders().get("custom");
        if (customConfig != null && customConfig.isEnabled()) {
            try {
                AiClient customClient = createOpenAiCompatibleClient(
                        AiProvider.CUSTOM,
                        customConfig.getApiKey(),
                        customConfig.getBaseUrl(),
                        customConfig.getModel(),
                        customConfig.getTimeout(),
                        customConfig.getTemperature(),
                        customConfig.getMaxRetries()
                );
                if (customClient.isAvailable()) {
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

        return new AiClientManager(clients, aiProperties.getDefaultProvider());
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

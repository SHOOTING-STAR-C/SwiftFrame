package com.star.swiftAi.client;

import com.star.swiftAi.core.factory.ProviderFactory;
import com.star.swiftAi.core.model.Conversation;
import com.star.swiftAi.core.model.LLMResponse;
import com.star.swiftAi.core.model.MessageChain;
import com.star.swiftAi.core.model.ProviderRequest;
import com.star.swiftAi.core.provider.Provider;
import com.star.swiftAi.entity.AiModel;
import com.star.swiftAi.entity.AiProvider;
import com.star.swiftAi.service.AiModelService;
import com.star.swiftAi.service.AiProviderService;
import com.star.swiftAi.util.ApiKeyCryptoUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 基于Provider的AI客户端
 * 使用Provider接口与AI服务交互
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProviderClient {

    private final AiProviderService aiProviderService;
    private final AiModelService aiModelService;
    private final ObjectMapper objectMapper;
    private final ApiKeyCryptoUtil apiKeyCryptoUtil;

    /**
     * 同步对话调用
     *
     * @param modelId 模型ID
     * @param conversation 对话上下文
     * @return 对话响应
     */
    public LLMResponse chat(Long modelId, Conversation conversation) {
        // 获取模型信息
        AiModel model = aiModelService.getById(modelId);
        if (model == null) {
            throw new RuntimeException("模型不存在: " + modelId);
        }

        // 获取供应商信息
        AiProvider providerEntity = aiProviderService.getById(model.getProviderId());
        if (providerEntity == null) {
            throw new RuntimeException("提供商不存在: " + model.getProviderId());
        }

        try {
            // 解密API密钥
            String decryptedApiKey = providerEntity.getApiKey();
            if (decryptedApiKey != null) {
                decryptedApiKey = apiKeyCryptoUtil.decryptApiKey(decryptedApiKey);
            }
            
            // 构建配置Map（供应商连接配置）
            Map<String, Object> providerConfig = Map.of(
                "api_key", decryptedApiKey != null ? decryptedApiKey : "",
                "base_url", providerEntity.getBaseUrl() != null ? providerEntity.getBaseUrl() : "",
                "timeout", providerEntity.getTimeout() != null ? providerEntity.getTimeout() : 60,
                "max_retries", providerEntity.getMaxRetries() != null ? providerEntity.getMaxRetries() : 3
            );
            
            // 构建设置Map（模型参数）
            Map<String, Object> providerSettings = Map.of(
                "model", model.getModelCode() != null ? model.getModelCode() : "",
                "temperature", model.getTemperature() != null ? model.getTemperature() : 0.7,
                "max_tokens", model.getMaxTokens() != null ? model.getMaxTokens() : 2048,
                "top_p", model.getTopP() != null ? model.getTopP() : 1.0
            );

            // 创建Provider实例
            Provider provider = ProviderFactory.createProvider(
                providerEntity.getProviderType().name(),
                providerConfig,
                providerSettings
            );

            // 构建请求
            ProviderRequest request = buildRequest(conversation, providerSettings);

            // 调用Provider
            return provider.chat(request);
        } catch (Exception e) {
            log.error("对话调用失败: modelId={}", modelId, e);
            throw new RuntimeException("对话调用失败: " + e.getMessage(), e);
        }
    }

    /**
     * 异步对话调用
     *
     * @param modelId 模型ID
     * @param conversation 对话上下文
     * @return 异步响应结果
     */
    public CompletableFuture<LLMResponse> asyncChat(Long modelId, Conversation conversation) {
        return CompletableFuture.supplyAsync(() -> chat(modelId, conversation));
    }

    /**
     * 流式对话调用
     *
     * @param modelId 模型ID
     * @param conversation 对话上下文
     * @param consumer 响应数据消费者
     */
    public void streamChat(Long modelId, Conversation conversation, java.util.function.Consumer<LLMResponse> consumer) {
        // 获取模型信息
        AiModel model = aiModelService.getById(modelId);
        if (model == null) {
            throw new RuntimeException("模型不存在: " + modelId);
        }

        // 获取供应商信息
        AiProvider providerEntity = aiProviderService.getById(model.getProviderId());
        if (providerEntity == null) {
            throw new RuntimeException("提供商不存在: " + model.getProviderId());
        }

        try {
            // 解密API密钥
            String decryptedApiKey = providerEntity.getApiKey();
            if (decryptedApiKey != null) {
                decryptedApiKey = apiKeyCryptoUtil.decryptApiKey(decryptedApiKey);
            }
            
            // 构建配置Map（供应商连接配置）
            Map<String, Object> providerConfig = Map.of(
                "api_key", decryptedApiKey != null ? decryptedApiKey : "",
                "base_url", providerEntity.getBaseUrl() != null ? providerEntity.getBaseUrl() : "",
                "timeout", providerEntity.getTimeout() != null ? providerEntity.getTimeout() : 60,
                "max_retries", providerEntity.getMaxRetries() != null ? providerEntity.getMaxRetries() : 3
            );
            
            // 构建设置Map（模型参数）
            Map<String, Object> providerSettings = Map.of(
                "model", model.getModelCode() != null ? model.getModelCode() : "",
                "temperature", model.getTemperature() != null ? model.getTemperature() : 0.7,
                "max_tokens", model.getMaxTokens() != null ? model.getMaxTokens() : 2048,
                "top_p", model.getTopP() != null ? model.getTopP() : 1.0
            );

            // 创建Provider实例
            Provider provider = ProviderFactory.createProvider(
                providerEntity.getProviderType().name(),
                providerConfig,
                providerSettings
            );

            // 构建请求
            ProviderRequest request = buildRequest(conversation, providerSettings);

            // 调用Provider的流式方法
            provider.streamChat(request, consumer);
        } catch (Exception e) {
            log.error("流式对话调用失败: modelId={}", modelId, e);
            throw new RuntimeException("流式对话调用失败: " + e.getMessage(), e);
        }
    }

    /**
     * 检查服务是否可用
     *
     * @param providerId 提供商ID
     * @return 是否可用
     */
    public boolean isAvailable(Long providerId) {
        try {
            AiProvider providerEntity = aiProviderService.getById(providerId);
            if (providerEntity == null || !providerEntity.getEnabled()) {
                return false;
            }

            // 解密API密钥
            String decryptedApiKey = providerEntity.getApiKey();
            if (decryptedApiKey != null) {
                decryptedApiKey = apiKeyCryptoUtil.decryptApiKey(decryptedApiKey);
            }
            
            // 构建配置Map（供应商连接配置）
            Map<String, Object> providerConfig = Map.of(
                "api_key", decryptedApiKey != null ? decryptedApiKey : "",
                "base_url", providerEntity.getBaseUrl() != null ? providerEntity.getBaseUrl() : "",
                "timeout", providerEntity.getTimeout() != null ? providerEntity.getTimeout() : 60,
                "max_retries", providerEntity.getMaxRetries() != null ? providerEntity.getMaxRetries() : 3
            );

            // 创建Provider实例（不需要模型参数）
            Provider provider = ProviderFactory.createProvider(
                providerEntity.getProviderType().name(),
                providerConfig,
                Map.of() // 空的settings，测试连接不需要模型参数
            );

            // 测试连接
            provider.test();
            return true;
        } catch (Exception e) {
            log.error("检查服务可用性失败: providerId={}", providerId, e);
            return false;
        }
    }

    /**
     * 构建Provider请求
     *
     * @param conversation 对话上下文
     * @param providerSettings 提供商设置
     * @return Provider请求
     */
    private ProviderRequest buildRequest(Conversation conversation, Map<String, Object> providerSettings) {
        ProviderRequest request = new ProviderRequest();
        request.setConversation(conversation);
        
        // 从providerSettings中获取模型名称
        if (providerSettings != null && providerSettings.containsKey("model")) {
            request.setModel((String) providerSettings.get("model"));
        }
        
        // 设置其他参数
        if (providerSettings != null) {
            if (providerSettings.containsKey("temperature")) {
                request.setTemperature(((Number) providerSettings.get("temperature")).doubleValue());
            }
            if (providerSettings.containsKey("max_tokens")) {
                request.setMaxTokens(((Number) providerSettings.get("max_tokens")).intValue());
            }
            if (providerSettings.containsKey("top_p")) {
                request.setTopP(((Number) providerSettings.get("top_p")).doubleValue());
            }
        }
        
        return request;
    }

}

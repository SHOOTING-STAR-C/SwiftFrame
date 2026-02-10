package com.star.swiftAi.core.adapter;

import com.star.swiftAi.client.AiClient;
import com.star.swiftAi.core.model.LLMResponse;
import com.star.swiftAi.core.model.Message;
import com.star.swiftAi.core.model.ProviderRequest;
import com.star.swiftAi.core.provider.Provider;
import com.star.swiftAi.core.request.ChatRequest;
import com.star.swiftAi.core.response.ChatResponse;
import com.star.swiftAi.core.response.ChatResponse.Choice;
import com.star.swiftAi.core.response.ChatResponse.Usage;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Provider到AIClient的适配器
 * 将原有的Provider接口适配到AIClient接口，以便在流水线中使用
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
public class ProviderAiClientAdapter implements AiClient {

    private final Provider provider;
    private final String baseUrl;
    private final String model;
    private boolean healthy = true;

    public ProviderAiClientAdapter(Provider provider, String baseUrl, String model) {
        this.provider = provider;
        this.baseUrl = baseUrl;
        this.model = model;
    }

    @Override
    public ChatResponse chat(ChatRequest request) {
        try {
            log.debug("ProviderAiClientAdapter.chat: model={}", request.getModel());
            
            // 构建ProviderRequest
            ProviderRequest providerRequest = buildProviderRequest(request);
            
            // 调用Provider
            LLMResponse llmResponse = provider.chat(providerRequest);
            
            // 转换为ChatResponse
            return convertToChatResponse(llmResponse);
            
        } catch (Exception e) {
            log.error("ProviderAiClientAdapter.chat failed", e);
            this.healthy = false;
            throw new RuntimeException("AI调用失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void streamChat(ChatRequest request, Consumer<ChatResponse> consumer) {
        try {
            log.debug("ProviderAiClientAdapter.streamChat: model={}", request.getModel());
            
            // 构建ProviderRequest
            ProviderRequest providerRequest = buildProviderRequest(request);
            
            // 调用Provider的流式接口
            provider.streamChatRealtime(providerRequest, llmResponse -> {
                ChatResponse response = convertToChatResponse(llmResponse);
                consumer.accept(response);
            });
            
        } catch (Exception e) {
            log.error("ProviderAiClientAdapter.streamChat failed", e);
            this.healthy = false;
            throw new RuntimeException("AI流式调用失败: " + e.getMessage(), e);
        }
    }

    @Override
    public CompletableFuture<ChatResponse> asyncChat(ChatRequest request) {
        return CompletableFuture.supplyAsync(() -> chat(request));
    }

    @Override
    public boolean isAvailable() {
        return healthy && provider != null;
    }

    @Override
    public ClientConfig getConfig() {
        return new ClientConfig("provider", baseUrl, model, healthy);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 提取最后一条用户消息
     */
    private String extractLastUserMessage(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return "";
        }
        
        // 从后往前找第一条用户消息
        for (int i = messages.size() - 1; i >= 0; i--) {
            Message msg = messages.get(i);
            if ("user".equalsIgnoreCase(msg.getRole()) && msg.getContent() != null) {
                return msg.getContent().toString();
            }
        }
        
        return "";
    }

    /**
     * 构建ProviderRequest
     */
    private ProviderRequest buildProviderRequest(ChatRequest request) {
        ProviderRequest providerRequest = new ProviderRequest();
        
        providerRequest.setPrompt(extractLastUserMessage(request.getMessages()));
        providerRequest.setModel(request.getModel());
        providerRequest.setContexts(convertToContexts(request.getMessages()));
        
        return providerRequest;
    }

    /**
     * 转换为Provider需要的contexts格式
     */
    private List<java.util.Map<String, Object>> convertToContexts(List<Message> messages) {
        if (messages == null) {
            return Collections.emptyList();
        }
        
        return messages.stream()
                .map(msg -> {
                    java.util.Map<String, Object> map = new java.util.HashMap<>();
                    map.put("role", msg.getRole());
                    map.put("content", msg.getContent());
                    return map;
                })
                .toList();
    }

    /**
     * 将LLMResponse转换为ChatResponse
     */
    private ChatResponse convertToChatResponse(LLMResponse llmResponse) {
        ChatResponse response = new ChatResponse();
        response.setId(llmResponse.getId() != null ? llmResponse.getId() : "chat-" + System.currentTimeMillis());
        response.setObject("chat.completion");
        response.setCreated(System.currentTimeMillis());
        response.setModel(model);
        
        // 构建Choice
        Message message = Message.builder()
                .role("assistant")
                .content(llmResponse.getContent())
                .build();
        
        // 构建增量消息（流式响应）
        Message delta = null;
        if (llmResponse.getDelta() != null) {
            delta = Message.builder()
                    .role("assistant")
                    .content(llmResponse.getDelta())
                    .build();
        }
        
        Choice choice = Choice.builder()
                .index(0)
                .finishReason(llmResponse.isFinished() ? "stop" : null)
                .message(message)
                .delta(delta)
                .build();
        
        response.setChoices(Collections.singletonList(choice));
        
        // 构建Usage
        if (llmResponse.getUsage() != null) {
            Usage usage = Usage.builder()
                    .promptTokens(llmResponse.getUsage().getInputOther())
                    .completionTokens(llmResponse.getUsage().getOutput())
                    .totalTokens(llmResponse.getUsage().getTotal())
                    .build();
            response.setUsage(usage);
        }
        
        return response;
    }
}
package com.star.swiftAi.service;

import com.star.swiftAi.client.AiClient;
import com.star.swiftAi.client.AiClientManager;
import com.star.swiftAi.core.model.Message;
import com.star.swiftAi.core.request.ChatRequest;
import com.star.swiftAi.core.response.ChatResponse;
import com.star.swiftAi.enums.AiProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AI服务示例
 * 演示如何使用AI客户端进行各种AI调用
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private final AiClientManager aiClientManager;

    /**
     * 简单的文本对话
     */
    public String simpleChat(String prompt) {
        AiClient client = aiClientManager.getClient();
        
        ChatRequest request = ChatRequest.builder()
                .messages(Arrays.asList(
                        Message.system("You are a helpful assistant."),
                        Message.user(prompt)
                ))
                .temperature(0.7)
                .maxTokens(1000)
                .build();
        
        ChatResponse response = client.chat(request);
        return response.getChoices().get(0).getMessage().getContent().toString();
    }

    /**
     * 使用指定的AI服务商进行对话
     */
    public String chatWithProvider(String provider, String prompt) {
        AiClient client = aiClientManager.getClient(provider);
        
        ChatRequest request = ChatRequest.builder()
                .messages(Arrays.asList(
                        Message.system("You are a helpful assistant."),
                        Message.user(prompt)
                ))
                .temperature(0.7)
                .build();
        
        ChatResponse response = client.chat(request);
        return response.getChoices().get(0).getMessage().getContent().toString();
    }

    /**
     * 流式对话示例
     */
    public void streamChat(String prompt, java.util.function.Consumer<String> chunkConsumer) {
        AiClient client = aiClientManager.getClient();
        
        ChatRequest request = ChatRequest.builder()
                .messages(Arrays.asList(
                        Message.system("You are a helpful assistant."),
                        Message.user(prompt)
                ))
                .temperature(0.7)
                .stream(true)
                .build();
        
        client.streamChat(request, response -> {
            if (response.getChoices() != null && !response.getChoices().isEmpty()) {
                String content = response.getChoices().get(0).getDelta().getContent().toString();
                if (content != null && !content.isEmpty()) {
                    chunkConsumer.accept(content);
                }
            }
        });
    }

    /**
     * 获取所有可用的AI服务商信息
     */
    public Map<String, Boolean> getAvailableProviders() {
        return aiClientManager.getAllClients().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().isAvailable()
                ));
    }
}

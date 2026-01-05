package com.star.swiftAi.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.star.swiftAi.core.request.ChatRequest;
import com.star.swiftAi.core.response.ChatResponse;
import com.star.swiftAi.enums.AiProviderEnum;
import com.star.swiftAi.exception.AiException;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * OpenAI 兼容客户端实现
 * 支持 DeepSeek 等 OpenAI 兼容格式的 AI 服务
 */
@Slf4j
public class OpenAiCompatibleClient implements AiClient {

    private final Config config;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Data
    @Builder
    public static class Config {
        private String apiKey;
        private String baseUrl;
        private String model;
        private AiProviderEnum provider;
        private Duration timeout;
        private Integer maxRetries;
        private Double defaultTemperature;
    }

    public OpenAiCompatibleClient(Config config) {
        this.config = config;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Optional.ofNullable(config.getTimeout())
                        .orElse(Duration.ofSeconds(30)))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public ChatResponse chat(ChatRequest request) {
        try {
            if (request.getModel() == null) {
                request.setModel(config.getModel());
            }

            String requestBody = objectMapper.writeValueAsString(request);
            log.debug("Sending request to {}: {}", config.getProvider(), requestBody);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(config.getBaseUrl() + "/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + config.getApiKey())
                    .timeout(Optional.ofNullable(config.getTimeout())
                            .orElse(Duration.ofSeconds(30)))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, 
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                ChatResponse chatResponse = objectMapper.readValue(response.body(), ChatResponse.class);
                log.debug("Received response from {}: {}", config.getProvider(), response.body());
                return chatResponse;
            } else {
                log.error("API call failed for {}: status={}, body={}", 
                        config.getProvider(), response.statusCode(), response.body());
                throw new AiException(
                        String.format("API call failed: %s", response.body()),
                        config.getProvider().getName(),
                        String.valueOf(response.statusCode()),
                        response.statusCode()
                );
            }
        } catch (IOException | InterruptedException e) {
            log.error("Request failed for " + config.getProvider(), e);
            throw new AiException(
                    String.format("Request failed: %s", e.getMessage()),
                    config.getProvider().getName(),
                    e
            );
        }
    }

    @Override
    public void streamChat(ChatRequest request, Consumer<ChatResponse> consumer) {
        try {
            if (request.getModel() == null) {
                request.setModel(config.getModel());
            }
            request.setStream(true);

            String requestBody = objectMapper.writeValueAsString(request);
            log.debug("Sending streaming request to {}: {}", config.getProvider(), requestBody);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(config.getBaseUrl() + "/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + config.getApiKey())
                    .timeout(Optional.ofNullable(config.getTimeout())
                            .orElse(Duration.ofSeconds(30)))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<Stream<String>> response = httpClient.send(httpRequest, 
                    HttpResponse.BodyHandlers.ofLines());
            
            try (Stream<String> lines = response.body()) {
                lines.forEach(line -> {
                    if (line.startsWith("data: ")) {
                        String data = line.substring(6);
                        if ("[DONE]".equals(data)) {
                            return;
                        }
                        try {
                            ChatResponse chunk = objectMapper.readValue(data, ChatResponse.class);
                            consumer.accept(chunk);
                        } catch (Exception e) {
                            log.error("Failed to parse streaming response chunk: {}", data, e);
                        }
                    }
                });
            }

        } catch (IOException | InterruptedException e) {
            log.error("Streaming request failed for " + config.getProvider(), e);
            throw new AiException(
                    String.format("Streaming request failed: %s", e.getMessage()),
                    config.getProvider().getName(),
                    e
            );
        }
    }

    @Override
    public CompletableFuture<ChatResponse> asyncChat(ChatRequest request) {
        return CompletableFuture.supplyAsync(() -> chat(request));
    }

    @Override
    public boolean isAvailable() {
        try {
            ChatRequest testRequest = ChatRequest.builder()
                    .model(config.getModel())
                    .messages(java.util.Arrays.asList(
                            com.star.swiftAi.core.model.Message.system("Test")
                    ))
                    .maxTokens(1)
                    .build();

            ChatResponse response = chat(testRequest);
            return response != null && response.getChoices() != null && !response.getChoices().isEmpty();
        } catch (Exception e) {
            log.debug("Health check failed for {}: {}", config.getProvider(), e.getMessage());
            return false;
        }
    }

    @Override
    public ClientConfig getConfig() {
        return new ClientConfig(
                config.getProvider().getName(),
                config.getBaseUrl(),
                config.getModel(),
                isAvailable()
        );
    }
}

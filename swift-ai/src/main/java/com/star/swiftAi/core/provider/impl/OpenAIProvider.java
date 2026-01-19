package com.star.swiftAi.core.provider.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.star.swiftAi.core.annotation.ProviderAdapter;
import com.star.swiftAi.core.model.*;
import com.star.swiftAi.core.provider.Provider;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * OpenAI提供商实现
 * 支持OpenAI API及其兼容接口
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@ProviderAdapter(
    typeName = "openai",
    desc = "OpenAI提供商，支持GPT系列模型",
    displayName = "OpenAI"
)
public class OpenAIProvider extends Provider {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public OpenAIProvider(Map<String, Object> providerConfig, Map<String, Object> providerSettings) {
        super(providerConfig, providerSettings);
        
        // 初始化HttpClient
        int timeout = getTimeout();
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(timeout))
            .build();
        
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * 获取超时时间
     */
    private int getTimeout() {
        Object timeoutObj = providerConfig.get("timeout");
        if (timeoutObj instanceof Number) {
            return ((Number) timeoutObj).intValue();
        }
        return 60; // 默认60秒
    }
    
    /**
     * 获取Base URL
     */
    private String getBaseUrl() {
        Object baseUrlObj = providerConfig.get("base_url");
        if (baseUrlObj != null) {
            String baseUrl = baseUrlObj.toString();
            // 移除末尾的斜杠
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }
            return baseUrl;
        }
        return "https://api.openai.com/v1";
    }

    @Override
    public String getCurrentKey() {
        return (String) providerConfig.get("api_key");
    }

    @Override
    public List<String> getKeys() {
        String apiKey = getCurrentKey();
        return apiKey != null ? Collections.singletonList(apiKey) : Collections.emptyList();
    }

    @Override
    public void setKey(String key) {
        providerConfig.put("api_key", key);
    }

    @Override
    public List<String> getModels() throws Exception {
        String baseUrl = getBaseUrl();
        String apiKey = getCurrentKey();
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/models"))
            .header("Authorization", "Bearer " + apiKey)
            .GET()
            .build();
        
        HttpResponse<String> response = httpClient.send(
            request,
            HttpResponse.BodyHandlers.ofString()
        );
        
        if (response.statusCode() != 200) {
            throw new RuntimeException("获取模型列表失败: " + response.body());
        }
        
        JsonNode root = objectMapper.readTree(response.body());
        JsonNode data = root.get("data");
        
        List<String> models = new ArrayList<>();
        if (data != null && data.isArray()) {
            for (JsonNode modelNode : data) {
                String modelId = modelNode.get("id").asText();
                models.add(modelId);
            }
        }
        
        return models;
    }

    @Override
    public LLMResponse textChat(
        String prompt,
        String sessionId,
        List<String> imageUrls,
        ToolSet funcTool,
        List<Map<String, Object>> contexts,
        String systemPrompt,
        List<ToolCallsResult> toolCallsResult,
        String model,
        List<ContentPart> extraUserContentParts
    ) throws Exception {
        log.info("OpenAI提供商执行textChat请求: model={}", model);
        
        String baseUrl = getBaseUrl();
        String apiKey = getCurrentKey();
        
        // 构建请求体
        Map<String, Object> requestBody = buildChatRequestBody(
            prompt, systemPrompt, contexts, model, funcTool, toolCallsResult
        );
        
        String jsonBody = objectMapper.writeValueAsString(requestBody);
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/chat/completions"))
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();
        
        HttpResponse<String> response = httpClient.send(
            request,
            HttpResponse.BodyHandlers.ofString()
        );
        
        if (response.statusCode() != 200) {
            throw new RuntimeException("OpenAI API调用失败: " + response.body());
        }
        
        return parseChatResponse(response.body());
    }
    
    /**
     * 构建聊天请求体
     */
    private Map<String, Object> buildChatRequestBody(
        String prompt,
        String systemPrompt,
        List<Map<String, Object>> contexts,
        String model,
        ToolSet funcTool,
        List<ToolCallsResult> toolCallsResult
    ) {
        Map<String, Object> body = new java.util.HashMap<>();
        body.put("model", model);
        
        // 构建消息列表
        List<Map<String, Object>> messages = new ArrayList<>();
        
        // 添加系统提示
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            Map<String, Object> systemMsg = new java.util.HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", systemPrompt);
            messages.add(systemMsg);
        }
        
        // 添加历史上下文
        if (contexts != null && !contexts.isEmpty()) {
            messages.addAll(contexts);
        }
        
        // 添加用户消息
        Map<String, Object> userMsg = new java.util.HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", prompt);
        messages.add(userMsg);
        
        body.put("messages", messages);
        
        // 添加工具（如果有）
        if (funcTool != null && funcTool.getTools() != null && !funcTool.getTools().isEmpty()) {
            body.put("tools", funcTool.getTools());
        }
        
        // 添加工具调用结果（如果有）
        if (toolCallsResult != null && !toolCallsResult.isEmpty()) {
            for (ToolCallsResult result : toolCallsResult) {
                messages.addAll(result.toOpenAiMessages());
            }
        }
        
        return body;
    }
    
    /**
     * 解析聊天响应
     */
    private LLMResponse parseChatResponse(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);
        
        LLMResponse response = new LLMResponse();
        
        // 获取内容
        JsonNode choices = root.get("choices");
        if (choices != null && choices.isArray() && choices.size() > 0) {
            JsonNode choice = choices.get(0);
            JsonNode message = choice.get("message");
            if (message != null) {
                JsonNode content = message.get("content");
                if (content != null) {
                    response.setContent(content.asText());
                }
                
                // 获取工具调用
                JsonNode toolCalls = message.get("tool_calls");
                if (toolCalls != null && toolCalls.isArray()) {
                    for (JsonNode toolCall : toolCalls) {
                        JsonNode function = toolCall.get("function");
                        if (function != null) {
                            String toolCallId = toolCall.get("id") != null ? toolCall.get("id").asText() : "";
                            String toolName = function.get("name") != null ? function.get("name").asText() : "";
                            String toolArgs = function.get("arguments") != null ? function.get("arguments").asText() : "{}";
                            
                            response.getToolsCallIds().add(toolCallId);
                            response.getToolsCallName().add(toolName);
                            response.getToolsCallArgs().add(objectMapper.readValue(toolArgs, Map.class));
                        }
                    }
                }
            }
        }
        
        // 获取使用量
        JsonNode usage = root.get("usage");
        if (usage != null) {
            TokenUsage tokenUsage = new TokenUsage();
            tokenUsage.setInputOther(usage.get("prompt_tokens").asInt());
            tokenUsage.setOutput(usage.get("completion_tokens").asInt());
            response.setUsage(tokenUsage);
        }
        
        return response;
    }

    @Override
    public List<LLMResponse> textChatStream(
        String prompt,
        String sessionId,
        List<String> imageUrls,
        ToolSet funcTool,
        List<Map<String, Object>> contexts,
        String systemPrompt,
        List<ToolCallsResult> toolCallsResult,
        String model
    ) throws Exception {
        log.info("OpenAI提供商执行textChatStream请求: model={}", model);
        
        String baseUrl = getBaseUrl();
        String apiKey = getCurrentKey();
        
        // 构建请求体
        Map<String, Object> requestBody = buildChatRequestBody(
            prompt, systemPrompt, contexts, model, funcTool, toolCallsResult
        );
        requestBody.put("stream", true);
        
        String jsonBody = objectMapper.writeValueAsString(requestBody);
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/chat/completions"))
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();
        
        HttpResponse<String> response = httpClient.send(
            request,
            HttpResponse.BodyHandlers.ofString()
        );
        
        if (response.statusCode() != 200) {
            throw new RuntimeException("OpenAI流式API调用失败: " + response.body());
        }
        
        return parseStreamResponse(response.body());
    }
    
    /**
     * 解析流式响应
     */
    private List<LLMResponse> parseStreamResponse(String responseBody) throws Exception {
        List<LLMResponse> responses = new ArrayList<>();
        
        // SSE格式：每行以"data: "开头
        String[] lines = responseBody.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("data: ")) {
                String data = line.substring(6);
                
                // 结束标记
                if ("[DONE]".equals(data)) {
                    LLMResponse finalResponse = new LLMResponse();
                    finalResponse.setFinished(true);
                    responses.add(finalResponse);
                    break;
                }
                
                try {
                    JsonNode root = objectMapper.readTree(data);
                    LLMResponse response = new LLMResponse();
                    
                    JsonNode choices = root.get("choices");
                    if (choices != null && choices.isArray() && choices.size() > 0) {
                        JsonNode choice = choices.get(0);
                        JsonNode delta = choice.get("delta");
                        
                        if (delta != null) {
                            JsonNode content = delta.get("content");
                            if (content != null) {
                                String contentText = content.asText();
                                response.setContent(contentText);
                                response.setDelta(contentText);
                            }
                        }
                        
                        String finishReason = choice.get("finish_reason") != null ? 
                            choice.get("finish_reason").asText() : null;
                        response.setFinished("stop".equals(finishReason));
                    }
                    
                    responses.add(response);
                } catch (Exception e) {
                    log.error("解析流式响应失败: {}", data, e);
                }
            }
        }
        
        return responses;
    }

    @Override
    public void textChatStreamRealtime(
        String prompt,
        String sessionId,
        List<String> imageUrls,
        ToolSet funcTool,
        List<Map<String, Object>> contexts,
        String systemPrompt,
        List<ToolCallsResult> toolCallsResult,
        String model,
        java.util.function.Consumer<LLMResponse> consumer
    ) throws Exception {
        log.info("OpenAI提供商执行textChatStreamRealtime请求: model={}", model);
        
        String baseUrl = getBaseUrl();
        String apiKey = getCurrentKey();
        
        // 构建请求体
        Map<String, Object> requestBody = buildChatRequestBody(
            prompt, systemPrompt, contexts, model, funcTool, toolCallsResult
        );
        requestBody.put("stream", true);
        
        String jsonBody = objectMapper.writeValueAsString(requestBody);
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/chat/completions"))
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();
        
        // 使用流式处理，逐行接收SSE数据
        java.util.concurrent.Flow.Subscriber<String> subscriber = new java.util.concurrent.Flow.Subscriber<String>() {
            private java.util.concurrent.Flow.Subscription subscription;
            private int chunkCount = 0;
            private boolean finished = false;
            
            @Override
            public void onSubscribe(java.util.concurrent.Flow.Subscription subscription) {
                log.info("流式响应订阅已建立");
                this.subscription = subscription;
                subscription.request(1); // 请求第一行数据
            }
            
            @Override
            public void onNext(String line) {
                try {
                    log.debug("接收到流式数据行: {}", line);
                    
                    // 处理SSE数据
                    line = line.trim();
                    if (line.startsWith("data: ")) {
                        String data = line.substring(6);
                        log.info("解析SSE数据块 #{}: {}", ++chunkCount, data);
                        
                        // 结束标记
                        if ("[DONE]".equals(data)) {
                            log.info("接收到流式结束标记[DONE]");
                            // 只在还未发送finished响应时才发送
                            if (!finished) {
                                finished = true;
                                LLMResponse finalResponse = new LLMResponse();
                                finalResponse.setFinished(true);
                                consumer.accept(finalResponse);
                            }
                            return;
                        }
                        
                        try {
                            JsonNode root = objectMapper.readTree(data);
                            LLMResponse response = new LLMResponse();
                            
                            JsonNode choices = root.get("choices");
                            if (choices != null && choices.isArray() && choices.size() > 0) {
                                JsonNode choice = choices.get(0);
                                JsonNode delta = choice.get("delta");
                                
                                if (delta != null) {
                                    JsonNode content = delta.get("content");
                                    if (content != null) {
                                        String contentText = content.asText();
                                        response.setContent(contentText);
                                        response.setDelta(contentText);
                                        log.info("数据块内容: '{}'", contentText);
                                    }
                                }
                                
                                String finishReason = choice.get("finish_reason") != null ? 
                                    choice.get("finish_reason").asText() : null;
                                
                                // 只在finish_reason为stop且还未发送finished响应时才设置finished=true
                                if ("stop".equals(finishReason) && !finished) {
                                    finished = true;
                                    response.setFinished(true);
                                }
                            }
                            
                            // 立即传递给消费者
                            log.info("立即传递响应块给consumer, finished={}", response.isFinished());
                            consumer.accept(response);
                        } catch (Exception e) {
                            log.error("解析流式响应失败: {}", data, e);
                        }
                    }
                } finally {
                    subscription.request(1); // 请求下一行数据
                }
            }
            
            @Override
            public void onError(Throwable throwable) {
                log.error("流式响应发生错误", throwable);
                // 发送一个finished=true的响应以正确结束流
                LLMResponse errorResponse = new LLMResponse();
                errorResponse.setFinished(true);
                consumer.accept(errorResponse);
            }
            
            @Override
            public void onComplete() {
                log.info("流式响应接收完成");
            }
        };
        
        HttpResponse<Void> response = httpClient.send(
            request,
            HttpResponse.BodyHandlers.fromLineSubscriber(subscriber)
        );
        
        if (response.statusCode() != 200) {
            throw new RuntimeException("OpenAI流式API调用失败: " + response.statusCode());
        }
    }

    @Override
    public ProviderMeta meta() {
        ProviderMeta meta = new ProviderMeta();
        meta.setId("openai");
        meta.setType("openai");
        meta.setModel(getModel());
        return meta;
    }

    @Override
    public void test(String model) {
        log.info("OpenAI提供商测试连接: model={}", model);
        
        try {
            String baseUrl = getBaseUrl();
            String apiKey = getCurrentKey();
            
            // 如果没有指定模型，从模型列表中获取第一个
            if (model == null || model.isEmpty()) {
                HttpRequest modelsRequest = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/models"))
                    .header("Authorization", "Bearer " + apiKey)
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();
                
                HttpResponse<String> modelsResponse = httpClient.send(
                    modelsRequest,
                    HttpResponse.BodyHandlers.ofString()
                );
                
                if (modelsResponse.statusCode() == 200) {
                    JsonNode root = objectMapper.readTree(modelsResponse.body());
                    JsonNode data = root.get("data");
                    if (data != null && data.isArray() && data.size() > 0) {
                        model = data.get(0).get("id").asText();
                        log.info("使用第一个模型进行测试: {}", model);
                    }
                }
            }
            
            // 如果还是没有模型，使用默认值
            if (model == null || model.isEmpty()) {
                model = "gpt-3.5-turbo";
                log.warn("无法获取模型列表，使用默认模型: {}", model);
            }
            
            // 使用模型测试连接
            Map<String, Object> requestBody = new java.util.HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", List.of(
                Map.of("role", "user", "content", "Hi")
            ));
            requestBody.put("max_tokens", 5);
            
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/chat/completions"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .timeout(Duration.ofSeconds(10))
                .build();
            
            HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
            );
            
            if (response.statusCode() == 200) {
                log.info("OpenAI提供商连接测试成功");
            } else {
                throw new RuntimeException("OpenAI提供商连接测试失败: " + response.body());
            }
        } catch (Exception e) {
            log.error("OpenAI提供商连接测试失败", e);
            throw new RuntimeException("OpenAI提供商连接测试失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取默认配置模板
     *
     * @return 默认配置模板
     */
    public static Map<String, Object> getDefaultConfigTemplate() {
        return Map.of(
            "api_key", "",
            "base_url", "https://api.openai.com/v1",
            "timeout", 60,
            "max_retries", 3
        );
    }
}

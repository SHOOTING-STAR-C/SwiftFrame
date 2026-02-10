package com.star.swiftAi.service.impl;

import com.star.swiftAi.core.adapter.MessageChainAdapter;
import com.star.swiftAi.core.adapter.ProviderAiClientAdapter;
import com.star.swiftAi.core.factory.MessagePipelineFactory;
import com.star.swiftAi.core.model.*;
import com.star.swiftAi.core.pipeline.ProcessingContext;
import com.star.swiftAi.core.provider.Provider;
import com.star.swiftAi.core.request.ChatRequest;
import com.star.swiftAi.core.response.ChatResponse;
import com.star.swiftAi.client.AiClient;
import com.star.swiftAi.dto.ChatRequestDTO;
import com.star.swiftAi.dto.ChatResponseDTO;
import com.star.swiftAi.dto.StreamChatResponseDTO;
import com.star.swiftAi.dto.ImportChatRequestDTO;
import com.star.swiftAi.dto.ChatSessionDataDTO;
import com.star.swiftAi.dto.MessageDataDTO;
import com.star.swiftAi.dto.MessageDTO;
import com.star.swiftAi.service.AiChatService;
import com.star.swiftAi.service.AiChatSessionService;
import com.star.swiftAi.service.AiChatMessageService;
import com.star.swiftAi.service.AiModelService;
import com.star.swiftAi.service.AiProviderService;
import com.star.swiftAi.service.AiSystemPromptService;
import com.star.swiftAi.entity.*;
import com.star.swiftAi.core.factory.ProviderFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * AI聊天服务实现
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatServiceImpl implements AiChatService {

    private final AiChatSessionService aiChatSessionService;
    private final AiChatMessageService aiChatMessageService;
    private final AiModelService aiModelService;
    private final AiProviderService aiProviderService;
    private final AiSystemPromptService aiSystemPromptService;
    private final com.star.swiftAi.util.ApiKeyCryptoUtil apiKeyCryptoUtil;

    @Transactional(rollbackFor = Exception.class)
    public ChatResponseDTO chat(ChatRequestDTO request, String userId) {
        AiModel model = validateAndGetModel(request.getModelId());
        AiProvider provider = validateAndGetProvider(model.getProviderId());
        AiChatSession session = getOrCreateSession(request, model, userId);
        
        // 使用流水线处理消息
        ChatResponse chatResponse = processWithPipeline(session, request, model, provider);
        
        // 提取响应内容
        String content = extractContentFromResponse(chatResponse);
        int totalTokens = extractTokensFromResponse(chatResponse);
        
        // 保存消息
        saveUserMessage(session, request.getMessage(), totalTokens);
        AiChatMessage assistantMessage = saveAssistantMessage(session, content, totalTokens);
        
        return buildChatResponse(session, content, totalTokens, assistantMessage);
    }

    @Transactional(rollbackFor = Exception.class)
    public void streamChat(ChatRequestDTO request, String userId, java.util.function.BiConsumer<String, LLMResponse> consumer) throws Exception {
        AiModel model = validateAndGetModel(request.getModelId());
        AiProvider provider = validateAndGetProvider(model.getProviderId());
        AiChatSession session = getOrCreateSession(request, model, userId);
        
        // 保存用户消息
        aiChatMessageService.saveMessage(session.getSessionId(), "user", request.getMessage(), 0);

        String sessionId = session.getSessionId();
        log.info("流式调用AI: sessionId={}, model={}", sessionId, model.getModelCode());
        
        // 构建MessageChain
        MessageChain messageChain = buildMessageChain(session.getSessionId(), request.getSystemPromptId());
        messageChain.addUser(request.getMessage());
        
        // 使用辅助方法执行流式调用
        executeStreamChat(model, provider, messageChain, response -> {
            LLMResponse llmResponse = convertToLLMResponse(response);
            consumer.accept(sessionId, llmResponse);
        });
    }

    public void anonymousStreamChat(ChatRequestDTO request, java.util.function.Consumer<LLMResponse> consumer) throws Exception {
        AiModel model = validateAndGetModel(request.getModelId());
        AiProvider provider = validateAndGetProvider(model.getProviderId());

        log.info("匿名流式调用AI: model={}", model.getModelCode());
        
        // 构建MessageChain
        MessageChain messageChain = new MessageChain();
        messageChain.addUser(request.getMessage());
        
        // 使用辅助方法执行流式调用
        executeStreamChat(model, provider, messageChain, response -> {
            LLMResponse llmResponse = convertToLLMResponse(response);
            consumer.accept(llmResponse);
        });
    }

    public void streamChatWithoutDb(ChatRequestDTO request, String userId, String sessionId, Consumer<LLMResponse> consumer) throws Exception {
        AiModel model = validateAndGetModel(request.getModelId());
        AiProvider provider = validateAndGetProvider(model.getProviderId());

        log.info("流式调用AI（无DB）: sessionId={}, model={}", sessionId, model.getModelCode());
        
        // 构建消息链
        MessageChain messageChain = buildMessageChain(sessionId, request.getSystemPromptId());
        messageChain.addUser(request.getMessage());
        
        // 使用辅助方法执行流式调用
        executeStreamChat(model, provider, messageChain, response -> {
            LLMResponse llmResponse = convertToLLMResponse(response);
            consumer.accept(llmResponse);
        });
    }

    public void streamChatWithEmitter(ChatRequestDTO request, String userId, String sessionId, 
                                      SseEmitter emitter,
                                      AtomicReference<StringBuilder> fullContentRef,
                                      AtomicInteger totalOutputTokens,
                                      AtomicBoolean completed,
                                      Function<LLMResponse, StreamChatResponseDTO> converter) {
        try {
            AiModel model = validateAndGetModel(request.getModelId());
            AiProvider provider = validateAndGetProvider(model.getProviderId());

            log.info("流式调用AI（SSE）: sessionId={}, model={}", sessionId, model.getModelCode());
            
            // 构建消息链
            MessageChain messageChain = buildMessageChain(sessionId, request.getSystemPromptId());
            messageChain.addUser(request.getMessage());
            
            // 使用辅助方法执行流式调用
            executeStreamChat(model, provider, messageChain, response -> {
                LLMResponse llmResponse = convertToLLMResponse(response);
                handleStreamResponse(llmResponse, sessionId, emitter, fullContentRef, totalOutputTokens, completed, converter, true);
            });
            
        } catch (Exception e) {
            log.error("流式调用AI失败（SSE）: sessionId={}, error={}", sessionId, e.getMessage(), e);
            if (completed.compareAndSet(false, true)) {
                emitter.completeWithError(e);
            }
            throw new RuntimeException("调用AI模型失败: " + e.getMessage(), e);
        }
    }

    public void anonymousStreamChatWithEmitter(ChatRequestDTO request, 
                                               SseEmitter emitter,
                                               AtomicBoolean completed,
                                               Function<LLMResponse, StreamChatResponseDTO> converter) {
        try {
            AiModel model = validateAndGetModel(request.getModelId());
            AiProvider provider = validateAndGetProvider(model.getProviderId());

            log.info("匿名流式调用AI（SSE）: model={}", model.getModelCode());
            
            // 构建消息链
            MessageChain messageChain = new MessageChain();
            messageChain.addUser(request.getMessage());
            
            // 使用辅助方法执行流式调用
            executeStreamChat(model, provider, messageChain, response -> {
                LLMResponse llmResponse = convertToLLMResponse(response);
                handleStreamResponse(llmResponse, null, emitter, null, null, completed, converter, false);
            });
            
        } catch (Exception e) {
            log.error("匿名流式调用AI失败（SSE）: error={}", e.getMessage(), e);
            if (completed.compareAndSet(false, true)) {
                emitter.completeWithError(e);
            }
            throw new RuntimeException("调用AI模型失败: " + e.getMessage(), e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveAssistantMessage(String sessionId, String content, int tokensUsed) {
        aiChatMessageService.saveMessage(sessionId, "assistant", content, tokensUsed);
        log.info("保存AI助手消息: sessionId={}, contentLength={}, tokens={}", 
            sessionId, content != null ? content.length() : 0, tokensUsed);
    }

    @Transactional(rollbackFor = Exception.class, propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void saveAssistantMessageWithoutSecurity(String sessionId, String content, int tokensUsed) {
        aiChatMessageService.saveMessage(sessionId, "assistant", content, tokensUsed);
        log.info("保存AI助手消息（无安全上下文）: sessionId={}, contentLength={}, tokens={}", 
            sessionId, content != null ? content.length() : 0, tokensUsed);
    }

    @Transactional(rollbackFor = Exception.class)
    public String prepareSessionAndSaveUserMessage(ChatRequestDTO request, String userId) {
        AiModel model = validateAndGetModel(request.getModelId());
        AiChatSession session = getOrCreateSession(request, model, userId);
        
        aiChatMessageService.saveMessage(session.getSessionId(), "user", request.getMessage(), 0);
        
        log.info("会话准备完成: sessionId={}, userId={}", session.getSessionId(), userId);
        return session.getSessionId();
    }

    public ChatResponseDTO anonymousChat(ChatRequestDTO request) throws Exception {
        AiModel model = validateAndGetModel(request.getModelId());
        AiProvider provider = validateAndGetProvider(model.getProviderId());
        
        // 构建消息链
        MessageChain messageChain = new MessageChain();
        messageChain.addUser(request.getMessage());
        
        // 构建模型配置
        ModelConfig modelConfig = ModelConfig.builder()
                .model(model.getModelCode())
                .build();
        
        // 创建适配器
        Provider aiProvider = createProvider(provider);
        AiClient adapter = new ProviderAiClientAdapter(aiProvider, provider.getBaseUrl(), model.getModelCode());
        
        // 构建处理上下文
        ProcessingContext context = ProcessingContext.builder()
                .messageChain(messageChain)
                .modelConfig(modelConfig)
                .build();
        
        // 使用流水线处理
        com.star.swiftAi.core.pipeline.MessagePipeline pipeline = 
            MessagePipelineFactory.createStandardPipeline(adapter);
        
        ProcessingContext result = pipeline.process(context);
        
        // 提取响应
        Message responseMessage = result.getMessageChain().getLastAssistantMessage();
        String content = responseMessage != null ? responseMessage.getContent().toString() : "";
        int tokens = result.getMessageChain().getTotalTokens();
        
        ChatResponseDTO response = new ChatResponseDTO();
        response.setSessionId(null);
        response.setRole("assistant");
        response.setContent(content);
        response.setTokensUsed(tokens);
        
        log.info("匿名聊天成功: modelId={}, tokens={}", model.getId(), tokens);
        return response;
    }

    @Transactional(rollbackFor = Exception.class)
    public int importChatHistory(String userId, ImportChatRequestDTO request) {
        int importedCount = 0;
        
        for (ChatSessionDataDTO sessionData : request.getSessions()) {
            try {
                if (aiChatSessionService.getBySessionId(sessionData.getSessionId()) != null) {
                    log.warn("会话已存在，跳过导入: sessionId={}", sessionData.getSessionId());
                    continue;
                }
                
                AiChatSession session = new AiChatSession();
                session.setSessionId(sessionData.getSessionId());
                session.setUserId(userId);
                session.setModelId(sessionData.getModelId());
                session.setTitle(sessionData.getTitle());
                aiChatSessionService.save(session);
                
                for (MessageDataDTO messageData : sessionData.getMessages()) {
                    aiChatMessageService.saveMessage(
                        sessionData.getSessionId(),
                        messageData.getRole(),
                        messageData.getContent(),
                        0
                    );
                }
                
                importedCount++;
                log.info("成功导入会话: sessionId={}, messageCount={}", 
                    sessionData.getSessionId(), sessionData.getMessages().size());
                
            } catch (Exception e) {
                log.error("导入会话失败: sessionId={}, error={}", sessionData.getSessionId(), e.getMessage(), e);
            }
        }
        
        log.info("导入聊天记录完成: userId={}, importedCount={}", userId, importedCount);
        return importedCount;
    }

    // ==================== 私有辅助方法 ====================

    private AiModel validateAndGetModel(Long modelId) {
        AiModel model = aiModelService.getById(modelId);
        if (model == null) {
            throw new RuntimeException("模型不存在");
        }
        if (!model.getEnabled()) {
            throw new RuntimeException("模型未启用");
        }
        return model;
    }

    private AiProvider validateAndGetProvider(Long providerId) {
        AiProvider provider = aiProviderService.getById(providerId);
        if (provider == null) {
            throw new RuntimeException("供应商不存在");
        }
        if (!provider.getEnabled()) {
            throw new RuntimeException("供应商未启用");
        }
        return provider;
    }

    private AiChatSession getOrCreateSession(ChatRequestDTO request, AiModel model, String userId) {
        if (request.getSessionId() != null && !request.getSessionId().isEmpty()) {
            AiChatSession session = aiChatSessionService.getBySessionId(request.getSessionId());
            if (session == null) {
                throw new RuntimeException("会话不存在");
            }
            if (!session.getModelId().equals(model.getId())) {
                throw new RuntimeException("会话与模型不匹配");
            }
            return session;
        }
        
        String title = generateTitle(request.getMessage());
        return aiChatSessionService.createSession(userId, request.getModelId(), title);
    }

    private String generateTitle(String message) {
        if (message == null || message.isEmpty()) {
            return "新对话";
        }
        message = message.trim();
        if (message.length() > 20) {
            return message.substring(0, 20) + "...";
        }
        return message;
    }

    private void saveUserMessage(AiChatSession session, String message, int tokens) {
        aiChatMessageService.saveMessage(session.getSessionId(), "user", message, tokens);
        log.debug("保存用户消息: sessionId={}, tokens={}", session.getSessionId(), tokens);
    }

    private AiChatMessage saveAssistantMessage(AiChatSession session, String content, int tokens) {
        AiChatMessage assistantMessage = aiChatMessageService.saveMessage(
            session.getSessionId(), "assistant", content, tokens
        );
        log.debug("保存AI回复: sessionId={}, outputTokens={}", session.getSessionId(), tokens);
        return assistantMessage;
    }

    private String getSystemPrompt(Long systemPromptId) {
        if (systemPromptId == null) {
            return null;
        }
        
        AiSystemPrompt prompt = aiSystemPromptService.getById(systemPromptId);
        if (prompt == null) {
            throw new RuntimeException("系统提示词不存在");
        }
        if (!prompt.getEnabled()) {
            throw new RuntimeException("系统提示词未启用");
        }
        
        return prompt.getPromptContent();
    }

    private List<Map<String, Object>> buildContexts(String sessionId, Long systemPromptId) {
        List<MessageDTO> historyMessages = aiChatMessageService.getMessagesBySessionId(sessionId);
        String systemPrompt = getSystemPrompt(systemPromptId);
        
        List<Map<String, Object>> contexts = new ArrayList<>();
        
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            Map<String, Object> systemContext = new HashMap<>();
            systemContext.put("role", "system");
            systemContext.put("content", systemPrompt);
            contexts.add(systemContext);
        }
        
        for (MessageDTO message : historyMessages) {
            Map<String, Object> context = new HashMap<>();
            context.put("role", message.getRole());
            context.put("content", message.getContent());
            contexts.add(context);
        }
        
        return contexts;
    }

    private ChatResponseDTO buildChatResponse(AiChatSession session, String content, int tokens, AiChatMessage assistantMessage) {
        ChatResponseDTO response = new ChatResponseDTO();
        response.setSessionId(session.getSessionId());
        response.setMessageId(assistantMessage.getId());
        response.setRole("assistant");
        response.setContent(content);
        response.setTokensUsed(tokens);
        response.setCreatedAt(assistantMessage.getCreatedAt());
        
        log.info("聊天成功: sessionId={}, messageId={}, tokens={}", 
            session.getSessionId(), assistantMessage.getId(), tokens);
        
        return response;
    }

    private Provider createProvider(AiProvider provider) throws Exception {
        String decryptedApiKey = apiKeyCryptoUtil.decryptApiKeyString(provider.getApiKey());
        
        Map<String, Object> providerConfig = new HashMap<>();
        providerConfig.put("api_key", decryptedApiKey);
        providerConfig.put("base_url", provider.getBaseUrl());
        providerConfig.put("timeout", 60);
        
        return ProviderFactory.createProvider(provider.getProviderCode(), providerConfig, new HashMap<>());
    }

    /**
     * 创建AI客户端适配器
     */
    private AiClient createAiClientAdapter(AiProvider provider, String modelCode) throws Exception {
        Provider aiProvider = createProvider(provider);
        return new ProviderAiClientAdapter(aiProvider, provider.getBaseUrl(), modelCode);
    }

    /**
     * 构建流式ChatRequest
     */
    private ChatRequest buildStreamChatRequest(String modelCode, List<Message> messages) {
        return ChatRequest.builder()
                .model(modelCode)
                .messages(messages)
                .stream(true)
                .build();
    }

    /**
     * 执行流式调用
     */
    private void executeStreamChat(AiModel model, AiProvider provider, MessageChain messageChain, 
                                   Consumer<ChatResponse> responseHandler) throws Exception {
        AiClient adapter = createAiClientAdapter(provider, model.getModelCode());
        ChatRequest chatRequest = buildStreamChatRequest(model.getModelCode(), messageChain.getMessages());
        adapter.streamChat(chatRequest, responseHandler);
    }

    /**
     * 使用流水线处理消息
     */
    private ChatResponse processWithPipeline(AiChatSession session, ChatRequestDTO request, 
                                            AiModel model, AiProvider provider) {
        try {
            // 构建消息链
            MessageChain messageChain = buildMessageChain(session.getSessionId(), request.getSystemPromptId());
            messageChain.addUser(request.getMessage());
            
            // 构建模型配置
            ModelConfig modelConfig = ModelConfig.builder()
                    .model(model.getModelCode())
                    .build();
            
            // 创建适配器
            Provider aiProvider = createProvider(provider);
            AiClient adapter = new ProviderAiClientAdapter(aiProvider, provider.getBaseUrl(), model.getModelCode());
            
            // 构建处理上下文
            ProcessingContext context = ProcessingContext.builder()
                    .messageChain(messageChain)
                    .modelConfig(modelConfig)
                    .conversationId(session.getSessionId())
                    .build();
            
            // 使用流水线处理
            com.star.swiftAi.core.pipeline.MessagePipeline pipeline = 
                MessagePipelineFactory.createStandardPipeline(adapter);
            
            ProcessingContext result = pipeline.process(context);
            
            // 获取ChatResponse
            return result.getSharedData("chatResponse", ChatResponse.class);
            
        } catch (Exception e) {
            log.error("流水线处理失败: sessionId={}, error={}", session.getSessionId(), e.getMessage(), e);
            throw new RuntimeException("消息处理失败: " + e.getMessage(), e);
        }
    }

    /**
     * 构建MessageChain
     */
    private MessageChain buildMessageChain(String sessionId, Long systemPromptId) {
        // 获取历史消息
        List<Map<String, Object>> contexts = buildContexts(sessionId, systemPromptId);
        
        // 转换为MessageChain
        return MessageChainAdapter.fromContexts(contexts);
    }

    /**
     * 从ChatResponse中提取内容
     */
    private String extractContentFromResponse(ChatResponse response) {
        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            return "";
        }
        
        Message message = response.getChoices().get(0).getMessage();
        if (message == null || message.getContent() == null) {
            return "";
        }
        
        return message.getContent().toString();
    }

    /**
     * 从ChatResponse中提取token数
     */
    private int extractTokensFromResponse(ChatResponse response) {
        if (response == null || response.getUsage() == null) {
            return 0;
        }
        
        return response.getUsage().getTotalTokens() != null ? response.getUsage().getTotalTokens() : 0;
    }

    /**
     * 从ChatResponse中提取增量内容（流式响应）
     */
    private String extractDeltaFromResponse(ChatResponse response) {
        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            return null;
        }
        
        ChatResponse.Choice choice = response.getChoices().get(0);
        if (choice == null || choice.getDelta() == null) {
            return null;
        }
        
        Object deltaContent = choice.getDelta().getContent();
        return deltaContent != null ? deltaContent.toString() : null;
    }

    /**
     * 将ChatResponse转换为LLMResponse
     */
    private LLMResponse convertToLLMResponse(ChatResponse chatResponse) {
        LLMResponse llmResponse = new LLMResponse();
        
        // 提取增量内容（流式响应）
        String delta = extractDeltaFromResponse(chatResponse);
        llmResponse.setDelta(delta);
        
        // 提取内容
        // 对于流式响应，delta就是内容；对于非流式响应，从message.content中提取
        String content = (delta != null) ? delta : extractContentFromResponse(chatResponse);
        llmResponse.setContent(content);
        
        // 提取token使用情况
        if (chatResponse.getUsage() != null) {
            TokenUsage usage = new TokenUsage();
            usage.setInputOther(chatResponse.getUsage().getPromptTokens() != null ? chatResponse.getUsage().getPromptTokens() : 0);
            usage.setOutput(chatResponse.getUsage().getCompletionTokens() != null ? chatResponse.getUsage().getCompletionTokens() : 0);
            // total是计算属性，不需要设置
            llmResponse.setUsage(usage);
        }
        
        // 设置ID
        llmResponse.setId(chatResponse.getId());
        
        // 设置是否完成
        // 对于流式响应，只有在finishReason为"stop"时才设置finished=true
        // "length"表示因为长度限制被截断，但流式响应还未完成
        if (chatResponse.getChoices() != null && !chatResponse.getChoices().isEmpty()) {
            String finishReason = chatResponse.getChoices().get(0).getFinishReason();
            boolean hasDelta = (delta != null && !delta.isEmpty());
            // 流式响应：只有在明确收到stop且没有新内容时才算完成
            // 非流式响应：收到stop或length都算完成
            if (hasDelta) {
                // 流式响应，只有stop才表示完成
                llmResponse.setFinished("stop".equals(finishReason));
            } else {
                // 非流式响应，stop或length都算完成
                llmResponse.setFinished("stop".equals(finishReason) || "length".equals(finishReason));
            }
        }
        
        return llmResponse;
    }

    private void handleStreamResponse(LLMResponse response, String sessionId, SseEmitter emitter,
                                      AtomicReference<StringBuilder> fullContentRef,
                                      AtomicInteger totalOutputTokens,
                                      AtomicBoolean completed,
                                      Function<LLMResponse, StreamChatResponseDTO> converter,
                                      boolean saveToDb) {
        if (completed.get()) {
            return;
        }
        
        try {
            // 记录响应信息
            log.debug("处理流式响应: sessionId={}, delta={}, content={}, finished={}", 
                sessionId, response.getDelta(), response.getContent(), response.isFinished());
            
            // 拼接完整内容（优先使用delta，如果没有delta则使用content）
            if (fullContentRef != null) {
                if (response.getDelta() != null) {
                    fullContentRef.get().append(response.getDelta());
                } else if (response.getContent() != null) {
                    fullContentRef.get().append(response.getContent());
                }
            }
            
            if (totalOutputTokens != null && response.getUsage() != null && response.getUsage().getOutput() > 0) {
                totalOutputTokens.set(response.getUsage().getOutput());
            }
            
            StreamChatResponseDTO streamResponse = converter.apply(response);
            // 不设置 .name("message")，使用默认的 onmessage 事件，提高前端兼容性
            // 同时在日志中记录发送情况，便于排查
            if (log.isTraceEnabled()) {
                log.trace("发送流式数据: sessionId={}, content={}", sessionId, streamResponse.getDelta());
            }
            
            emitter.send(SseEmitter.event()
                .data(streamResponse)
                .id(String.valueOf(System.currentTimeMillis())));
            
            if (streamResponse.isFinished() && completed.compareAndSet(false, true)) {
                if (saveToDb && sessionId != null) {
                    try {
                        saveAssistantMessageWithoutSecurity(sessionId, fullContentRef.get().toString(), totalOutputTokens.get());
                    } catch (Exception e) {
                        log.error("保存AI助手消息失败: {}", e.getMessage(), e);
                    }
                }
                emitter.complete();
            }
        } catch (java.lang.IllegalStateException e) {
            if (e.getMessage() != null && e.getMessage().contains("already completed")) {
                completed.set(true);
            } else {
                log.error("发送SSE事件失败: {}", e.getMessage(), e);
                if (completed.compareAndSet(false, true)) {
                    emitter.completeWithError(e);
                }
            }
        } catch (Exception e) {
            log.error("发送SSE事件失败: {}", e.getMessage(), e);
            if (completed.compareAndSet(false, true)) {
                emitter.completeWithError(e);
            }
        }
    }
}

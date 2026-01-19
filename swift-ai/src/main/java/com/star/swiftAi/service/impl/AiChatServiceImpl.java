package com.star.swiftAi.service.impl;

import com.star.swiftAi.dto.*;
import com.star.swiftAi.service.AiChatService;
import com.star.swiftAi.service.AiChatSessionService;
import com.star.swiftAi.service.AiChatMessageService;
import com.star.swiftAi.service.AiModelService;
import com.star.swiftAi.service.AiProviderService;
import com.star.swiftAi.service.AiSystemPromptService;
import com.star.swiftAi.entity.*;
import com.star.swiftAi.core.model.LLMResponse;
import com.star.swiftAi.core.model.ProviderRequest;
import com.star.swiftAi.core.provider.Provider;
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
        
        List<Map<String, Object>> contexts = buildContexts(session.getSessionId(), request.getSystemPromptId());
        LLMResponse aiResponse = callAI(model, provider, request.getMessage(), contexts);
        
        saveUserMessage(session, request.getMessage(), aiResponse);
        AiChatMessage assistantMessage = saveAssistantMessage(session, aiResponse);
        
        return buildChatResponse(session, aiResponse, assistantMessage);
    }

    @Transactional(rollbackFor = Exception.class)
    public void streamChat(ChatRequestDTO request, String userId, java.util.function.BiConsumer<String, LLMResponse> consumer) throws Exception {
        AiModel model = validateAndGetModel(request.getModelId());
        AiProvider provider = validateAndGetProvider(model.getProviderId());
        AiChatSession session = getOrCreateSession(request, model, userId);
        
        List<Map<String, Object>> contexts = buildContexts(session.getSessionId(), request.getSystemPromptId());
        aiChatMessageService.saveMessage(session.getSessionId(), "user", request.getMessage(), 0);

        String sessionId = session.getSessionId();
        Provider aiProvider = createProvider(provider);
        ProviderRequest providerRequest = buildProviderRequest(request, model, contexts);
        
        log.info("流式调用AI: sessionId={}, model={}", sessionId, model.getModelCode());
        aiProvider.streamChatRealtime(providerRequest, response -> consumer.accept(sessionId, response));
    }

    public void anonymousStreamChat(ChatRequestDTO request, java.util.function.Consumer<LLMResponse> consumer) throws Exception {
        AiModel model = validateAndGetModel(request.getModelId());
        AiProvider provider = validateAndGetProvider(model.getProviderId());
        
        Provider aiProvider = createProvider(provider);
        ProviderRequest providerRequest = buildProviderRequest(request, model, new ArrayList<>());
        
        log.info("匿名流式调用AI: model={}", model.getModelCode());
        aiProvider.streamChatRealtime(providerRequest, consumer::accept);
    }

    public void streamChatWithoutDb(ChatRequestDTO request, String userId, String sessionId, Consumer<LLMResponse> consumer) throws Exception {
        AiModel model = validateAndGetModel(request.getModelId());
        AiProvider provider = validateAndGetProvider(model.getProviderId());
        
        List<Map<String, Object>> contexts = buildContexts(sessionId, request.getSystemPromptId());
        
        Provider aiProvider = createProvider(provider);
        ProviderRequest providerRequest = buildProviderRequest(request, model, contexts);
        
        log.info("流式调用AI（无DB）: sessionId={}, model={}", sessionId, model.getModelCode());
        aiProvider.streamChatRealtime(providerRequest, consumer::accept);
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
            
            List<Map<String, Object>> contexts = buildContexts(sessionId, request.getSystemPromptId());
            
            Provider aiProvider = createProvider(provider);
            ProviderRequest providerRequest = buildProviderRequest(request, model, contexts);
            
            log.info("流式调用AI（SSE）: sessionId={}, model={}", sessionId, model.getModelCode());
            
            aiProvider.streamChatRealtime(providerRequest, response -> handleStreamResponse(
                response, sessionId, emitter, fullContentRef, totalOutputTokens, completed, converter, true
            ));
            
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
            
            Provider aiProvider = createProvider(provider);
            ProviderRequest providerRequest = buildProviderRequest(request, model, new ArrayList<>());
            
            log.info("匿名流式调用AI（SSE）: model={}", model.getModelCode());
            
            aiProvider.streamChatRealtime(providerRequest, response -> handleStreamResponse(
                response, null, emitter, null, null, completed, converter, false
            ));
            
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

    public ChatResponseDTO anonymousChat(ChatRequestDTO request) {
        AiModel model = validateAndGetModel(request.getModelId());
        AiProvider provider = validateAndGetProvider(model.getProviderId());
        
        LLMResponse aiResponse = callAI(model, provider, request.getMessage(), new ArrayList<>());
        
        ChatResponseDTO response = new ChatResponseDTO();
        response.setSessionId(null);
        response.setRole("assistant");
        response.setContent(aiResponse.getContent());
        response.setTokensUsed(aiResponse.getUsage() != null ? aiResponse.getUsage().getTotal() : 0);
        
        log.info("匿名聊天成功: modelId={}, tokens={}", model.getId(), response.getTokensUsed());
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

    private void saveUserMessage(AiChatSession session, String message, LLMResponse aiResponse) {
        int inputTokens = aiResponse.getUsage() != null ? aiResponse.getUsage().getInputOther() : 0;
        aiChatMessageService.saveMessage(session.getSessionId(), "user", message, inputTokens);
        log.debug("保存用户消息: sessionId={}, inputTokens={}", session.getSessionId(), inputTokens);
    }

    private AiChatMessage saveAssistantMessage(AiChatSession session, LLMResponse aiResponse) {
        int outputTokens = aiResponse.getUsage() != null ? aiResponse.getUsage().getOutput() : 0;
        AiChatMessage assistantMessage = aiChatMessageService.saveMessage(
            session.getSessionId(), "assistant", aiResponse.getContent(), outputTokens
        );
        log.debug("保存AI回复: sessionId={}, outputTokens={}", session.getSessionId(), outputTokens);
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

    private LLMResponse callAI(AiModel model, AiProvider provider, String message, List<Map<String, Object>> contexts) {
        try {
            Provider aiProvider = createProvider(provider);
            ProviderRequest request = buildProviderRequest(message, model.getModelCode(), contexts);
            
            log.info("调用AI模型: model={}, message={}", model.getModelCode(), message);
            LLMResponse response = aiProvider.chat(request);
            log.info("AI响应: content={}, tokens={}", 
                response.getContent(), 
                response.getUsage() != null ? response.getUsage().getTotal() : 0);
            
            return response;
        } catch (Exception e) {
            log.error("调用AI模型失败: model={}, error={}", model.getModelCode(), e.getMessage(), e);
            throw new RuntimeException("调用AI模型失败: " + e.getMessage(), e);
        }
    }

    private ChatResponseDTO buildChatResponse(AiChatSession session, LLMResponse aiResponse, AiChatMessage assistantMessage) {
        ChatResponseDTO response = new ChatResponseDTO();
        response.setSessionId(session.getSessionId());
        response.setMessageId(assistantMessage.getId());
        response.setRole("assistant");
        response.setContent(aiResponse.getContent());
        response.setTokensUsed(aiResponse.getUsage() != null ? aiResponse.getUsage().getTotal() : 0);
        response.setCreatedAt(assistantMessage.getCreatedAt());
        
        log.info("聊天成功: sessionId={}, messageId={}, tokens={}", 
            session.getSessionId(), assistantMessage.getId(), response.getTokensUsed());
        
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

    private ProviderRequest buildProviderRequest(ChatRequestDTO request, AiModel model, List<Map<String, Object>> contexts) {
        return buildProviderRequest(request.getMessage(), model.getModelCode(), contexts);
    }

    private ProviderRequest buildProviderRequest(String message, String modelCode, List<Map<String, Object>> contexts) {
        ProviderRequest request = new ProviderRequest();
        request.setPrompt(message);
        request.setModel(modelCode);
        request.setContexts(contexts);
        return request;
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
            if (fullContentRef != null && response.getContent() != null) {
                fullContentRef.get().append(response.getContent());
            }
            
            if (totalOutputTokens != null && response.getUsage() != null && response.getUsage().getOutput() > 0) {
                totalOutputTokens.set(response.getUsage().getOutput());
            }
            
            StreamChatResponseDTO streamResponse = converter.apply(response);
            emitter.send(SseEmitter.event()
                .data(streamResponse)
                .id(String.valueOf(System.currentTimeMillis()))
                .name("message"));
            
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

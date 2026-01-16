package com.star.swiftAi.service.impl;

import com.star.swiftAi.service.AiChatService;
import com.star.swiftAi.service.AiChatSessionService;
import com.star.swiftAi.service.AiChatMessageService;
import com.star.swiftAi.service.AiModelService;
import com.star.swiftAi.service.AiProviderService;
import com.star.swiftAi.service.AiSystemPromptService;
import com.star.swiftAi.dto.ChatRequestDTO;
import com.star.swiftAi.dto.ChatResponseDTO;
import com.star.swiftAi.dto.ImportChatRequestDTO;
import com.star.swiftAi.dto.MessageDataDTO;
import com.star.swiftAi.dto.MessageDTO;
import com.star.swiftAi.dto.ChatSessionDataDTO;
import com.star.swiftAi.entity.*;
import com.star.swiftAi.core.model.LLMResponse;
import com.star.swiftAi.core.model.ProviderRequest;
import com.star.swiftAi.core.model.TokenUsage;
import com.star.swiftAi.core.provider.Provider;
import com.star.swiftAi.core.factory.ProviderFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI聊天服务实现
 * 封装AI聊天逻辑，包含：
 * - 创建/获取会话
 * - 获取历史消息
 * - 调用AI模型
 * - 保存消息
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

    /**
     * 发送聊天消息并获取AI回复
     *
     * @param request 聊天请求
     * @return 聊天响应
     */
    @Transactional(rollbackFor = Exception.class)
    public ChatResponseDTO chat(ChatRequestDTO request) {
        // 获取模型信息
        AiModel model = aiModelService.getById(request.getModelId());
        if (model == null) {
            throw new RuntimeException("模型不存在");
        }

        if (!model.getEnabled()) {
            throw new RuntimeException("模型未启用");
        }

        // 获取供应商信息
        AiProvider provider = aiProviderService.getById(model.getProviderId());
        if (provider == null) {
            throw new RuntimeException("供应商不存在");
        }

        if (!provider.getEnabled()) {
            throw new RuntimeException("供应商未启用");
        }

        // 获取或创建会话
        AiChatSession session = getOrCreateSession(request, model);

        // 获取历史消息（用于构建上下文）
        List<MessageDTO> historyMessages = aiChatMessageService.getMessagesBySessionId(session.getSessionId());
        
        // 获取系统提示词（如果指定了systemPromptId）
        String systemPrompt = getSystemPrompt(request.getSystemPromptId());
        
        // 构建OpenAI格式的上下文
        List<Map<String, Object>> contexts = buildOpenAIContexts(historyMessages, systemPrompt);

        // 调用AI模型获取回复
        LLMResponse aiResponse = callAI(model, provider, request.getMessage(), contexts);

        // 保存用户消息（输入token）
        saveUserMessage(session, request.getMessage(), aiResponse);

        // 保存AI回复（输出token）
        AiChatMessage assistantMessage = saveAssistantMessage(session, aiResponse);

        // 构建响应
        return buildChatResponse(session, aiResponse, assistantMessage);
    }

    /**
     * 获取或创建会话
     */
    private AiChatSession getOrCreateSession(ChatRequestDTO request, AiModel model) {
        if (request.getSessionId() != null && !request.getSessionId().isEmpty()) {
            AiChatSession session = aiChatSessionService.getBySessionId(request.getSessionId());
            if (session == null) {
                throw new RuntimeException("会话不存在");
            }
            // 验证会话使用的是否是同一个模型
            if (!session.getModelId().equals(model.getId())) {
                throw new RuntimeException("会话与模型不匹配");
            }
            return session;
        } else {
            // 创建新会话
            return aiChatSessionService.createSession("default", request.getModelId(), null);
        }
    }

    /**
     * 保存用户消息
     */
    private void saveUserMessage(AiChatSession session, String message, LLMResponse aiResponse) {
        int inputTokens = aiResponse.getUsage() != null ? 
            aiResponse.getUsage().getInputOther() : 0;
        
        aiChatMessageService.saveMessage(
            session.getSessionId(),
            "user",
            message,
            inputTokens
        );
        log.debug("保存用户消息: sessionId={}, content={}, inputTokens={}", 
            session.getSessionId(), message, inputTokens);
    }

    /**
     * 保存AI回复
     */
    private AiChatMessage saveAssistantMessage(AiChatSession session, LLMResponse aiResponse) {
        // 只记录输出token，不包含输入token
        int outputTokens = aiResponse.getUsage() != null ? 
            aiResponse.getUsage().getOutput() : 0;
            
        AiChatMessage assistantMessage = aiChatMessageService.saveMessage(
            session.getSessionId(),
            "assistant",
            aiResponse.getContent(),
            outputTokens
        );
        log.debug("保存AI回复: sessionId={}, content={}, outputTokens={}", 
            session.getSessionId(), aiResponse.getContent(), outputTokens);
        return assistantMessage;
    }

    /**
     * 获取系统提示词
     */
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

    /**
     * 构建OpenAI格式的上下文
     * 转换历史消息为OpenAI API需要的格式
     */
    private List<Map<String, Object>> buildOpenAIContexts(List<MessageDTO> historyMessages, String systemPrompt) {
        List<Map<String, Object>> contexts = new ArrayList<>();
        
        // 如果有系统提示词，添加到上下文开头
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            Map<String, Object> systemContext = new HashMap<>();
            systemContext.put("role", "system");
            systemContext.put("content", systemPrompt);
            contexts.add(systemContext);
        }
        
        // 添加历史消息
        for (MessageDTO message : historyMessages) {
            Map<String, Object> context = new HashMap<>();
            context.put("role", message.getRole());
            context.put("content", message.getContent());
            contexts.add(context);
        }
        
        return contexts;
    }

    /**
     * 调用AI模型
     */
    private LLMResponse callAI(AiModel model, AiProvider provider, 
                              String message, List<Map<String, Object>> contexts) {
        try {
            // 获取解密后的API密钥
            String decryptedApiKey = apiKeyCryptoUtil.decryptApiKeyString(provider.getApiKey());

            // 创建Provider配置
            Map<String, Object> providerConfig = new HashMap<>();
            providerConfig.put("api_key", decryptedApiKey);
            providerConfig.put("base_url", provider.getBaseUrl());
            providerConfig.put("timeout", 60);

            // 获取提供商实例（调用静态方法，不需要注入)
            Provider aiProvider = ProviderFactory.createProvider(
                provider.getProviderCode(), 
                providerConfig,
                new HashMap<>()  // providerSettings，这里传空Map
            );

        // 构建请求
        ProviderRequest request = new ProviderRequest();
        request.setPrompt(message);
        request.setModel(model.getModelCode());
        request.setContexts(contexts);

            // 调用AI
            log.info("调用AI模型: provider={}, model={}, message={}", 
                provider.getProviderName(), model.getModelCode(), message);
            
            LLMResponse response = aiProvider.chat(request);
            
            log.info("AI响应: content={}, tokens={}", 
                response.getContent(), 
                response.getUsage() != null ? response.getUsage().getTotal() : 0);

            return response;
            
        } catch (Exception e) {
            log.error("调用AI模型失败: provider={}, model={}, error={}", 
                provider.getProviderName(), model.getModelCode(), e.getMessage(), e);
            throw new RuntimeException("调用AI模型失败: " + e.getMessage(), e);
        }
    }

    /**
     * 构建聊天响应（带消息信息）
     */
    private ChatResponseDTO buildChatResponse(AiChatSession session, LLMResponse aiResponse, 
                                              AiChatMessage assistantMessage) {
        ChatResponseDTO response = new ChatResponseDTO();
        response.setSessionId(session.getSessionId());
        response.setMessageId(assistantMessage.getId());
        response.setRole("assistant");
        response.setContent(aiResponse.getContent());
        
        int tokensUsed = aiResponse.getUsage() != null ? 
            aiResponse.getUsage().getTotal() : 0;
        response.setTokensUsed(tokensUsed);
        
        response.setCreatedAt(assistantMessage.getCreatedAt());
        
        log.info("聊天成功: sessionId={}, messageId={}, tokensUsed={}, content={}", 
            session.getSessionId(), assistantMessage.getId(), tokensUsed, aiResponse.getContent());
        
        return response;
    }

    /**
     * 匿名聊天（不保存到数据库）
     * 用于未登录用户的临时聊天
     *
     * @param request 聊天请求
     * @return 聊天响应
     */
    public ChatResponseDTO anonymousChat(ChatRequestDTO request) {
        // 获取模型信息
        AiModel model = aiModelService.getById(request.getModelId());
        if (model == null) {
            throw new RuntimeException("模型不存在");
        }

        if (!model.getEnabled()) {
            throw new RuntimeException("模型未启用");
        }

        // 获取供应商信息
        AiProvider provider = aiProviderService.getById(model.getProviderId());
        if (provider == null) {
            throw new RuntimeException("供应商不存在");
        }

        if (!provider.getEnabled()) {
            throw new RuntimeException("供应商未启用");
        }

        // 调用AI模型获取回复（不保存任何数据）
        LLMResponse aiResponse = callAI(model, provider, request.getMessage(), new ArrayList<>());

        // 构建响应（sessionId为null，因为是匿名聊天）
        ChatResponseDTO response = new ChatResponseDTO();
        response.setSessionId(null);
        response.setRole("assistant");
        response.setContent(aiResponse.getContent());
        
        int tokensUsed = aiResponse.getUsage() != null ? 
            aiResponse.getUsage().getTotal() : 0;
        response.setTokensUsed(tokensUsed);
        
        log.info("匿名聊天成功: modelId={}, tokensUsed={}, content={}", 
            model.getId(), tokensUsed, aiResponse.getContent());
        
        return response;
    }

    /**
     * 导入聊天记录
     * 用于将匿名用户的聊天记录迁移到登录用户账户
     *
     * @param userId 用户ID
     * @param request 导入请求
     * @return 导入的会话数量
     */
    @Transactional(rollbackFor = Exception.class)
    public int importChatHistory(String userId, ImportChatRequestDTO request) {
        int importedCount = 0;
        
        for (ChatSessionDataDTO sessionData : request.getSessions()) {
            try {
                // 检查会话是否已存在
                AiChatSession existingSession = aiChatSessionService.getBySessionId(sessionData.getSessionId());
                if (existingSession != null) {
                    log.warn("会话已存在，跳过导入: sessionId={}", sessionData.getSessionId());
                    continue;
                }
                
                // 创建会话
                AiChatSession session = new AiChatSession();
                session.setSessionId(sessionData.getSessionId());
                session.setUserId(userId);
                session.setModelId(sessionData.getModelId());
                session.setTitle(sessionData.getTitle());
                aiChatSessionService.save(session);
                
                // 导入消息
                for (MessageDataDTO messageData : sessionData.getMessages()) {
                    aiChatMessageService.saveMessage(
                        sessionData.getSessionId(),
                        messageData.getRole(),
                        messageData.getContent(),
                        0 // 匿名聊天不统计token
                    );
                }
                
                importedCount++;
                log.info("成功导入会话: sessionId={}, messageCount={}", 
                    sessionData.getSessionId(), sessionData.getMessages().size());
                
            } catch (Exception e) {
                log.error("导入会话失败: sessionId={}, error={}", 
                    sessionData.getSessionId(), e.getMessage(), e);
                // 继续导入其他会话
            }
        }
        
        log.info("导入聊天记录完成: userId={}, importedCount={}", userId, importedCount);
        return importedCount;
    }
}

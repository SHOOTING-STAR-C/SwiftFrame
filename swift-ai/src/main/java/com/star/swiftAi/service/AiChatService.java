package com.star.swiftAi.service;

import com.star.swiftAi.dto.ChatRequestDTO;
import com.star.swiftAi.dto.ChatResponseDTO;
import com.star.swiftAi.dto.ImportChatRequestDTO;
import com.star.swiftAi.core.model.LLMResponse;

/**
 * AI聊天服务接口
 * 封装AI聊天逻辑，包含：
 * - 创建/获取会话
 * - 获取历史消息
 * - 调用AI模型
 * - 保存消息
 *
 * @author SHOOTING_STAR_C
 */
public interface AiChatService {

    /**
     * 发送聊天消息并获取AI回复
     *
     * @param request 聊天请求
     * @param userId 用户ID
     * @return 聊天响应
     */
    ChatResponseDTO chat(ChatRequestDTO request, String userId);

    /**
     * 匿名聊天（不保存到数据库）
     * 用于未登录用户的临时聊天
     *
     * @param request 聊天请求
     * @return 聊天响应
     */
    ChatResponseDTO anonymousChat(ChatRequestDTO request);

    /**
     * 导入聊天记录
     * 用于将匿名用户的聊天记录迁移到登录用户账户
     *
     * @param userId  用户ID
     * @param request 导入请求
     * @return 导入的会话数量
     */
    int importChatHistory(String userId, ImportChatRequestDTO request);

    /**
     * 流式发送聊天消息并获取AI回复
     *
     * @param request 聊天请求
     * @param userId 用户ID
     * @param consumer 响应消费者（接收sessionId和LLMResponse）
     */
    void streamChat(ChatRequestDTO request, String userId, java.util.function.BiConsumer<String, LLMResponse> consumer) throws Exception;

    /**
     * 匿名流式聊天（不保存到数据库）
     * 用于未登录用户的临时聊天
     *
     * @param request 聊天请求
     * @param consumer 响应消费者
     */
    void anonymousStreamChat(ChatRequestDTO request, java.util.function.Consumer<LLMResponse> consumer) throws Exception;

    /**
     * 保存AI助手消息（用于流式响应完成后保存）
     *
     * @param sessionId 会话ID
     * @param content 消息内容
     * @param tokensUsed 使用的token数
     */
    void saveAssistantMessage(String sessionId, String content, int tokensUsed);

    /**
     * 准备会话并保存用户消息（在主线程中执行）
     * 用于流式聊天前的准备工作，避免在异步线程中进行数据库操作
     *
     * @param request 聊天请求
     * @param userId 用户ID
     * @return 会话ID
     */
    String prepareSessionAndSaveUserMessage(ChatRequestDTO request, String userId);

    /**
     * 流式发送聊天消息（不包含数据库操作）
     * 只负责调用AI模型并返回流式响应，不进行任何数据库操作
     *
     * @param request 聊天请求
     * @param userId 用户ID
     * @param sessionId 会话ID（已预先创建）
     * @param consumer 响应消费者（接收LLMResponse）
     */
    void streamChatWithoutDb(ChatRequestDTO request, String userId, String sessionId, 
                            java.util.function.Consumer<com.star.swiftAi.core.model.LLMResponse> consumer) throws Exception;

    /**
     * 流式发送聊天消息（使用SseEmitter）
     * 直接在HTTP线程中处理，不创建新线程
     *
     * @param request 聊天请求
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @param emitter SSE发射器
     * @param fullContentRef 完整内容引用（用于累积响应）
     * @param totalOutputTokens 总输出token数
     * @param completed 完成标志
     * @param converter 响应转换器
     */
    void streamChatWithEmitter(ChatRequestDTO request, String userId, String sessionId, 
                              org.springframework.web.servlet.mvc.method.annotation.SseEmitter emitter,
                              java.util.concurrent.atomic.AtomicReference<java.lang.StringBuilder> fullContentRef,
                              java.util.concurrent.atomic.AtomicInteger totalOutputTokens,
                              java.util.concurrent.atomic.AtomicBoolean completed,
                              java.util.function.Function<com.star.swiftAi.core.model.LLMResponse, com.star.swiftAi.dto.StreamChatResponseDTO> converter);

    /**
     * 匿名流式聊天（使用SseEmitter）
     * 直接在HTTP线程中处理，不创建新线程
     *
     * @param request 聊天请求
     * @param emitter SSE发射器
     * @param completed 完成标志
     * @param converter 响应转换器
     */
    void anonymousStreamChatWithEmitter(ChatRequestDTO request, 
                                       org.springframework.web.servlet.mvc.method.annotation.SseEmitter emitter,
                                       java.util.concurrent.atomic.AtomicBoolean completed,
                                       java.util.function.Function<com.star.swiftAi.core.model.LLMResponse, com.star.swiftAi.dto.StreamChatResponseDTO> converter);
}

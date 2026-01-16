package com.star.swiftAi.service;

import com.star.swiftAi.dto.ChatRequestDTO;
import com.star.swiftAi.dto.ChatResponseDTO;
import com.star.swiftAi.dto.ImportChatRequestDTO;

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
     * @return 聊天响应
     */
    ChatResponseDTO chat(ChatRequestDTO request);

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
}

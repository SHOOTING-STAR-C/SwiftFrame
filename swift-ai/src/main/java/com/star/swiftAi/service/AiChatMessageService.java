package com.star.swiftAi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.swiftAi.dto.MessageDTO;
import com.star.swiftAi.entity.AiChatMessage;

import java.util.List;

/**
 * AI聊天消息服务接口
 *
 * @author SHOOTING_STAR_C
 */
public interface AiChatMessageService extends IService<AiChatMessage> {

    /**
     * 保存消息
     *
     * @param sessionId  会话ID
     * @param role       角色
     * @param content    消息内容
     * @param tokensUsed 使用的token数
     * @return 保存的消息
     */
    AiChatMessage saveMessage(String sessionId, String role, String content, Integer tokensUsed);

    /**
     * 获取会话的所有消息
     *
     * @param sessionId 会话ID
     * @return 消息列表
     */
    List<MessageDTO> getMessagesBySessionId(String sessionId);

    /**
     * 删除会话的所有消息
     *
     * @param sessionId 会话ID
     */
    void deleteMessagesBySessionId(String sessionId);
}

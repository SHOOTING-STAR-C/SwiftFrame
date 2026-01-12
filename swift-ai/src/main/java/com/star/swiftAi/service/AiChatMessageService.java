package com.star.swiftAi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.swiftAi.dto.MessageDTO;
import com.star.swiftAi.entity.AiChatMessage;
import com.star.swiftAi.mapper.postgresql.AiChatMessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AI聊天消息服务
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatMessageService extends ServiceImpl<AiChatMessageMapper, AiChatMessage> {

    /**
     * 保存消息
     *
     * @param sessionId  会话ID
     * @param role       角色
     * @param content    消息内容
     * @param tokensUsed 使用的token数
     * @return 保存的消息
     */
    @Transactional(rollbackFor = Exception.class)
    public AiChatMessage saveMessage(String sessionId, String role, String content, Integer tokensUsed) {
        AiChatMessage message = new AiChatMessage();
        message.setSessionId(sessionId);
        message.setRole(role);
        message.setContent(content);
        message.setTokensUsed(tokensUsed);
        
        this.save(message);
        
        // 重新查询以获取完整的消息信息（包括自动生成的ID和createdAt）
        message = this.getById(message.getId());
        
        log.info("保存消息成功: sessionId={}, role={}, messageId={}, createdAt={}", 
            sessionId, role, message.getId(), message.getCreatedAt());
        return message;
    }

    /**
     * 获取会话的所有消息
     *
     * @param sessionId 会话ID
     * @return 消息列表
     */
    public List<MessageDTO> getMessagesBySessionId(String sessionId) {
        LambdaQueryWrapper<AiChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiChatMessage::getSessionId, sessionId);
        wrapper.orderByAsc(AiChatMessage::getCreatedAt);
        
        List<AiChatMessage> messages = this.list(wrapper);
        
        return messages.stream().map(message -> {
            MessageDTO dto = new MessageDTO();
            BeanUtils.copyProperties(message, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 删除会话的所有消息
     *
     * @param sessionId 会话ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteMessagesBySessionId(String sessionId) {
        LambdaQueryWrapper<AiChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiChatMessage::getSessionId, sessionId);
        
        this.remove(wrapper);
        log.info("删除会话消息成功: sessionId={}", sessionId);
    }
}

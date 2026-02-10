package com.star.swiftAi.core.processor;

import com.star.swiftAi.core.model.Message;
import com.star.swiftAi.core.pipeline.MessageProcessor;
import com.star.swiftAi.core.pipeline.ProcessingContext;
import com.star.swiftAi.exception.AiException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 消息验证处理器
 * 验证消息的完整性和合法性
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
public class MessageValidationProcessor implements MessageProcessor {
    
    private static final int MAX_CONTENT_LENGTH = 100000; // 最大内容长度
    
    @Override
    public ProcessingContext process(ProcessingContext context) throws AiException {
        if (context.getMessageChain() == null) {
            throw new AiException("Message chain cannot be null");
        }
        
        List<Message> messages = context.getMessageChain().getMessages();
        if (messages == null || messages.isEmpty()) {
            throw new AiException("Message list cannot be empty");
        }
        
        // 验证每条消息
        for (Message message : messages) {
            validateMessage(message);
        }
        
        log.debug("All messages validated successfully");
        return context;
    }
    
    /**
     * 验证单条消息
     *
     * @param message 消息
     * @throws AiException 验证失败
     */
    private void validateMessage(Message message) throws AiException {
        // 验证角色
        if (message.getRole() == null || message.getRole().trim().isEmpty()) {
            throw new AiException("Message role cannot be null or empty");
        }
        
        // 验证角色是否合法
        String role = message.getRole().toLowerCase();
        if (!role.equals("system") && !role.equals("user") 
                && !role.equals("assistant") && !role.equals("tool")) {
            throw new AiException("Invalid message role: " + message.getRole());
        }
        
        // 验证内容
        if (message.getContent() == null) {
            throw new AiException("Message content cannot be null");
        }
        
        // 验证内容长度
        if (message.getContent() instanceof String) {
            String content = (String) message.getContent();
            if (content.length() > MAX_CONTENT_LENGTH) {
                throw new AiException("Message content too long: " + content.length() 
                        + " (max: " + MAX_CONTENT_LENGTH + ")");
            }
        }
        
        // 验证tool消息必须有toolCallId
        if (role.equals("tool") && (message.getToolCallId() == null 
                || message.getToolCallId().trim().isEmpty())) {
            throw new AiException("Tool message must have toolCallId");
        }
        
        // 验证助手消息如果有toolCalls，必须有内容
        if (role.equals("assistant") && message.getToolCalls() != null 
                && !message.getToolCalls().isEmpty()) {
            if (message.getContent() == null || message.getContent().toString().trim().isEmpty()) {
                // 允许tool_calls消息content为null或空字符串
            }
        }
    }
    
    @Override
    public String getName() {
        return "MessageValidationProcessor";
    }
    
    @Override
    public int getPriority() {
        return 15;
    }
}
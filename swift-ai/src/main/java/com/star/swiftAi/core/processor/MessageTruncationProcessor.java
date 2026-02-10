package com.star.swiftAi.core.processor;

import com.star.swiftAi.core.model.Message;
import com.star.swiftAi.core.pipeline.MessageProcessor;
import com.star.swiftAi.core.pipeline.ProcessingContext;
import com.star.swiftAi.exception.AiException;
import com.star.swiftAi.util.TokenCounter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.List;

/**
 * 消息截断处理器
 * 根据token限制截断MessageChain
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Data
public class MessageTruncationProcessor implements MessageProcessor {
    
    /**
     * 最大token数（默认4000）
     */
    private int maxTokens = 4000;
    
    /**
     * 保留系统消息（默认true）
     */
    private boolean keepSystemMessage = true;
    
    /**
     * 保留最后N条消息（默认保留最后5条）
     */
    private int keepLastMessages = 5;
    
    public MessageTruncationProcessor() {
    }
    
    public MessageTruncationProcessor(int maxTokens) {
        this.maxTokens = maxTokens;
    }
    
    @Override
    public ProcessingContext process(ProcessingContext context) throws AiException {
        if (context.getMessageChain() == null || context.getMessageChain().getMessages() == null) {
            log.warn("Message chain or messages is null");
            return context;
        }
        
        List<Message> messages = context.getMessageChain().getMessages();
        
        // 检查是否需要截断
        Integer totalTokens = context.getSharedData("totalTokens", Integer.class);
        if (totalTokens == null) {
            totalTokens = TokenCounter.estimateTotalTokens(messages);
        }
        
        if (totalTokens <= maxTokens) {
            log.debug("No truncation needed. Total tokens: {}", totalTokens);
            return context;
        }
        
        log.info("Truncating messages. Current tokens: {}, Max tokens: {}", totalTokens, maxTokens);
        
        // 保留系统消息
        Message systemMessage = null;
        if (keepSystemMessage) {
            for (Message message : messages) {
                if ("system".equalsIgnoreCase(message.getRole())) {
                    systemMessage = message;
                    break;
                }
            }
        }
        
        // 保留最后N条消息
        int keepCount = Math.min(keepLastMessages, messages.size());
        int startIndex = messages.size() - keepCount;
        
        List<Message> truncatedMessages = messages.subList(startIndex, messages.size());
        
        // 重新构建MessageChain
        messages.clear();
        
        // 先添加系统消息
        if (systemMessage != null && !truncatedMessages.contains(systemMessage)) {
            messages.add(systemMessage);
        }
        
        // 添加截断后的消息
        messages.addAll(truncatedMessages);
        
        // 重新计算token
        int newTotalTokens = TokenCounter.estimateTotalTokens(messages);
        context.putSharedData("totalTokens", newTotalTokens);
        context.putSharedData("truncated", true);
        context.putSharedData("originalTokenCount", totalTokens);
        
        log.info("Truncation completed. Original tokens: {}, New tokens: {}, Messages: {}", 
                totalTokens, newTotalTokens, messages.size());
        
        return context;
    }
    
    @Override
    public String getName() {
        return "MessageTruncationProcessor";
    }
    
    @Override
    public int getPriority() {
        return 30;
    }
}
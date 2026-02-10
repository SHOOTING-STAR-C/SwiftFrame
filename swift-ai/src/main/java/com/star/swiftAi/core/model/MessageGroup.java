package com.star.swiftAi.core.model;

import lombok.Data;

/**
 * 消息组
 * 表示一个对话轮次（用户消息+助手消息）
 *
 * @author SHOOTING_STAR_C
 */
@Data
public class MessageGroup {
    
    /**
     * 用户消息
     */
    private Message userMessage;
    
    /**
     * 助手消息
     */
    private Message assistantMessage;
    
    /**
     * 是否完整（包含用户和助手消息）
     *
     * @return 是否完整
     */
    public boolean isComplete() {
        return userMessage != null && assistantMessage != null;
    }
    
    /**
     * 获取轮次token数
     *
     * @return token数量
     */
    public int getTokenCount() {
        int count = 0;
        if (userMessage != null && userMessage.getTokens() != null) {
            count += userMessage.getTokens();
        }
        if (assistantMessage != null && assistantMessage.getTokens() != null) {
            count += assistantMessage.getTokens();
        }
        return count;
    }
    
    /**
     * 获取轮次字符数
     *
     * @return 字符数量
     */
    public int getCharCount() {
        int count = 0;
        if (userMessage != null && userMessage.getContent() != null) {
            count += userMessage.getContent().toString().length();
        }
        if (assistantMessage != null && assistantMessage.getContent() != null) {
            count += assistantMessage.getContent().toString().length();
        }
        return count;
    }
}
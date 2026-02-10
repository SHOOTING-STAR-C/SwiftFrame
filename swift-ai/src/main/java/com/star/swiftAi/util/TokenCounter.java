package com.star.swiftAi.util;

import com.star.swiftAi.core.model.Message;

import java.util.List;

/**
 * Token计数工具类
 * 用于估算消息的token数量
 *
 * @author SHOOTING_STAR_C
 */
public class TokenCounter {
    
    /**
     * 估算文本的token数量
     * 简单估算：英文约4字符/token，中文约1.5字符/token
     * 更精确的估算需要使用tokenizer
     *
     * @param text 文本
     * @return token数量
     */
    public static int estimateTokens(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        
        int chineseCount = 0;
        int englishCount = 0;
        
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            // 判断是否为中文字符
            if (isChinese(c)) {
                chineseCount++;
            } else if (isEnglish(c)) {
                englishCount++;
            }
            // 其他字符按英文计算
        }
        
        // 中文约1.5字符/token，英文约4字符/token
        return (int) Math.ceil(chineseCount / 1.5) + (int) Math.ceil(englishCount / 4.0);
    }
    
    /**
     * 估算消息的token数量
     *
     * @param message 消息
     * @return token数量
     */
    public static int estimateTokens(Message message) {
        if (message == null) {
            return 0;
        }
        
        // 如果已经计算过token，直接返回
        if (message.getTokens() != null) {
            return message.getTokens();
        }
        
        int tokens = 0;
        
        // 计算内容的token
        if (message.getContent() != null) {
            if (message.getContent() instanceof String) {
                tokens += estimateTokens((String) message.getContent());
            } else {
                // 如果是其他类型（如内容数组），转换为字符串估算
                tokens += estimateTokens(message.getContent().toString());
            }
        }
        
        // 角色和名称也会占用token
        if (message.getRole() != null) {
            tokens += estimateTokens(message.getRole());
        }
        if (message.getName() != null) {
            tokens += estimateTokens(message.getName());
        }
        
        return tokens;
    }
    
    /**
     * 估算消息列表的总token数量
     *
     * @param messages 消息列表
     * @return token数量
     */
    public static int estimateTotalTokens(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return 0;
        }
        
        return messages.stream()
                .mapToInt(TokenCounter::estimateTokens)
                .sum();
    }
    
    /**
     * 截断文本以适应token限制
     *
     * @param text          文本
     * @param maxTokens     最大token数
     * @return 截断后的文本
     */
    public static String truncateToTokens(String text, int maxTokens) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        int estimatedTokens = estimateTokens(text);
        if (estimatedTokens <= maxTokens) {
            return text;
        }
        
        // 按比例截断
        double ratio = (double) maxTokens / estimatedTokens;
        int targetLength = (int) (text.length() * ratio * 0.9); // 留10%余量
        
        if (targetLength < 1) {
            return "";
        }
        
        return text.substring(0, targetLength);
    }
    
    /**
     * 判断是否为中文字符
     *
     * @param c 字符
     * @return 是否为中文
     */
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION;
    }
    
    /**
     * 判断是否为英文字符
     *
     * @param c 字符
     * @return 是否为英文
     */
    private static boolean isEnglish(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }
}
package com.star.swiftAi.core.model;

import com.star.swiftAi.util.TokenCounter;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * chain-based消息管理模型
 * 提供链式调用方法和丰富的消息管理能力
 *
 * @author SHOOTING_STAR_C
 */
@Data
public class MessageChain {
    /**
     * 消息列表
     */
    private List<Message> messages = new ArrayList<>();
    
    /**
     * 添加消息
     *
     * @param message 消息
     * @return 当前MessageChain实例，支持链式调用
     */
    public MessageChain addMessage(Message message) {
        if (message != null) {
            this.messages.add(message);
        }
        return this;
    }
    
    /**
     * 添加系统消息
     *
     * @param content 内容
     * @return 当前MessageChain实例，支持链式调用
     */
    public MessageChain addSystem(String content) {
        return addMessage(Message.system(content));
    }
    
    /**
     * 添加用户消息
     *
     * @param content 内容
     * @return 当前MessageChain实例，支持链式调用
     */
    public MessageChain addUser(String content) {
        return addMessage(Message.user(content));
    }
    
    /**
     * 添加助手消息
     *
     * @param content 内容
     * @return 当前MessageChain实例，支持链式调用
     */
    public MessageChain addAssistant(String content) {
        return addMessage(Message.assistant(content));
    }
    
    /**
     * 获取系统消息
     *
     * @return 系统消息列表
     */
    public List<Message> getSystemMessages() {
        return messages.stream()
                .filter(m -> "system".equalsIgnoreCase(m.getRole()))
                .collect(Collectors.toList());
    }
    
    /**
     * 获取用户消息
     *
     * @return 用户消息列表
     */
    public List<Message> getUserMessages() {
        return messages.stream()
                .filter(m -> "user".equalsIgnoreCase(m.getRole()))
                .collect(Collectors.toList());
    }
    
    /**
     * 获取助手消息
     *
     * @return 助手消息列表
     */
    public List<Message> getAssistantMessages() {
        return messages.stream()
                .filter(m -> "assistant".equalsIgnoreCase(m.getRole()))
                .collect(Collectors.toList());
    }
    
    /**
     * 获取工具消息
     *
     * @return 工具消息列表
     */
    public List<Message> getToolMessages() {
        return messages.stream()
                .filter(m -> "tool".equalsIgnoreCase(m.getRole()))
                .collect(Collectors.toList());
    }
    
    /**
     * 获取最后一条消息
     *
     * @return 最后一条消息
     */
    public Message getLastMessage() {
        if (messages.isEmpty()) {
            return null;
        }
        return messages.get(messages.size() - 1);
    }
    
    /**
     * 获取最后一条助手消息
     *
     * @return 最后一条助手消息
     */
    public Message getLastAssistantMessage() {
        List<Message> assistantMessages = getAssistantMessages();
        if (assistantMessages.isEmpty()) {
            return null;
        }
        return assistantMessages.get(assistantMessages.size() - 1);
    }
    
    /**
     * 获取消息组（用户+助手消息对）
     *
     * @return 消息组列表
     */
    public List<MessageGroup> getMessageGroups() {
        List<MessageGroup> groups = new ArrayList<>();
        MessageGroup currentGroup = null;
        
        for (Message message : messages) {
            String role = message.getRole().toLowerCase();
            
            if (role.equals("user")) {
                // 如果有未完成的组，先添加
                if (currentGroup != null) {
                    groups.add(currentGroup);
                }
                // 创建新组
                currentGroup = new MessageGroup();
                currentGroup.setUserMessage(message);
            } else if (role.equals("assistant") && currentGroup != null) {
                currentGroup.setAssistantMessage(message);
                groups.add(currentGroup);
                currentGroup = null;
            }
        }
        
        // 添加最后一个未完成的组
        if (currentGroup != null) {
            groups.add(currentGroup);
        }
        
        return groups;
    }
    
    /**
     * 计算总token数
     *
     * @return token数量
     */
    public int getTotalTokens() {
        return TokenCounter.estimateTotalTokens(messages);
    }
    
    /**
     * 计算总字符数
     *
     * @return 字符数量
     */
    public int getTotalChars() {
        return messages.stream()
                .mapToInt(m -> m.getContent() != null ? m.getContent().toString().length() : 0)
                .sum();
    }
    
    /**
     * 清空所有消息
     *
     * @return 当前MessageChain实例，支持链式调用
     */
    public MessageChain clear() {
        messages.clear();
        return this;
    }
    
    /**
     * 删除指定索引的消息
     *
     * @param index 索引
     * @return 被删除的消息
     */
    public Message removeMessage(int index) {
        if (index >= 0 && index < messages.size()) {
            return messages.remove(index);
        }
        return null;
    }
    
    /**
     * 删除指定ID的消息
     *
     * @param messageId 消息ID
     * @return 是否删除成功
     */
    public boolean removeMessageById(String messageId) {
        return messages.removeIf(m -> m.getMessageId() != null && m.getMessageId().equals(messageId));
    }
    
    /**
     * 获取消息数量
     *
     * @return 消息数量
     */
    public int size() {
        return messages.size();
    }
    
    /**
     * 判断是否为空
     *
     * @return 是否为空
     */
    public boolean isEmpty() {
        return messages.isEmpty();
    }
    
    /**
     * 将MessageChain转换为OpenAI API格式
     *
     * @return OpenAI格式的消息列表
     */
    public List<Message> toOpenAIFormat() {
        return new ArrayList<>(messages);
    }
}

package com.star.swiftAi.core.adapter;

import com.star.swiftAi.core.model.Message;
import com.star.swiftAi.core.model.MessageChain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MessageChain适配器
 * 用于在不同格式和MessageChain之间进行转换
 *
 * @author SHOOTING_STAR_C
 */
public class MessageChainAdapter {

    /**
     * 将Map列表转换为MessageChain
     *
     * @param contexts Map格式的消息列表
     * @return MessageChain
     */
    public static MessageChain fromContexts(List<Map<String, Object>> contexts) {
        MessageChain chain = new MessageChain();
        if (contexts == null || contexts.isEmpty()) {
            return chain;
        }

        for (Map<String, Object> context : contexts) {
            String role = (String) context.get("role");
            Object content = context.get("content");
            
            if (role != null && content != null) {
                Message message = Message.builder()
                        .role(role)
                        .content(content)
                        .build();
                message.init();
                chain.addMessage(message);
            }
        }

        return chain;
    }

    /**
     * 将MessageChain转换为Map列表
     *
     * @param chain MessageChain
     * @return Map格式的消息列表
     */
    public static List<Map<String, Object>> toContexts(MessageChain chain) {
        List<Map<String, Object>> contexts = new ArrayList<>();
        if (chain == null || chain.getMessages() == null) {
            return contexts;
        }

        for (Message message : chain.getMessages()) {
            Map<String, Object> context = new java.util.HashMap<>();
            context.put("role", message.getRole());
            context.put("content", message.getContent());
            contexts.add(context);
        }

        return contexts;
    }

    /**
     * 创建单条消息的MessageChain
     *
     * @param role 角色
     * @param content 内容
     * @return MessageChain
     */
    public static MessageChain createSingleMessage(String role, String content) {
        MessageChain chain = new MessageChain();
        Message message = Message.builder()
                .role(role)
                .content(content)
                .build();
        message.init();
        chain.addMessage(message);
        return chain;
    }
}
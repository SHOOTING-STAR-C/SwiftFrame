package com.star.swiftAi.core.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 对话对象
 *
 * @author SHOOTING_STAR_C
 */
@Data
public class Conversation {
    /**
     * 对话ID
     */
    private String conversationId;
    
    /**
     * 消息历史
     */
    private List<Message> messages = new ArrayList<>();
}

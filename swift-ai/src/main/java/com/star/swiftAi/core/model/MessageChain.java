package com.star.swiftAi.core.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息链
 *
 * @author SHOOTING_STAR_C
 */
@Data
public class MessageChain {
    /**
     * 消息列表
     */
    private List<Message> messages = new ArrayList<>();
}

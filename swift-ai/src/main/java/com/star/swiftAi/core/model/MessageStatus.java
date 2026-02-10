package com.star.swiftAi.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息状态枚举
 *
 * @author SHOOTING_STAR_C
 */
@Getter
@AllArgsConstructor
public enum MessageStatus {
    /**
     * 待发送
     */
    PENDING("待发送"),
    
    /**
     * 发送中
     */
    SENDING("发送中"),
    
    /**
     * 成功
     */
    SUCCESS("成功"),
    
    /**
     * 失败
     */
    FAILED("失败"),
    
    /**
     * 超时
     */
    TIMEOUT("超时");
    
    private final String description;
}
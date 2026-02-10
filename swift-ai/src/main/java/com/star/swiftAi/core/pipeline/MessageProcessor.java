package com.star.swiftAi.core.pipeline;

import com.star.swiftAi.exception.AiException;

/**
 * 消息处理器接口
 * 所有处理器都需要实现此接口
 *
 * @author SHOOTING_STAR_C
 */
public interface MessageProcessor {
    
    /**
     * 处理MessageChain
     *
     * @param context 处理上下文
     * @return 处理后的上下文
     * @throws AiException 处理异常
     */
    ProcessingContext process(ProcessingContext context) throws AiException;
    
    /**
     * 获取处理器名称
     *
     * @return 处理器名称
     */
    String getName();
    
    /**
     * 获取处理器优先级（数字越小优先级越高）
     *
     * @return 优先级
     */
    default int getPriority() {
        return 100;
    }
}
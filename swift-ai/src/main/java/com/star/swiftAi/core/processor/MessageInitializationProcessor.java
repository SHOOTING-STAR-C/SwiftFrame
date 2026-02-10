package com.star.swiftAi.core.processor;

import com.star.swiftAi.core.model.Message;
import com.star.swiftAi.core.model.MessageStatus;
import com.star.swiftAi.core.pipeline.MessageProcessor;
import com.star.swiftAi.core.pipeline.ProcessingContext;
import com.star.swiftAi.exception.AiException;
import lombok.extern.slf4j.Slf4j;

/**
 * 消息初始化处理器
 * 为消息设置默认值（ID、时间戳、状态等）
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
public class MessageInitializationProcessor implements MessageProcessor {
    
    @Override
    public ProcessingContext process(ProcessingContext context) throws AiException {
        if (context.getMessageChain() == null || context.getMessageChain().getMessages() == null) {
            log.warn("Message chain or messages is null");
            return context;
        }
        
        for (Message message : context.getMessageChain().getMessages()) {
            message.init();
            log.debug("Initialized message: {}", message.getMessageId());
        }
        
        return context;
    }
    
    @Override
    public String getName() {
        return "MessageInitializationProcessor";
    }
    
    @Override
    public int getPriority() {
        return 10; // 最高优先级，最先执行
    }
}
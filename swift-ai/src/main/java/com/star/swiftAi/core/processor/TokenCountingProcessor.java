package com.star.swiftAi.core.processor;

import com.star.swiftAi.core.model.Message;
import com.star.swiftAi.core.pipeline.MessageProcessor;
import com.star.swiftAi.core.pipeline.ProcessingContext;
import com.star.swiftAi.exception.AiException;
import com.star.swiftAi.util.TokenCounter;
import lombok.extern.slf4j.Slf4j;

/**
 * Token计数处理器
 * 计算每条消息和整个MessageChain的token数量
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
public class TokenCountingProcessor implements MessageProcessor {
    
    @Override
    public ProcessingContext process(ProcessingContext context) throws AiException {
        if (context.getMessageChain() == null || context.getMessageChain().getMessages() == null) {
            log.warn("Message chain or messages is null");
            return context;
        }
        
        int totalTokens = 0;
        
        for (Message message : context.getMessageChain().getMessages()) {
            int messageTokens = TokenCounter.estimateTokens(message);
            message.setTokens(messageTokens);
            totalTokens += messageTokens;
            log.debug("Message [{}] tokens: {}", message.getMessageId(), messageTokens);
        }
        
        context.putSharedData("totalTokens", totalTokens);
        log.debug("Total tokens for message chain: {}", totalTokens);
        
        return context;
    }
    
    @Override
    public String getName() {
        return "TokenCountingProcessor";
    }
    
    @Override
    public int getPriority() {
        return 20;
    }
}
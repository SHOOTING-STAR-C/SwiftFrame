package com.star.swiftAi.core.processor;

import com.star.swiftAi.core.pipeline.MessageProcessor;
import com.star.swiftAi.core.pipeline.ProcessingContext;
import com.star.swiftAi.exception.AiException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 日志记录处理器
 * 记录处理过程的信息
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Data
public class LoggingProcessor implements MessageProcessor {
    
    /**
     * 是否记录详细信息
     */
    private boolean verbose = false;
    
    /**
     * 是否记录消息内容
     */
    private boolean logContent = false;
    
    public LoggingProcessor() {
    }
    
    public LoggingProcessor(boolean verbose) {
        this.verbose = verbose;
    }
    
    @Override
    public ProcessingContext process(ProcessingContext context) throws AiException {
        log.info("=== Message Processing Log ===");
        log.info("Request ID: {}", context.getRequestId());
        log.info("Conversation ID: {}", context.getConversationId());
        log.info("User ID: {}", context.getUserId());
        
        if (context.getMessageChain() != null) {
            log.info("Message count: {}", context.getMessageChain().getMessages().size());
            
            if (verbose) {
                context.getMessageChain().getMessages().forEach(msg -> {
                    log.info("  - Role: {}, ID: {}, Tokens: {}, Status: {}", 
                            msg.getRole(), 
                            msg.getMessageId(), 
                            msg.getTokens(), 
                            msg.getStatus());
                    
                    if (logContent && msg.getContent() != null) {
                        String content = msg.getContent().toString();
                        if (content.length() > 100) {
                            content = content.substring(0, 100) + "...";
                        }
                        log.info("    Content: {}", content);
                    }
                });
            }
        }
        
        // 记录共享数据
        if (verbose && context.getSharedData() != null) {
            log.info("Shared data keys: {}", context.getSharedData().keySet());
            
            Integer totalTokens = context.getSharedData("totalTokens", Integer.class);
            if (totalTokens != null) {
                log.info("Total tokens: {}", totalTokens);
            }
            
            Boolean truncated = context.getSharedData("truncated", Boolean.class);
            if (truncated != null && truncated) {
                Integer originalTokens = context.getSharedData("originalTokenCount", Integer.class);
                log.info("Messages were truncated. Original tokens: {}", originalTokens);
            }
        }
        
        // 记录模型配置
        if (verbose && context.getModelConfig() != null) {
            log.info("Model: {}, Temperature: {}, MaxTokens: {}", 
                    context.getModelConfig().getModel(),
                    context.getModelConfig().getTemperature(),
                    context.getModelConfig().getMaxTokens());
        }
        
        log.info("============================");
        
        return context;
    }
    
    @Override
    public String getName() {
        return "LoggingProcessor";
    }
    
    @Override
    public int getPriority() {
        return 5; // 第一个执行
    }
}
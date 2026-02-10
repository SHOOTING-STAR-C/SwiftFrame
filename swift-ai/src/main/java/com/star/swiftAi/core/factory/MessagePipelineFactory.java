package com.star.swiftAi.core.factory;

import com.star.swiftAi.client.AiClient;
import com.star.swiftAi.core.processor.*;
import com.star.swiftAi.core.pipeline.MessagePipeline;
import com.star.swiftAi.core.pipeline.MessagePipelineBuilder;

/**
 * 消息流水线工厂
 * 提供预配置的流水线创建方法
 *
 * @author SHOOTING_STAR_C
 */
public class MessagePipelineFactory {
    
    private static final int DEFAULT_MAX_TOKENS = 4000;
    
    /**
     * 创建标准流水线
     * 包含：日志、初始化、验证、计数、截断、发送
     *
     * @param aiClient   AI客户端
     * @param maxTokens  最大token数
     * @return 流水线
     */
    public static MessagePipeline createStandardPipeline(AiClient aiClient, int maxTokens) {
        return createCustomPipeline("StandardPipeline", aiClient, maxTokens, false, true);
    }
    
    /**
     * 创建标准流水线（使用默认4000 token限制）
     *
     * @param aiClient AI客户端
     * @return 流水线
     */
    public static MessagePipeline createStandardPipeline(AiClient aiClient) {
        return createStandardPipeline(aiClient, DEFAULT_MAX_TOKENS);
    }
    
    /**
     * 创建简单流水线
     * 只包含：初始化、验证、发送
     *
     * @param aiClient AI客户端
     * @return 流水线
     */
    public static MessagePipeline createSimplePipeline(AiClient aiClient) {
        return createCustomPipeline("SimplePipeline", aiClient, 0, false, false);
    }
    
    /**
     * 创建详细日志流水线
     * 包含所有处理器，并启用详细日志
     *
     * @param aiClient  AI客户端
     * @param maxTokens 最大token数
     * @return 流水线
     */
    public static MessagePipeline createVerbosePipeline(AiClient aiClient, int maxTokens) {
        return createCustomPipeline("VerbosePipeline", aiClient, maxTokens, true, true);
    }
    
    /**
     * 创建自定义流水线
     *
     * @param name       流水线名称
     * @param aiClient   AI客户端
     * @param maxTokens  最大token数
     * @param verbose    是否启用详细日志
     * @param enableTruncation 是否启用截断
     * @return 流水线
     */
    public static MessagePipeline createCustomPipeline(String name, AiClient aiClient,
            int maxTokens, boolean verbose, boolean enableTruncation) {
        
        MessagePipelineBuilder builder = MessagePipelineBuilder.create(name);
        
        // 添加日志处理器
        LoggingProcessor loggingProcessor = new LoggingProcessor(verbose);
        if (verbose) {
            loggingProcessor.setLogContent(true);
        }
        builder.addProcessor(loggingProcessor);
        
        // 添加核心处理器
        builder.addProcessor(new MessageInitializationProcessor());
        builder.addProcessor(new MessageValidationProcessor());
        builder.addProcessor(new TokenCountingProcessor());
        
        // 可选：添加截断处理器
        if (enableTruncation) {
            builder.addProcessor(new MessageTruncationProcessor(maxTokens));
        }
        
        // 添加发送处理器
        builder.addProcessor(new MessageSendingProcessor(aiClient));
        
        return builder.build();
    }
}

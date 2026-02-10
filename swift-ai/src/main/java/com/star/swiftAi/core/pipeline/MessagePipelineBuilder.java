package com.star.swiftAi.core.pipeline;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * 流水线构建器
 * 提供流畅的API来构建流水线
 *
 * @author SHOOTING_STAR_C
 */
public class MessagePipelineBuilder {
    
    private final List<MessageProcessor> processors = new ArrayList<>();
    private final String name;
    private boolean async = false;
    private Executor executor;
    
    /**
     * 创建构建器
     *
     * @param name 流水线名称
     * @return 构建器
     */
    public static MessagePipelineBuilder create(String name) {
        return new MessagePipelineBuilder(name);
    }
    
    private MessagePipelineBuilder(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Pipeline name cannot be null or empty");
        }
        this.name = name;
    }
    
    /**
     * 添加处理器
     *
     * @param processor 处理器
     * @return 构建器
     */
    public MessagePipelineBuilder addProcessor(MessageProcessor processor) {
        if (processor == null) {
            throw new IllegalArgumentException("Processor cannot be null");
        }
        this.processors.add(processor);
        return this;
    }
    
    /**
     * 批量添加处理器
     *
     * @param processors 处理器列表
     * @return 构建器
     */
    public MessagePipelineBuilder addProcessors(List<MessageProcessor> processors) {
        if (processors != null) {
            this.processors.addAll(processors);
        }
        return this;
    }
    
    /**
     * 启用异步处理
     *
     * @param executor 执行器
     * @return 构建器
     */
    public MessagePipelineBuilder enableAsync(Executor executor) {
        this.async = true;
        this.executor = executor;
        return this;
    }
    
    /**
     * 构建流水线
     *
     * @return 流水线
     */
    public MessagePipeline build() {
        if (processors.isEmpty()) {
            throw new IllegalStateException("Pipeline must have at least one processor");
        }
        
        MessagePipeline pipeline = new MessagePipeline();
        pipeline.setName(name);
        pipeline.setProcessors(new ArrayList<>(processors));
        pipeline.setAsync(async);
        pipeline.setExecutor(executor);
        
        // 按优先级排序
        pipeline.getProcessors().sort(Comparator.comparingInt(MessageProcessor::getPriority));
        
        return pipeline;
    }
    
    /**
     * 获取当前处理器数量
     *
     * @return 处理器数量
     */
    public int getProcessorCount() {
        return processors.size();
    }
}
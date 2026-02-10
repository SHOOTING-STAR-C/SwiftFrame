package com.star.swiftAi.core.pipeline;

import com.star.swiftAi.exception.AiException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

/**
 * 消息流水线
 * 管理处理器列表并按顺序执行处理
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Data
public class MessagePipeline {
    
    /**
     * 处理器列表（按优先级排序）
     */
    private List<MessageProcessor> processors;
    
    /**
     * 流水线名称
     */
    private String name;
    
    /**
     * 是否启用异步处理
     */
    private boolean async;
    
    /**
     * 异步执行器
     */
    private Executor executor;
    
    /**
     * 处理MessageChain
     *
     * @param context 处理上下文
     * @return 处理后的上下文
     * @throws AiException 处理异常
     */
    public ProcessingContext process(ProcessingContext context) throws AiException {
        if (context == null) {
            throw new AiException("Processing context cannot be null");
        }
        
        if (processors == null || processors.isEmpty()) {
            log.warn("No processors in pipeline: {}", name);
            return context;
        }
        
        context.setStartTime(System.currentTimeMillis());
        
        log.debug("Starting pipeline: {} with {} processors", name, processors.size());
        
        for (MessageProcessor processor : processors) {
            if (context.isSkipRemaining()) {
                log.info("Skipping remaining processors due to skip flag");
                break;
            }
            
            try {
                long start = System.currentTimeMillis();
                context = processor.process(context);
                long duration = System.currentTimeMillis() - start;
                
                log.debug("Processor [{}] executed in {}ms", processor.getName(), duration);
                
            } catch (Exception e) {
                log.error("Processor [{}] failed", processor.getName(), e);
                throw new AiException("Processor [" + processor.getName() + "] failed: " + e.getMessage(), e);
            }
        }
        
        long totalDuration = System.currentTimeMillis() - context.getStartTime();
        log.debug("Pipeline [{}] completed in {}ms", name, totalDuration);
        
        return context;
    }
    
    /**
     * 异步处理
     *
     * @param context 处理上下文
     * @return CompletableFuture
     */
    public CompletableFuture<ProcessingContext> processAsync(ProcessingContext context) {
        if (!async || executor == null) {
            log.warn("Async processing not enabled, falling back to sync processing");
            try {
                return CompletableFuture.completedFuture(process(context));
            } catch (AiException e) {
                CompletableFuture<ProcessingContext> future = new CompletableFuture<>();
                future.completeExceptionally(e);
                return future;
            }
        }
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                return process(context);
            } catch (AiException e) {
                throw new CompletionException(e);
            }
        }, executor);
    }
    
    /**
     * 添加处理器
     *
     * @param processor 处理器
     * @return 当前流水线
     */
    public MessagePipeline addProcessor(MessageProcessor processor) {
        if (processors == null) {
            processors = new ArrayList<>();
        }
        processors.add(processor);
        sortProcessors();
        return this;
    }
    
    /**
     * 移除处理器
     *
     * @param processorName 处理器名称
     * @return 当前流水线
     */
    public MessagePipeline removeProcessor(String processorName) {
        if (processors != null) {
            processors.removeIf(p -> p.getName().equals(processorName));
        }
        return this;
    }
    
    /**
     * 获取处理器
     *
     * @param processorName 处理器名称
     * @return 处理器
     */
    public MessageProcessor getProcessor(String processorName) {
        if (processors == null) {
            return null;
        }
        return processors.stream()
                .filter(p -> p.getName().equals(processorName))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 获取所有处理器名称
     *
     * @return 处理器名称列表
     */
    public List<String> getProcessorNames() {
        if (processors == null) {
            return new ArrayList<>();
        }
        return processors.stream()
                .map(MessageProcessor::getName)
                .toList();
    }
    
    /**
     * 按优先级排序处理器
     */
    private void sortProcessors() {
        if (processors != null) {
            processors.sort(Comparator.comparingInt(MessageProcessor::getPriority));
        }
    }
    
    /**
     * 获取处理器数量
     *
     * @return 处理器数量
     */
    public int getProcessorCount() {
        return processors != null ? processors.size() : 0;
    }
}
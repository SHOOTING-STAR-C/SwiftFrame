package com.star.swiftAi.core.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * LLM响应结果
 *
 * @author SHOOTING_STAR_C
 */
@Data
public class LLMResponse {
    /**
     * 角色（assistant/tool/err）
     */
    private String role;
    
    /**
     * 消息链
     */
    private MessageChain resultChain;
    
    /**
     * 工具调用参数
     */
    private List<Map<String, Object>> toolsCallArgs = new ArrayList<>();
    
    /**
     * 工具调用名称
     */
    private List<String> toolsCallName = new ArrayList<>();
    
    /**
     * 工具调用ID
     */
    private List<String> toolsCallIds = new ArrayList<>();
    
    /**
     * 推理内容
     */
    private String reasoningContent = "";
    
    /**
     * 原始响应
     */
    private Object rawCompletion;
    
    /**
     * 是否为流式响应
     */
    private boolean isChunk = false;
    
    /**
     * 响应ID
     */
    private String id;
    
    /**
     * Token使用情况
     */
    private TokenUsage usage;
    
    /**
     * 响应内容
     */
    private String content;
    
    /**
     * 增量内容（流式响应）
     */
    private String delta;
    
    /**
     * 是否完成
     */
    private boolean finished;
}

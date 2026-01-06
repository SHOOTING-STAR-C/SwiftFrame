package com.star.swiftAi.core.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 工具调用结果
 *
 * @author SHOOTING_STAR_C
 */
@Data
public class ToolCallsResult {
    /**
     * 工具调用ID
     */
    private String toolCallId;
    
    /**
     * 工具名称
     */
    private String toolName;
    
    /**
     * 工具参数
     */
    private Map<String, Object> toolArgs;
    
    /**
     * 工具执行结果
     */
    private String result;
    
    /**
     * 转换为OpenAI格式的消息列表
     */
    public List<Map<String, Object>> toOpenAiMessages() {
        List<Map<String, Object>> messages = new ArrayList<>();
        
        // 添加工具调用消息
        Map<String, Object> toolCallMessage = Map.of(
            "role", "assistant",
            "tool_calls", List.of(Map.of(
                "id", toolCallId,
                "type", "function",
                "function", Map.of(
                    "name", toolName,
                    "arguments", toolArgs != null ? toolArgs.toString() : "{}"
                )
            ))
        );
        messages.add(toolCallMessage);
        
        // 添加工具响应消息
        Map<String, Object> toolResponseMessage = Map.of(
            "role", "tool",
            "tool_call_id", toolCallId,
            "content", result != null ? result : ""
        );
        messages.add(toolResponseMessage);
        
        return messages;
    }
}

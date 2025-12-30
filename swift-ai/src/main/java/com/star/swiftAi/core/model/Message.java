package com.star.swiftAi.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 对话消息实体
 * 遵循 OpenAI API 格式
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Message {

    /**
     * 消息角色：system, user, assistant, tool
     */
    @JsonProperty(value = "role", required = true)
    private String role;

    /**
     * 消息内容
     * 可以是字符串或内容数组（多模态支持）
     */
    @JsonProperty(value = "content", required = true)
    private Object content;

    /**
     * 工具调用信息（助手的响应中）
     */
    @JsonProperty("tool_calls")
    private List<ToolCall> toolCalls;

    /**
     * 工具调用ID（工具消息的响应中）
     */
    @JsonProperty("tool_call_id")
    private String toolCallId;

    /**
     * 消息名称（用于区分相同角色的不同消息发送者）
     */
    @JsonProperty("name")
    private String name;

    /**
     * 工具调用实体
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ToolCall {

        @JsonProperty(value = "id", required = true)
        private String id;

        @JsonProperty(value = "type", required = true)
        private String type;

        @JsonProperty(value = "function", required = true)
        private Function function;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Function {

            @JsonProperty(value = "name", required = true)
            private String name;

            @JsonProperty("arguments")
            private String arguments;

            @JsonProperty("arguments_map")
            private Map<String, Object> argumentsMap;
        }
    }

    /**
     * 创建文本消息的便捷方法
     */
    public static Message of(String role, String content) {
        return Message.builder()
                .role(role)
                .content(content)
                .build();
    }

    /**
     * 创建系统消息的便捷方法
     */
    public static Message system(String content) {
        return of("system", content);
    }

    /**
     * 创建用户消息的便捷方法
     */
    public static Message user(String content) {
        return of("user", content);
    }

    /**
     * 创建助手消息的便捷方法
     */
    public static Message assistant(String content) {
        return of("assistant", content);
    }
}

package com.star.swiftAi.core.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.star.swiftAi.core.model.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 对话响应实体
 * 遵循 OpenAI Chat Completions API 响应格式
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatResponse {

    /**
     * 响应ID
     */
    @JsonProperty("id")
    private String id;

    /**
     * 对象类型
     */
    @JsonProperty("object")
    private String object;

    /**
     * 创建时间戳
     */
    @JsonProperty("created")
    private Long created;

    /**
     * 模型名称
     */
    @JsonProperty("model")
    private String model;

    /**
     * 选择列表
     */
    @JsonProperty("choices")
    private List<Choice> choices;

    /**
     * 使用情况统计
     */
    @JsonProperty("usage")
    private Usage usage;

    /**
     * 系统指纹
     */
    @JsonProperty("system_fingerprint")
    private String systemFingerprint;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Choice {

        /**
         * 完成原因
         * stop, length, tool_calls, content_filter
         */
        @JsonProperty("finish_reason")
        private String finishReason;

        /**
         * 索引
         */
        @JsonProperty("index")
        private Integer index;

        /**
         * 消息内容
         */
        @JsonProperty("message")
        private Message message;

        /**
         * 增量内容（流式响应）
         */
        @JsonProperty("delta")
        private Message delta;

        /**
         * 对数概率
         */
        @JsonProperty("logprobs")
        private Object logprobs;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Usage {

        /**
         * 提示标记数
         */
        @JsonProperty("prompt_tokens")
        private Integer promptTokens;

        /**
         * 完成标记数
         */
        @JsonProperty("completion_tokens")
        private Integer completionTokens;

        /**
         * 总标记数
         */
        @JsonProperty("total_tokens")
        private Integer totalTokens;
    }
}

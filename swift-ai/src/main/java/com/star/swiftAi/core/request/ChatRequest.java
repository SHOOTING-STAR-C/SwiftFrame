package com.star.swiftAi.core.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.star.swiftAi.core.model.Message;
import com.star.swiftAi.core.model.Tool;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 对话请求实体
 * 遵循 OpenAI Chat Completions API 格式
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatRequest {

    /**
     * 模型名称
     */
    @JsonProperty(value = "model", required = true)
    private String model;

    /**
     * 消息列表
     */
    @JsonProperty(value = "messages", required = true)
    private List<Message> messages;

    /**
     * 采样温度 (0-2)
     * 较高的值会使输出更随机，较低的值会使输出更集中和确定
     */
    @JsonProperty("temperature")
    private Double temperature;

    /**
     * 核采样阈值 (0-1)
     * 模型考虑具有 top_p 概率质量的标记的结果
     */
    @JsonProperty("top_p")
    private Double topP;

    /**
     * 是否流式响应
     */
    @JsonProperty("stream")
    private Boolean stream;

    /**
     * 生成的最大标记数
     */
    @JsonProperty("max_tokens")
    private Integer maxTokens;

    /**
     * 生成的最小标记数
     */
    @JsonProperty("min_tokens")
    private Integer minTokens;

    /**
     * 停止词序列
     */
    @JsonProperty("stop")
    private Object stop;

    /**
     * 是否返回对数概率
     */
    @JsonProperty("logprobs")
    private Boolean logprobs;

    /**
     * 返回的最高对数概率数
     */
    @JsonProperty("top_logprobs")
    private Integer topLogprobs;

    /**
     * 是否返回多次生成结果
     */
    @JsonProperty("n")
    private Integer n;

    /**
     * 是否返回消息中的提示标记
     */
    @JsonProperty("echo")
    private Boolean echo;

    /**
     * 工具定义列表
     */
    @JsonProperty("tools")
    private List<Tool> tools;

    /**
     * 工具选择策略
     * none, auto, 或特定的工具名称
     */
    @JsonProperty("tool_choice")
    private Object toolChoice;

    /**
     * 用户标识，用于检测滥用
     */
    @JsonProperty("user")
    private String user;

    /**
     * 响应格式
     * 用于指定响应的 JSON Schema
     */
    @JsonProperty("response_format")
    private Map<String, Object> responseFormat;

    /**
     * 频率惩罚 (-2.0 到 2.0)
     * 正值会根据文本中现有频率惩罚新标记
     */
    @JsonProperty("frequency_penalty")
    private Double frequencyPenalty;

    /**
     * 存在惩罚 (-2.0 到 2.0)
     * 正值会根据文本中是否存在来惩罚新标记
     */
    @JsonProperty("presence_penalty")
    private Double presencePenalty;

    /**
     * 种子值，用于确定性的采样
     */
    @JsonProperty("seed")
    private Integer seed;

    /**
     * 接口类型后缀
     * 可以是空字符串或一个简单的标识符
     */
    @JsonProperty("suffix")
    private String suffix;

    /**
     * 超时时间（毫秒）
     * 非 OpenAI 标准参数，用于自定义超时
     */
    @JsonProperty("timeout")
    private Long timeout;
}

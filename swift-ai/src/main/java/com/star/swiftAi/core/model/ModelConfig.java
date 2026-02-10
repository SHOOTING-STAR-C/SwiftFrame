package com.star.swiftAi.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 模型配置
 * 用于配置AI模型的参数
 *
 * @author SHOOTING_STAR_C
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelConfig {
    
    /**
     * 模型名称（如：gpt-3.5-turbo, gpt-4等）
     */
    private String model;
    
    /**
     * 采样温度 (0-2)
     * 较高的值会使输出更随机，较低的值会使输出更集中和确定
     */
    @Builder.Default
    private Double temperature = 0.7;
    
    /**
     * 核采样阈值 (0-1)
     * 模型考虑具有 top_p 概率质量的标记的结果
     */
    private Double topP;
    
    /**
     * 是否流式响应
     */
    private Boolean stream;
    
    /**
     * 生成的最大标记数
     */
    private Integer maxTokens;
    
    /**
     * 生成的最小标记数
     */
    private Integer minTokens;
    
    /**
     * 停止词序列
     */
    private Object stop;
    
    /**
     * 是否返回对数概率
     */
    private Boolean logprobs;
    
    /**
     * 返回的最高对数概率数
     */
    private Integer topLogprobs;
    
    /**
     * 是否返回多次生成结果
     */
    private Integer n;
    
    /**
     * 是否返回消息中的提示标记
     */
    private Boolean echo;
    
    /**
     * 用户标识，用于检测滥用
     */
    private String user;
    
    /**
     * 响应格式
     * 用于指定响应的 JSON Schema
     */
    private Object responseFormat;
    
    /**
     * 频率惩罚 (-2.0 到 2.0)
     * 正值会根据文本中现有频率惩罚新标记
     */
    @Builder.Default
    private Double frequencyPenalty = 0.0;
    
    /**
     * 存在惩罚 (-2.0 到 2.0)
     * 正值会根据文本中是否存在来惩罚新标记
     */
    @Builder.Default
    private Double presencePenalty = 0.0;
    
    /**
     * 种子值，用于确定性的采样
     */
    private Integer seed;
    
    /**
     * 超时时间（毫秒）
     */
    private Long timeout;
}
package com.star.swiftAi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 模型DTO
 *
 * @author SHOOTING_STAR_C
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI模型信息DTO")
public class ModelDTO {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID", example = "1")
    private Long id;

    /**
     * 模型代码
     */
    @Schema(description = "模型代码", example = "gpt-3.5-turbo")
    private String modelCode;

    /**
     * 模型名称
     */
    @Schema(description = "模型名称", example = "GPT-3.5 Turbo")
    private String modelName;

    /**
     * 供应商ID
     */
    @Schema(description = "供应商ID", example = "1")
    private Long providerId;

    /**
     * 供应商名称
     */
    @Schema(description = "供应商名称", example = "OpenAI")
    private String providerName;

    /**
     * 是否启用
     */
    @Schema(description = "是否启用")
    private Boolean enabled;

    /**
     * 最大token数
     */
    @Schema(description = "最大token数", example = "4096")
    private Integer maxTokens;

    /**
     * 上下文长度
     */
    @Schema(description = "上下文长度", example = "16384")
    private Integer contextLength;

    /**
     * 描述
     */
    @Schema(description = "描述", example = "OpenAI 经典的 3.5 模型")
    private String description;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}

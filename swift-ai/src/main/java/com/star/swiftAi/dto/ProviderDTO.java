package com.star.swiftAi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 供应商DTO
 *
 * @author SHOOTING_STAR_C
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI供应商信息DTO")
public class ProviderDTO {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID", example = "1")
    private Long id;

    /**
     * 供应商代码
     */
    @Schema(description = "供应商代码", example = "openai")
    private String providerCode;

    /**
     * 供应商名称
     */
    @Schema(description = "供应商名称", example = "OpenAI")
    private String providerName;

    /**
     * API基础URL
     */
    @Schema(description = "API基础URL", example = "https://api.openai.com/v1")
    private String baseUrl;

    /**
     * 是否启用
     */
    @Schema(description = "是否启用")
    private Boolean enabled;

    /**
     * 优先级
     */
    @Schema(description = "优先级", example = "1")
    private Integer priority;

    /**
     * 描述
     */
    @Schema(description = "描述", example = "OpenAI 官方供应商")
    private String description;

    /**
     * 健康状态
     */
    @Schema(description = "健康状态")
    private Boolean healthy;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}

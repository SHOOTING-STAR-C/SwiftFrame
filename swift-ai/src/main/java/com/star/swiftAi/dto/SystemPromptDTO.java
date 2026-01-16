package com.star.swiftAi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统提示词DTO
 *
 * @author SHOOTING_STAR_C
 */
@Data
@Schema(description = "系统提示词DTO")
public class SystemPromptDTO {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID", example = "1")
    private Long id;

    /**
     * 提示词名称
     */
    @Schema(description = "提示词名称", example = "代码助手")
    private String promptName;

    /**
     * 提示词内容
     */
    @Schema(description = "提示词内容", example = "你是一个专业的代码助手，擅长帮助用户解决编程问题。")
    private String promptContent;

    /**
     * 是否启用
     */
    @Schema(description = "是否启用", example = "true")
    private Boolean enabled;

    /**
     * 描述
     */
    @Schema(description = "描述", example = "用于编程相关的对话")
    private String description;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2024-01-16T14:00:00")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间", example = "2024-01-16T14:00:00")
    private LocalDateTime updatedAt;
}

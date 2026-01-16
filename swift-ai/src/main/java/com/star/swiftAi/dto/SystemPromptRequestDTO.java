package com.star.swiftAi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 系统提示词请求DTO
 *
 * @author SHOOTING_STAR_C
 */
@Data
@Schema(description = "系统提示词请求DTO")
public class SystemPromptRequestDTO {

    /**
     * 提示词名称
     */
    @Schema(description = "提示词名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "代码助手")
    @NotBlank(message = "提示词名称不能为空")
    private String promptName;

    /**
     * 提示词内容
     */
    @Schema(description = "提示词内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "你是一个专业的代码助手，擅长帮助用户解决编程问题。")
    @NotBlank(message = "提示词内容不能为空")
    private String promptContent;

    /**
     * 是否启用
     */
    @Schema(description = "是否启用", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "启用状态不能为空")
    private Boolean enabled;

    /**
     * 描述
     */
    @Schema(description = "描述", example = "用于编程相关的对话")
    private String description;
}

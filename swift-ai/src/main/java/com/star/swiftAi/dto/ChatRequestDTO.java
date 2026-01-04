package com.star.swiftAi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 聊天请求DTO
 *
 * @author SHOOTING_STAR_C
 */
@Data
@Schema(description = "聊天请求DTO")
public class ChatRequestDTO {

    /**
     * 会话ID（可选，不传则创建新会话）
     */
    @Schema(description = "会话ID（可选，不传则创建新会话）", example = "1723456789012345678")
    private String sessionId;

    /**
     * 模型ID
     */
    @Schema(description = "模型ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "模型ID不能为空")
    private Long modelId;

    /**
     * 消息内容
     */
    @Schema(description = "消息内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "你好，请问你是谁？")
    @NotBlank(message = "消息内容不能为空")
    private String message;

    /**
     * 是否流式返回
     */
    @Schema(description = "是否流式返回", defaultValue = "false")
    private Boolean stream = false;
}

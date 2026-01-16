package com.star.swiftAi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 聊天会话数据DTO（用于导入）
 *
 * @author SHOOTING_STAR_C
 */
@Data
@Schema(description = "聊天会话数据DTO")
public class ChatSessionDataDTO {

    /**
     * 会话ID
     */
    @Schema(description = "会话ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1723456789012345678")
    @NotBlank(message = "会话ID不能为空")
    private String sessionId;

    /**
     * 模型ID
     */
    @Schema(description = "模型ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "模型ID不能为空")
    private Long modelId;

    /**
     * 会话标题
     */
    @Schema(description = "会话标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "新对话")
    @NotBlank(message = "会话标题不能为空")
    private String title;

    /**
     * 消息列表
     */
    @Schema(description = "消息列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "消息列表不能为空")
    @Valid
    private List<MessageDataDTO> messages;
}

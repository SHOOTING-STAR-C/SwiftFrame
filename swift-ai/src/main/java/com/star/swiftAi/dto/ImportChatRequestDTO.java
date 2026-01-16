package com.star.swiftAi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

/**
 * 导入聊天记录请求DTO
 *
 * @author SHOOTING_STAR_C
 */
@Data
@Schema(description = "导入聊天记录请求DTO")
public class ImportChatRequestDTO {

    /**
     * 会话列表
     */
    @Schema(description = "会话列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "会话列表不能为空")
    @Valid
    private List<ChatSessionDataDTO> sessions;
}

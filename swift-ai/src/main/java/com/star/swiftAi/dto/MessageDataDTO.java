package com.star.swiftAi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 消息数据DTO（用于导入）
 *
 * @author SHOOTING_STAR_C
 */
@Data
@Schema(description = "消息数据DTO")
public class MessageDataDTO {

    /**
     * 角色
     */
    @Schema(description = "角色：user/assistant/system", requiredMode = Schema.RequiredMode.REQUIRED, example = "user")
    @NotBlank(message = "角色不能为空")
    private String role;

    /**
     * 消息内容
     */
    @Schema(description = "消息内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "你好，请问你是谁？")
    @NotBlank(message = "消息内容不能为空")
    private String content;
}

package com.star.swiftAi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 消息DTO
 *
 * @author SHOOTING_STAR_C
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI聊天消息DTO")
public class MessageDTO {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID", example = "1")
    private Long id;

    /**
     * 会话ID
     */
    @Schema(description = "会话ID", example = "1723456789012345678")
    private String sessionId;

    /**
     * 角色
     */
    @Schema(description = "角色（user/assistant/system）", example = "user")
    private String role;

    /**
     * 消息内容
     */
    @Schema(description = "消息内容", example = "你好")
    private String content;

    /**
     * 使用的token数
     */
    @Schema(description = "使用的token数", example = "5")
    private Integer tokensUsed;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}

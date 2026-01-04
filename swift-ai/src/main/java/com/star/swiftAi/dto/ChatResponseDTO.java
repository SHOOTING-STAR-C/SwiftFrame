package com.star.swiftAi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 聊天响应DTO
 *
 * @author SHOOTING_STAR_C
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "聊天响应DTO")
public class ChatResponseDTO {

    /**
     * 会话ID
     */
    @Schema(description = "会话ID", example = "1723456789012345678")
    private String sessionId;

    /**
     * 消息ID
     */
    @Schema(description = "消息ID", example = "1")
    private Long messageId;

    /**
     * 角色
     */
    @Schema(description = "角色", example = "assistant")
    private String role;

    /**
     * 消息内容
     */
    @Schema(description = "消息内容", example = "你好！我是AI助手。")
    private String content;

    /**
     * 使用的token数
     */
    @Schema(description = "使用的token数", example = "10")
    private Integer tokensUsed;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}

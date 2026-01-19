package com.star.swiftAi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 流式聊天响应DTO
 * 
 * 该DTO与ChatResponseDTO保持字段一致，并增加流式特有的字段
 * 便于前端统一处理流式和非流式响应
 *
 * @author SHOOTING_STAR_C
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "流式聊天响应DTO")
public class StreamChatResponseDTO {

    /**
     * 会话ID
     */
    @Schema(description = "会话ID", example = "1723456789012345678")
    private String sessionId;

    /**
     * 消息ID
     * 流式响应时，第一次响应可能为null，后续响应会有值
     */
    @Schema(description = "消息ID", example = "1")
    private String messageId;

    /**
     * 角色
     */
    @Schema(description = "角色", example = "assistant")
    private String role;

    /**
     * 消息内容（完整累积内容）
     */
    @Schema(description = "消息内容", example = "你好！我是AI助手。")
    private String content;

    /**
     * 增量内容（流式特有）
     * 本次响应的增量部分
     */
    @Schema(description = "增量内容", example = "你好")
    private String delta;

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

    /**
     * 是否完成（流式特有）
     * 标识是否为最后一个响应
     */
    @Schema(description = "是否完成", example = "false")
    private boolean finished;
}

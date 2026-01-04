package com.star.swiftAi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI聊天会话实体
 *
 * @author SHOOTING_STAR_C
 */
@Data
@TableName("ai_chat_session")
public class AiChatSession {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会话ID
     */
    @TableField("session_id")
    private String sessionId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private String userId;

    /**
     * 会话标题
     */
    @TableField("title")
    private String title;

    /**
     * 使用的模型ID
     */
    @TableField("model_id")
    private Long modelId;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 模型名称（非数据库字段，用于关联查询）
     */
    @TableField(exist = false)
    private String modelName;
}

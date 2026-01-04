package com.star.swiftAi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI模型实体
 *
 * @author SHOOTING_STAR_C
 */
@Data
@TableName("ai_model")
public class AiModel {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模型代码
     */
    @TableField("model_code")
    private String modelCode;

    /**
     * 模型名称
     */
    @TableField("model_name")
    private String modelName;

    /**
     * 供应商ID
     */
    @TableField("provider_id")
    private Long providerId;

    /**
     * 是否启用
     */
    @TableField("enabled")
    private Boolean enabled;

    /**
     * 最大token数
     */
    @TableField("max_tokens")
    private Integer maxTokens;

    /**
     * 上下文长度
     */
    @TableField("context_length")
    private Integer contextLength;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

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
     * 供应商名称（非数据库字段，用于关联查询）
     */
    @TableField(exist = false)
    private String providerName;
}

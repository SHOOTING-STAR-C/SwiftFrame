package com.star.swiftAi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI供应商实体
 *
 * @author SHOOTING_STAR_C
 */
@Data
@TableName("ai_provider")
public class AiProvider {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 提供商名称
     */
    @TableField("provider_name")
    private String providerName;
    
    /**
     * 提供商代码
     */
    @TableField("provider_code")
    private String providerCode;
    
    /**
     * 基础URL
     */
    @TableField("base_url")
    private String baseUrl;

    /**
     * API密钥（加密存储）
     */
    @TableField("api_key")
    private String apiKey;

    /**
     * 超时时间（秒）
     */
    @TableField("timeout")
    private Integer timeout;

    /**
     * 最大重试次数
     */
    @TableField("max_retries")
    private Integer maxRetries;

    /**
     * 是否启用
     */
    @TableField("enabled")
    private Boolean enabled;

    /**
     * 优先级
     */
    @TableField("priority")
    private Integer priority;

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
}

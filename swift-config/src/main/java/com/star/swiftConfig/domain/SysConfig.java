package com.star.swiftConfig.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统配置实体类
 * 用于存储系统敏感配置信息，如AI配置、邮件配置等
 * 所有配置值在存储前都会进行AES加密处理
 *
 * @author SHOOTING_STAR_C
 */
@Data
public class SysConfig {
    
    /**
     * 配置ID，主键，自增
     */
    private Long configId;
    
    /**
     * 配置键，唯一标识一个配置项
     * 例如：ai.openai.api_key, mail.smtp.password
     */
    private String configKey;
    
    /**
     * 配置值，存储加密后的数据
     * 存储前会使用AES加密算法进行加密
     */
    private String configValue;
    
    /**
     * 配置类型，用于分类管理不同类型的配置
     * SYSTEM: 系统配置
     * AI: AI相关配置（如OpenAI API Key、模型配置等）
     * MAIL: 邮件配置（如SMTP密码、发件人信息等）
     * DATABASE: 数据库配置
     * THIRD_PARTY: 第三方服务配置
     */
    private String configType;
    
    /**
     * 配置描述，说明该配置的用途
     */
    private String description;
    
    /**
     * 是否启用，用于控制配置是否生效
     * true: 启用该配置
     * false: 禁用该配置
     */
    private Boolean isEnabled;
    
    /**
     * 创建时间，记录配置的创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间，记录配置的最后更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 创建人，记录创建该配置的用户
     */
    private String createdBy;
    
    /**
     * 更新人，记录最后更新该配置的用户
     */
    private String updatedBy;
}

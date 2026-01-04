package com.star.swiftAi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 会话DTO
 *
 * @author SHOOTING_STAR_C
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionDTO {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 会话标题
     */
    private String title;
    
    /**
     * 模型ID
     */
    private Long modelId;
    
    /**
     * 模型名称
     */
    private String modelName;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}

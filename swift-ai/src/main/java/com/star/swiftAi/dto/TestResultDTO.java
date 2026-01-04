package com.star.swiftAi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 测试结果DTO
 *
 * @author SHOOTING_STAR_C
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestResultDTO {
    
    /**
     * 是否成功
     */
    private boolean success;
    
    /**
     * 消息
     */
    private String message;
    
    /**
     * 延迟（毫秒）
     */
    private long latency;
}

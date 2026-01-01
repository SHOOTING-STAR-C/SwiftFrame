package com.star.swiftMail.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 邮件响应DTO
 *
 * @author SHOOTING_STAR_C
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailResponse {
    
    /**
     * 是否发送成功
     */
    private Boolean success;
    
    /**
     * 消息ID
     */
    private String messageId;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 错误信息
     */
    private String error;
}

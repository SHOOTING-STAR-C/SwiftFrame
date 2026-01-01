package com.star.swiftMail.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 邮件请求DTO
 *
 * @author SHOOTING_STAR_C
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailRequest {
    
    /**
     * 收件人邮箱
     */
    @NotBlank(message = "收件人邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String to;
    
    /**
     * 邮件主题
     */
    @NotBlank(message = "邮件主题不能为空")
    private String subject;
    
    /**
     * 邮件内容（文本）
     */
    private String text;
    
    /**
     * 邮件内容（HTML）
     */
    private String html;
    
    /**
     * 模板名称
     */
    private String template;
    
    /**
     * 模板参数
     */
    private Map<String, Object> variables;
}

package com.star.swiftLogin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 重置密码请求
 *
 * @author SHOOTING_STAR_C
 */
@Data
public class ResetPasswordRequest {

    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    private String verificationCode;

    /**
     * 新密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 30, message = "密码长度需在8-30个字符之间")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#_+])[A-Za-z\\d@$!%*?&#_+]+$",
            message = "密码必须包含大小写字母、数字和特殊字符"
    )
    private String newPassword;
}

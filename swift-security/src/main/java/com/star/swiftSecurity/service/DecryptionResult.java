package com.star.swiftSecurity.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 解密结果 DTO
 * 用于替代固定长度数组，提升代码可读性和扩展性
 *
 * @author SHOOTING_STAR_C
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DecryptionResult {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮箱（可选）
     */
    private String email;

    /**
     * 手机号（可选）
     */
    private String phone;
}
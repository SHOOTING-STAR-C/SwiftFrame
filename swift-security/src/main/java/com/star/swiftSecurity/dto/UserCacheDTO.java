package com.star.swiftSecurity.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 用户缓存DTO
 * 用于缓存用户信息、角色和权限，避免循环引用
 *
 * @author SHOOTING_STAR_C
 */
@Data
public class UserCacheDTO {
    
    // ================= 用户基础信息 =================
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    
    // ================= 账户状态 =================
    private boolean enabled;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    
    // ================= 安全追踪字段 =================
    private int failedLoginAttempts;
    private LocalDateTime lockUntil;
    private LocalDateTime passwordChangedAt;
    private LocalDateTime lastLoginAt;
    private String lastLoginIp;
    private LocalDateTime createdAt;
    
    // ================= 角色和权限 =================
    private Set<String> roleNames;  // 角色名称集合，如 ["ROLE_ADMIN", "ROLE_USER"]
    private Set<String> authorityNames;  // 权限名称集合，如 ["user:read", "user:write"]
}

package com.star.swiftSecurity.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.star.swiftSecurity.properties.AccountSecurityProperties;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户实体
 *
 * @author SHOOTING_STAR_C
 */
@Data
public class SwiftUserDetails implements UserDetails {
    //================= 用户基础信息 =================
    private UUID userId;

    private String username;

    private String fullName;

    private String password;

    private String email;

    private String phone;


    // ================= 账户状态管理 =================
    private boolean enabled = true;  // 账户是否启用

    private boolean accountNonExpired = true;  // 账户是否未过期

    private boolean accountNonLocked = true;  // 账户是否未锁定

    private boolean credentialsNonExpired = true;  // 凭证是否未过期

    // ================= 安全追踪字段 =================
    private int failedLoginAttempts = 0;  // 连续登录失败次数

    private LocalDateTime lockUntil;  // 账户锁定截止时间

    private LocalDateTime passwordChangedAt;  // 密码最后修改时间

    private LocalDateTime lastLoginAt;  // 最后登录时间

    private String lastLoginIp;  // 最后登录IP地址

    private LocalDateTime createdAt;


    private Set<SwiftUserRole> userRoles = new HashSet<>();

    // ================= 状态管理方法 =================
    // Spring Security 接口实现
    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired &&
                (lockUntil == null || lockUntil.isBefore(LocalDateTime.now()));
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    // ================= 安全操作 =================

    /**
     * 记录登录失败
     */
    public void recordLoginFailure(AccountSecurityProperties accountSecurityProperties) {
        failedLoginAttempts++;
        if (failedLoginAttempts >= accountSecurityProperties.getMaxLoginAttempts()) {
            lockAccount(accountSecurityProperties.getLockDurationMinutes());
        }
    }

    /**
     * 锁定账户
     *
     * @param minutes 锁定时间（分钟）
     */
    public void lockAccount(int minutes) {
        this.accountNonLocked = false;
        this.lockUntil = LocalDateTime.now().plusMinutes(minutes);
    }

    /**
     * 解锁账户
     */
    public void unlockAccount() {
        this.accountNonLocked = true;
        this.failedLoginAttempts = 0;
        this.lockUntil = null;
    }

    /**
     * 更新密码
     *
     * @param newPassword 新密码（加密前）
     * @param encoder     密码编码器
     */
    public void changePassword(String newPassword, PasswordEncoder encoder) {
        this.password = encoder.encode(newPassword);
        this.passwordChangedAt = LocalDateTime.now();
        this.credentialsNonExpired = true;
        this.failedLoginAttempts = 0; // 重置失败计数
    }

    /**
     * (获取权限)实现 UserDetails 接口方法
     *
     * @return Collection<? extends GrantedAuthority>
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userRoles.stream()
                .flatMap(userRole -> userRole.getRole().getRoleAuthorities().stream())
                .map(roleAuth -> new SimpleGrantedAuthority(roleAuth.getAuthority().getName()))
                .collect(Collectors.toSet());
    }

    /**
     * 防止密码序列化到JSON
     *
     * @return String
     */
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public Map<String, Object> toSafeMap() {
        return Map.of(
                "id", userId,
                "username", username
                // 排除敏感字段
        );
    }
}

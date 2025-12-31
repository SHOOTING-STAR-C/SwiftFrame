package com.star.swiftSecurity.utils;

import com.star.swiftSecurity.constant.RoleConstants;
import com.star.swiftSecurity.entity.SwiftUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全工具类
 * 提供便捷的权限检查方法
 *
 * @author SHOOTING_STAR_C
 */
public class SecurityUtils {

    /**
     * 获取当前登录用户
     *
     * @return SwiftUserDetails
     */
    public static SwiftUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof SwiftUserDetails) {
            return (SwiftUserDetails) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * 获取当前用户ID
     *
     * @return UUID
     */
    public static String getCurrentUserId() {
        SwiftUserDetails user = getCurrentUser();
        return user != null ? user.getUserId().toString() : null;
    }

    /**
     * 获取当前用户名
     *
     * @return String
     */
    public static String getCurrentUsername() {
        SwiftUserDetails user = getCurrentUser();
        return user != null ? user.getUsername() : null;
    }

    /**
     * 检查当前用户是否已认证
     *
     * @return boolean
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() 
                && !"anonymousUser".equals(authentication.getPrincipal());
    }

    /**
     * 检查当前用户是否是超级管理员
     *
     * @return boolean
     */
    public static boolean isSuperAdmin() {
        return hasRole(RoleConstants.ROLE_SUPER_ADMIN);
    }

    /**
     * 检查当前用户是否拥有指定角色
     *
     * @param role 角色名称
     * @return boolean
     */
    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(role));
    }

    /**
     * 检查当前用户是否拥有任意一个指定角色
     *
     * @param roles 角色名称数组
     * @return boolean
     */
    public static boolean hasAnyRole(String... roles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> {
                    for (String role : roles) {
                        if (authority.getAuthority().equals(role)) {
                            return true;
                        }
                    }
                    return false;
                });
    }

    /**
     * 检查当前用户是否拥有指定权限
     *
     * @param permission 权限名称
     * @return boolean
     */
    public static boolean hasPermission(String permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(permission));
    }

    /**
     * 检查当前用户是否拥有任意一个指定权限
     *
     * @param permissions 权限名称数组
     * @return boolean
     */
    public static boolean hasAnyPermission(String... permissions) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> {
                    for (String permission : permissions) {
                        if (authority.getAuthority().equals(permission)) {
                            return true;
                        }
                    }
                    return false;
                });
    }

    /**
     * 检查当前用户是否拥有所有指定权限
     *
     * @param permissions 权限名称数组
     * @return boolean
     */
    public static boolean hasAllPermissions(String... permissions) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        for (String permission : permissions) {
            final String perm = permission;
            boolean hasPerm = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals(perm));
            if (!hasPerm) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查当前用户是否可以访问资源（超级管理员可以访问所有资源）
     *
     * @param permission 所需权限
     * @return boolean
     */
    public static boolean canAccess(String permission) {
        // 超级管理员可以访问所有资源
        if (isSuperAdmin()) {
            return true;
        }
        // 检查是否拥有指定权限
        return hasPermission(permission);
    }

    /**
     * 检查当前用户是否可以访问资源（超级管理员可以访问所有资源）
     *
     * @param permissions 所需权限数组（满足任意一个即可）
     * @return boolean
     */
    public static boolean canAccessAny(String... permissions) {
        // 超级管理员可以访问所有资源
        if (isSuperAdmin()) {
            return true;
        }
        // 检查是否拥有任意一个指定权限
        return hasAnyPermission(permissions);
    }

    /**
     * 检查当前用户是否可以访问资源（超级管理员可以访问所有资源）
     *
     * @param permissions 所需权限数组（必须满足所有）
     * @return boolean
     */
    public static boolean canAccessAll(String... permissions) {
        // 超级管理员可以访问所有资源
        if (isSuperAdmin()) {
            return true;
        }
        // 检查是否拥有所有指定权限
        return hasAllPermissions(permissions);
    }
}

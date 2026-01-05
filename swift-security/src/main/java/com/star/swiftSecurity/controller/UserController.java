package com.star.swiftSecurity.controller;

import com.star.swiftCommon.domain.PageResult;
import com.star.swiftCommon.domain.PubResult;
import com.star.swiftSecurity.constant.AuthorityConstants;
import com.star.swiftSecurity.entity.SwiftRole;
import com.star.swiftSecurity.entity.SwiftUserDetails;
import com.star.swiftSecurity.service.SwiftUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户管理接口
 *
 * @author SHOOTING_STAR_C
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "系统用户相关接口")
public class UserController {

    private final SwiftUserService userService;

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/info")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    public PubResult<Map<String, Object>> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof SwiftUserDetails) {
            SwiftUserDetails userDetails = (SwiftUserDetails) authentication.getPrincipal();
            
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", userDetails.getUserId());
            userInfo.put("username", userDetails.getUsername());
            userInfo.put("fullName", userDetails.getFullName());
            userInfo.put("email", userDetails.getEmail());
            userInfo.put("phone", userDetails.getPhone());
            userInfo.put("enabled", userDetails.isEnabled());
            userInfo.put("lastLoginAt", userDetails.getLastLoginAt());
            userInfo.put("createdAt", userDetails.getCreatedAt());
            userInfo.put("roles", userDetails.getUserRoles().stream()
                    .map(userRole -> userRole.getRole().getName())
                    .toList());
            userInfo.put("authorities", userDetails.getAuthorities().stream()
                    .map(Object::toString)
                    .toList());
            
            return PubResult.success(userInfo);
        }
        
        return PubResult.error("未找到用户信息");
    }

    /**
     * 创建用户
     *
     * @param user 用户信息
     * @return 创建的用户
     */
    @PostMapping
    @PreAuthorize("hasAuthority('" + AuthorityConstants.USER_CREATE + "')")
    @Operation(summary = "创建用户", description = "创建新的系统用户")
    public PubResult<SwiftUserDetails> createUser(@Valid @RequestBody SwiftUserDetails user) {
        return PubResult.success(userService.createUser(user));
    }

    /**
     * 更新用户信息
     *
     * @param userId 用户ID
     * @param user   用户信息
     * @return 更新后的用户
     */
    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.USER_UPDATE + "')")
    @Operation(summary = "更新用户", description = "更新指定用户的信息")
    public PubResult<SwiftUserDetails> updateUser(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Valid @RequestBody SwiftUserDetails user) {
        user.setUserId(userId);
        return PubResult.success(userService.updateUser(user));
    }

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.USER_DELETE + "')")
    @Operation(summary = "删除用户", description = "删除指定的用户")
    public PubResult<Void> deleteUser(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId) {
        userService.deleteUser(userId);
        return PubResult.success();
    }

    /**
     * 根据ID获取用户
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.USER_READ + "')")
    @Operation(summary = "获取用户详情", description = "根据ID获取用户的详细信息")
    public PubResult<SwiftUserDetails> getUserById(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId) {
        return PubResult.success(userService.getUserById(userId));
    }

    /**
     * 获取所有用户（分页）
     *
     * @param current 当前页码
     * @param size    每页大小
     * @return 用户分页列表
     */
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.USER_READ + "')")
    @Operation(summary = "获取所有用户", description = "获取系统中所有的用户列表（支持分页）")
    public PageResult<SwiftUserDetails> getAllUsers(
            @Parameter(description = "当前页码", example = "1") 
            @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小", example = "10") 
            @RequestParam(defaultValue = "10") long size) {
        return userService.getUserPage(current, size);
    }

    /**
     * 更改用户密码
     *
     * @param userId      用户ID
     * @param newPassword 新密码
     * @return 更新后的用户
     */
    @PutMapping("/{userId}/password")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.USER_PASSWORD + "')")
    @Operation(summary = "修改密码", description = "修改指定用户的密码")
    public PubResult<SwiftUserDetails> changePassword(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Parameter(description = "新密码", required = true) @RequestBody String newPassword) {
        return PubResult.success(userService.changePassword(userId, newPassword));
    }

    /**
     * 分配角色给用户
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 操作结果
     */
    @PostMapping("/{userId}/roles/{roleId}")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.USER_MANAGE + "')")
    @Operation(summary = "分配角色", description = "将指定角色分配给用户")
    public PubResult<Void> assignRoleToUser(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Parameter(description = "角色ID", required = true) @PathVariable Long roleId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String assignedBy = authentication != null ? authentication.getName() : "system";
        userService.assignRoleToUser(userId, roleId, assignedBy);
        return PubResult.success();
    }

    /**
     * 收回用户的角色
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 操作结果
     */
    @DeleteMapping("/{userId}/roles/{roleId}")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.USER_MANAGE + "')")
    @Operation(summary = "收回角色", description = "从用户中收回指定的角色")
    public PubResult<Void> removeRoleFromUser(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Parameter(description = "角色ID", required = true) @PathVariable Long roleId) {
        userService.removeRoleFromUser(userId, roleId);
        return PubResult.success();
    }

    /**
     * 获取用户拥有的角色
     *
     * @param userId 用户ID
     * @return 角色集合
     */
    @GetMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.USER_READ + "')")
    @Operation(summary = "获取用户角色", description = "获取指定用户拥有的所有角色")
    public PubResult<Set<SwiftRole>> getUserRoles(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId) {
        return PubResult.success(userService.getUserRoles(userId));
    }

    /**
     * 启用用户
     *
     * @param userId 用户ID
     * @return 操作结果
     */
    @PutMapping("/{userId}/enable")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.USER_MANAGE + "')")
    @Operation(summary = "启用用户", description = "启用指定的用户账户")
    public PubResult<Void> enableUser(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId) {
        userService.enableUser(userId);
        return PubResult.success();
    }

    /**
     * 禁用用户
     *
     * @param userId 用户ID
     * @return 操作结果
     */
    @PutMapping("/{userId}/disable")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.USER_MANAGE + "')")
    @Operation(summary = "禁用用户", description = "禁用指定的用户账户")
    public PubResult<Void> disableUser(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId) {
        userService.disableUser(userId);
        return PubResult.success();
    }

    /**
     * 解锁用户
     *
     * @param userId 用户ID
     * @return 操作结果
     */
    @PutMapping("/{userId}/unlock")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.USER_MANAGE + "')")
    @Operation(summary = "解锁用户", description = "解锁指定的用户账户")
    public PubResult<Void> unlockUser(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId) {
        userService.unlockUser(userId);
        return PubResult.success();
    }

    /**
     * 锁定用户
     *
     * @param userId 用户ID
     * @return 操作结果
     */
    @PutMapping("/{userId}/lock")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.USER_MANAGE + "')")
    @Operation(summary = "锁定用户", description = "锁定指定的用户账户")
    public PubResult<Void> lockUser(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId) {
        userService.lockUser(userId);
        return PubResult.success();
    }
}

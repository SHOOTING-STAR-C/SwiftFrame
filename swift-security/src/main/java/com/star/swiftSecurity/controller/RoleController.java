package com.star.swiftSecurity.controller;

import com.star.swiftCommon.domain.PageResult;
import com.star.swiftCommon.domain.PubResult;
import com.star.swiftSecurity.constant.AuthorityConstants;
import com.star.swiftSecurity.entity.SwiftAuthority;
import com.star.swiftSecurity.entity.SwiftRole;
import com.star.swiftSecurity.entity.SwiftUserDetails;
import com.star.swiftSecurity.service.SwiftRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 角色管理接口
 *
 * @author SHOOTING_STAR_C
 */
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Tag(name = "角色管理", description = "系统角色相关接口")
public class RoleController {

    private final SwiftRoleService roleService;

    /**
     * 创建角色
     *
     * @param role 角色信息
     * @return 创建的角色
     */
    @PostMapping
    @PreAuthorize("hasAuthority('" + AuthorityConstants.ROLE_CREATE + "')")
    @Operation(summary = "创建角色", description = "创建新的系统角色")
    public PubResult<SwiftRole> createRole(@Valid @RequestBody SwiftRole role) {
        return PubResult.success(roleService.createRole(role));
    }

    /**
     * 更新角色信息
     *
     * @param roleId 角色ID
     * @param role   角色信息
     * @return 更新后的角色
     */
    @PutMapping("/{roleId}")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.ROLE_UPDATE + "')")
    @Operation(summary = "更新角色", description = "更新指定角色的信息")
    public PubResult<SwiftRole> updateRole(
            @Parameter(description = "角色ID", required = true) @PathVariable Long roleId,
            @Valid @RequestBody SwiftRole role) {
        role.setRoleId(roleId);
        return PubResult.success(roleService.updateRole(role));
    }

    /**
     * 删除角色
     *
     * @param roleId 角色ID
     * @return 操作结果
     */
    @DeleteMapping("/{roleId}")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.ROLE_DELETE + "')")
    @Operation(summary = "删除角色", description = "删除指定的角色（不能删除已分配用户的角色）")
    public PubResult<Void> deleteRole(
            @Parameter(description = "角色ID", required = true) @PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return PubResult.success();
    }

    /**
     * 根据ID获取角色
     *
     * @param roleId 角色ID
     * @return 角色信息
     */
    @GetMapping("/{roleId}")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.ROLE_READ + "')")
    @Operation(summary = "获取角色详情", description = "根据ID获取角色的详细信息")
    public PubResult<SwiftRole> getRoleById(
            @Parameter(description = "角色ID", required = true) @PathVariable Long roleId) {
        return PubResult.success(roleService.getRoleById(roleId));
    }

    /**
     * 分页获取角色
     *
     * @param page 页码
     * @param size 每页大小
     * @return 角色分页列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('" + AuthorityConstants.ROLE_READ + "')")
    @Operation(summary = "分页获取角色", description = "分页获取系统中的角色列表")
    public PageResult<SwiftRole> getRolePage(
            @Parameter(description = "页码", example = "1") 
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "10") 
            @RequestParam(defaultValue = "10") Integer size) {
        return roleService.getRolePage(page, size);
    }

    /**
     * 授予权限给角色
     *
     * @param roleId      角色ID
     * @param authorityId 权限ID
     * @return 操作结果
     */
    @PostMapping("/{roleId}/authorities/{authorityId}")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.ROLE_MANAGE + "')")
    @Operation(summary = "授予权限", description = "将指定权限授予给角色")
    public PubResult<Void> grantAuthorityToRole(
            @Parameter(description = "角色ID", required = true) @PathVariable Long roleId,
            @Parameter(description = "权限ID", required = true) @PathVariable Long authorityId) {
        roleService.grantAuthorityToRole(roleId, authorityId);
        return PubResult.success();
    }

    /**
     * 收回角色的权限
     *
     * @param roleId      角色ID
     * @param authorityId 权限ID
     * @return 操作结果
     */
    @DeleteMapping("/{roleId}/authorities/{authorityId}")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.ROLE_MANAGE + "')")
    @Operation(summary = "收回权限", description = "从角色中收回指定的权限")
    public PubResult<Void> revokeAuthorityFromRole(
            @Parameter(description = "角色ID", required = true) @PathVariable Long roleId,
            @Parameter(description = "权限ID", required = true) @PathVariable Long authorityId) {
        roleService.revokeAuthorityFromRole(roleId, authorityId);
        return PubResult.success();
    }

    /**
     * 获取角色拥有的权限
     *
     * @param roleId 角色ID
     * @return 权限集合
     */
    @GetMapping("/{roleId}/authorities")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.ROLE_READ + "')")
    @Operation(summary = "获取角色权限", description = "获取指定角色拥有的所有权限")
    public PubResult<Set<SwiftAuthority>> getRoleAuthorities(
            @Parameter(description = "角色ID", required = true) @PathVariable Long roleId) {
        return PubResult.success(roleService.getRoleAuthorities(roleId));
    }

    /**
     * 获取拥有该角色的用户
     *
     * @param roleId 角色ID
     * @return 用户集合
     */
    @GetMapping("/{roleId}/users")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.ROLE_READ + "')")
    @Operation(summary = "获取角色用户", description = "获取拥有指定角色的所有用户")
    public PubResult<Set<SwiftUserDetails>> getUsersWithRole(
            @Parameter(description = "角色ID", required = true) @PathVariable Long roleId) {
        return PubResult.success(roleService.getUsersWithRole(roleId));
    }
}

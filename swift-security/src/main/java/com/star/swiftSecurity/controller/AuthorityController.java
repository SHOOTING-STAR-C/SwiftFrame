package com.star.swiftSecurity.controller;

import com.star.swiftCommon.domain.PubResult;
import com.star.swiftSecurity.constant.AuthorityConstants;
import com.star.swiftSecurity.entity.SwiftAuthority;
import com.star.swiftSecurity.entity.SwiftRole;
import com.star.swiftSecurity.service.SwiftAuthorityService;
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
 * 权限管理接口
 *
 * @author SHOOTING_STAR_C
 */
@RestController
@RequestMapping("/authorities")
@RequiredArgsConstructor
@Tag(name = "权限管理", description = "系统权限相关接口")
public class AuthorityController {

    private final SwiftAuthorityService authorityService;

    /**
     * 创建权限
     *
     * @param authority 权限信息
     * @return 创建的权限
     */
    @PostMapping
    @PreAuthorize("hasAuthority('" + AuthorityConstants.AUTHORITY_CREATE + "')")
    @Operation(summary = "创建权限", description = "创建新的系统权限")
    public PubResult<SwiftAuthority> createAuthority(@Valid @RequestBody SwiftAuthority authority) {
        return PubResult.success(authorityService.createAuthority(authority));
    }

    /**
     * 更新权限信息
     *
     * @param authorityId 权限ID
     * @param authority   权限信息
     * @return 更新后的权限
     */
    @PutMapping("/{authorityId}")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.AUTHORITY_UPDATE + "')")
    @Operation(summary = "更新权限", description = "更新指定权限的信息")
    public PubResult<SwiftAuthority> updateAuthority(
            @Parameter(description = "权限ID", required = true) @PathVariable Long authorityId,
            @Valid @RequestBody SwiftAuthority authority) {
        authority.setAuthorityId(authorityId);
        return PubResult.success(authorityService.updateAuthority(authority));
    }

    /**
     * 删除权限
     *
     * @param authorityId 权限ID
     * @return 操作结果
     */
    @DeleteMapping("/{authorityId}")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.AUTHORITY_DELETE + "')")
    @Operation(summary = "删除权限", description = "删除指定的权限（不能删除已分配给角色的权限）")
    public PubResult<Void> deleteAuthority(
            @Parameter(description = "权限ID", required = true) @PathVariable Long authorityId) {
        authorityService.deleteAuthority(authorityId);
        return PubResult.success();
    }

    /**
     * 根据ID获取权限
     *
     * @param authorityId 权限ID
     * @return 权限信息
     */
    @GetMapping("/{authorityId}")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.AUTHORITY_READ + "')")
    @Operation(summary = "获取权限详情", description = "根据ID获取权限的详细信息")
    public PubResult<SwiftAuthority> getAuthorityById(
            @Parameter(description = "权限ID", required = true) @PathVariable Long authorityId) {
        return PubResult.success(authorityService.getAuthorityById(authorityId));
    }

    /**
     * 根据权限名获取权限
     *
     * @param name 权限名
     * @return 权限信息
     */
    @GetMapping("/name/{name}")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.AUTHORITY_READ + "')")
    @Operation(summary = "根据权限名获取权限", description = "根据权限名获取权限信息")
    public PubResult<SwiftAuthority> getAuthorityByName(
            @Parameter(description = "权限名", required = true) @PathVariable String name) {
        return PubResult.success(authorityService.getAuthorityByName(name));
    }

    /**
     * 获取所有权限
     *
     * @return 权限列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('" + AuthorityConstants.AUTHORITY_READ + "')")
    @Operation(summary = "获取所有权限", description = "获取系统中所有的权限列表")
    public PubResult<List<SwiftAuthority>> getAllAuthorities() {
        return PubResult.success(authorityService.getAllAuthorities());
    }

    /**
     * 根据权限名列表获取权限
     *
     * @param names 权限名列表
     * @return 权限列表
     */
    @PostMapping("/batch")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.AUTHORITY_READ + "')")
    @Operation(summary = "批量获取权限", description = "根据权限名列表批量获取权限信息")
    public PubResult<List<SwiftAuthority>> getAuthoritiesByNames(
            @RequestBody List<String> names) {
        return PubResult.success(authorityService.getAuthoritiesByNames(names));
    }

    /**
     * 获取拥有该权限的角色
     *
     * @param authorityId 权限ID
     * @return 角色集合
     */
    @GetMapping("/{authorityId}/roles")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.AUTHORITY_READ + "')")
    @Operation(summary = "获取权限角色", description = "获取拥有指定权限的所有角色")
    public PubResult<Set<SwiftRole>> getRolesWithAuthority(
            @Parameter(description = "权限ID", required = true) @PathVariable Long authorityId) {
        return PubResult.success(authorityService.getRolesWithAuthority(authorityId));
    }
}

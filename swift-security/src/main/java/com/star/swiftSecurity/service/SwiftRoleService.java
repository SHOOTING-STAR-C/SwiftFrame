package com.star.swiftSecurity.service;

import com.star.swiftCommon.domain.PageResult;
import com.star.swiftSecurity.entity.SwiftAuthority;
import com.star.swiftSecurity.entity.SwiftRole;
import com.star.swiftSecurity.entity.SwiftUserDetails;

import java.util.List;
import java.util.Set;

public interface SwiftRoleService {
    /**
     * 创建角色
     *
     * @param role role
     * @return SwiftRole
     */
    SwiftRole createRole(SwiftRole role);

    /**
     * 更新角色信息
     *
     * @param role role
     * @return SwiftRole
     */
    SwiftRole updateRole(SwiftRole role);

    /**
     * 删除角色
     *
     * @param roleId roleId
     */
    void deleteRole(Long roleId);

    /**
     * 根据io获取角色
     *
     * @param roleId roleId
     * @return SwiftRole
     */
    SwiftRole getRoleById(Long roleId);

    /**
     * 获取所有角色
     *
     * @return List<SwiftRole>
     */
    List<SwiftRole> getAllRoles();

    /**
     * 分页获取角色
     *
     * @param page 页码
     * @param size 每页大小
     * @return PageResult<SwiftRole>
     */
    PageResult<SwiftRole> getRolePage(long page, long size);

    /**
     * 根据角色名查找角色
     *
     * @param name 角色名
     * @return SwiftRole
     */
    SwiftRole findByName(String name);

    /**
     * 授予权限给角色
     *
     * @param roleId      roleId
     * @param authorityId authorityId
     */
    void grantAuthorityToRole(Long roleId, Long authorityId);

    /**
     * 收回角色的权限
     * @param roleId roleId
     * @param authorityId authorityId
     */
    void revokeAuthorityFromRole(Long roleId, Long authorityId);

    /**
     * 获取角色拥有的权限
     * @param roleId roleId
     * @return Set<SwiftAuthority>
     */
    Set<SwiftAuthority> getRoleAuthorities(Long roleId);

    /**
     *
     * @param roleId roleId
     * @return Set<SwiftUserDetails>
     */
    Set<SwiftUserDetails> getUsersWithRole(Long roleId);
}

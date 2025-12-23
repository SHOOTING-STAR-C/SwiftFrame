package com.star.swiftSecurity.service;

import com.star.swiftSecurity.entity.SwiftAuthority;
import com.star.swiftSecurity.entity.SwiftRole;
import com.star.swiftSecurity.entity.SwiftUserDetails;

import java.util.List;
import java.util.Set;
import java.util.UUID;

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
    void deleteRole(UUID roleId);

    /**
     * 根据io获取角色
     *
     * @param roleId roleId
     * @return SwiftRole
     */
    SwiftRole getRoleById(UUID roleId);

    /**
     * 获取所有角色
     *
     * @return List<SwiftRole>
     */
    List<SwiftRole> getAllRoles();

    /**
     * 授予权限给角色
     *
     * @param roleId      roleId
     * @param authorityId authorityId
     */
    void grantAuthorityToRole(UUID roleId, UUID authorityId);

    /**
     * 收回角色的权限
     * @param roleId roleId
     * @param authorityId authorityId
     */
    void revokeAuthorityFromRole(UUID roleId, UUID authorityId);

    /**
     * 获取角色拥有的权限
     * @param roleId roleId
     * @return Set<SwiftAuthority>
     */
    Set<SwiftAuthority> getRoleAuthorities(UUID roleId);

    /**
     *
     * @param roleId roleId
     * @return Set<SwiftUserDetails>
     */
    Set<SwiftUserDetails> getUsersWithRole(UUID roleId);
}

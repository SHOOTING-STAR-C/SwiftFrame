package com.star.swiftSecurity.service.impl;

import com.star.swiftSecurity.entity.*;
import com.star.swiftSecurity.exception.BusinessException;
import com.star.swiftSecurity.exception.DuplicateEntityException;
import com.star.swiftSecurity.exception.OperationNotAllowedException;
import com.star.swiftSecurity.mapper.SwiftAuthorityMapper;
import com.star.swiftSecurity.mapper.SwiftRoleAuthorityMapper;
import com.star.swiftSecurity.mapper.SwiftRoleMapper;
import com.star.swiftSecurity.mapper.SwiftUserRoleMapper;
import com.star.swiftSecurity.service.SwiftRoleService;
import com.star.swiftCommon.utils.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色服务实现
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SwiftRoleServiceImpl implements SwiftRoleService {
    private final SwiftRoleMapper roleMapper;
    private final SwiftAuthorityMapper authorityMapper;
    private final SwiftRoleAuthorityMapper roleAuthorityMapper;
    private final SwiftUserRoleMapper userRoleMapper;

    /**
     * 创建角色
     *
     * @param role role
     * @return SwiftRole
     */
    @Override
    public SwiftRole createRole(SwiftRole role) {
        if (roleMapper.existsByName(role.getName())) {
            throw new DuplicateEntityException("角色名称已存在");
        }
        if (role.getRoleId() == null) {
            role.setRoleId(SnowflakeIdGenerator.generateId());
        }
        roleMapper.insert(role);
        return role;
    }

    @Override
    public SwiftRole updateRole(SwiftRole role) {
        SwiftRole existing = getRoleById(role.getRoleId());

        if (!existing.getName().equals(role.getName()) &&
                roleMapper.existsByName(role.getName())) {
            throw new DuplicateEntityException("角色名称已存在");
        }

        existing.setName(role.getName());
        existing.setDescription(role.getDescription());
        roleMapper.update(existing);
        return existing;
    }

    /**
     * 删除角色
     *
     * @param roleId roleId
     */
    @Override
    public void deleteRole(Long roleId) {
        // 检查是否有用户关联
        if (userRoleMapper.countByRole(roleId) > 0) {
            throw new OperationNotAllowedException("无法删除已分配用户的角色");
        }

        roleMapper.deleteById(roleId);
    }

    /**
     * 根据io获取角色
     *
     * @param roleId roleId
     * @return SwiftRole
     */
    @Override
    public SwiftRole getRoleById(Long roleId) {
        SwiftRole role = roleMapper.findById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        return role;
    }

    /**
     * 获取所有角色
     *
     * @return List<SwiftRole>
     */
    @Override
    public List<SwiftRole> getAllRoles() {
        return roleMapper.findAll();
    }

    /**
     * 授予权限给角色
     * @param roleId      roleId
     * @param authorityId authorityId
     */
    @Override
    public void grantAuthorityToRole(Long roleId, Long authorityId) {
        SwiftRole role = getRoleById(roleId);
        SwiftAuthority authority = authorityMapper.findById(authorityId);
        if (authority == null) {
            throw new BusinessException("权限不存在");
        }

        if (roleAuthorityMapper.existsByRoleAndAuthority(roleId, authorityId)) {
            throw new DuplicateEntityException("角色已拥有该权限");
        }

        SwiftRoleAuthority roleAuthority = new SwiftRoleAuthority();
        SwiftRoleAuthorityId id = new SwiftRoleAuthorityId();
        id.setRoleId(roleId);
        id.setAuthorityId(authorityId);
        roleAuthority.setId(id);
        roleAuthority.setRole(role);
        roleAuthority.setAuthority(authority);
        roleAuthorityMapper.insert(roleId, authorityId);
    }

    /**
     * 收回角色的权限
     * @param roleId roleId
     * @param authorityId authorityId
     */
    @Override
    public void revokeAuthorityFromRole(Long roleId, Long authorityId) {
        roleAuthorityMapper.deleteByRoleAndAuthority(roleId, authorityId);
    }

    /**
     * 获取角色拥有的权限
     * @param roleId roleId
     * @return Set<SwiftAuthority>
     */
    @Override
    public Set<SwiftAuthority> getRoleAuthorities(Long roleId) {
        return roleAuthorityMapper.findByRole(roleId).stream()
                .map(SwiftRoleAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

    /**
     * 获取拥有角色的用户
     * @param roleId roleId
     * @return Set<SwiftUserDetails>
     */
    @Override
    public Set<SwiftUserDetails> getUsersWithRole(Long roleId) {
        return userRoleMapper.findByRole(roleId).stream()
                .map(SwiftUserRole::getUser)
                .collect(Collectors.toSet());
    }
}

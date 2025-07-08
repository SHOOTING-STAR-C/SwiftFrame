package com.star.swiftSecurity.service.impl;

import com.star.swiftSecurity.entity.*;
import com.star.swiftSecurity.exception.DuplicateEntityException;
import com.star.swiftSecurity.exception.OperationNotAllowedException;
import com.star.swiftSecurity.repository.SwiftAuthorityRepository;
import com.star.swiftSecurity.repository.SwiftRoleAuthorityRepository;
import com.star.swiftSecurity.repository.SwiftRoleRepository;
import com.star.swiftSecurity.repository.SwiftUserRoleRepository;
import com.star.swiftSecurity.service.SwiftRoleService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    private final SwiftRoleRepository roleRepository;
    private final SwiftAuthorityRepository authorityRepository;
    private final SwiftRoleAuthorityRepository roleAuthorityRepository;
    private final SwiftUserRoleRepository userRoleRepository;

    /**
     * 创建角色
     *
     * @param role role
     * @return SwiftRole
     */
    @Override
    public SwiftRole createRole(SwiftRole role) {
        if (roleRepository.existsByName(role.getName())) {
            throw new DuplicateEntityException("角色名称已存在");
        }
        return roleRepository.save(role);
    }

    @Override
    public SwiftRole updateRole(SwiftRole role) {
        SwiftRole existing = getRoleById(role.getRoleId());

        if (!existing.getName().equals(role.getName()) &&
                roleRepository.existsByName(role.getName())) {
            throw new DuplicateEntityException("角色名称已存在");
        }

        existing.setName(role.getName());
        existing.setDescription(role.getDescription());
        return roleRepository.save(existing);
    }

    /**
     * 删除角色
     *
     * @param roleId roleId
     */
    @Override
    public void deleteRole(Long roleId) {
        SwiftRole role = getRoleById(roleId);

        // 检查是否有用户关联
        if (userRoleRepository.countByRole(role) > 0) {
            throw new OperationNotAllowedException("无法删除已分配用户的角色");
        }

        roleRepository.delete(role);
    }

    /**
     * 根据io获取角色
     *
     * @param roleId roleId
     * @return SwiftRole
     */
    @Override
    public SwiftRole getRoleById(Long roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("角色不存在"));
    }

    /**
     * 获取所有角色
     *
     * @return List<SwiftRole>
     */
    @Override
    public List<SwiftRole> getAllRoles() {
        return roleRepository.findAll();
    }

    /**
     * 授予权限给角色
     * @param roleId      roleId
     * @param authorityId authorityId
     */
    @Override
    public void grantAuthorityToRole(Long roleId, Long authorityId) {
        SwiftRole role = getRoleById(roleId);
        SwiftAuthority authority = authorityRepository.findById(authorityId)
                .orElseThrow(() -> new EntityNotFoundException("权限不存在"));

        if (roleAuthorityRepository.existsByRoleAndAuthority(role, authority)) {
            throw new DuplicateEntityException("角色已拥有该权限");
        }

        SwiftRoleAuthority roleAuthority = new SwiftRoleAuthority();
        roleAuthority.setRole(role);
        roleAuthority.setAuthority(authority);
        roleAuthorityRepository.save(roleAuthority);
    }

    /**
     * 收回角色的权限
     * @param roleId roleId
     * @param authorityId authorityId
     */
    @Override
    public void revokeAuthorityFromRole(Long roleId, Long authorityId) {
        SwiftRole role = getRoleById(roleId);
        SwiftAuthority authority = authorityRepository.findById(authorityId)
                .orElseThrow(() -> new EntityNotFoundException("权限不存在"));

        roleAuthorityRepository.deleteByRoleAndAuthority(role, authority);
    }

    /**
     * 获取角色拥有的权限
     * @param roleId roleId
     * @return Set<SwiftAuthority>
     */
    @Override
    public Set<SwiftAuthority> getRoleAuthorities(Long roleId) {
        SwiftRole role = getRoleById(roleId);
        return roleAuthorityRepository.findByRole(role).stream()
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
        SwiftRole role = getRoleById(roleId);
        return userRoleRepository.findByRole(role).stream()
                .map(SwiftUserRole::getUser)
                .collect(Collectors.toSet());
    }
}

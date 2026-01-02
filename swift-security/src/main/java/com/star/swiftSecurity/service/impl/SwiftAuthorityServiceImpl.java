package com.star.swiftSecurity.service.impl;

import com.star.swiftSecurity.entity.SwiftAuthority;
import com.star.swiftSecurity.entity.SwiftRole;
import com.star.swiftSecurity.entity.SwiftRoleAuthority;
import com.star.swiftSecurity.exception.BusinessException;
import com.star.swiftSecurity.exception.DuplicateEntityException;
import com.star.swiftSecurity.exception.OperationNotAllowedException;
import com.star.swiftSecurity.mapper.mysql.SwiftAuthorityMapper;
import com.star.swiftSecurity.mapper.mysql.SwiftRoleAuthorityMapper;
import com.star.swiftSecurity.service.SwiftAuthorityService;
import com.star.swiftCommon.utils.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限服务实现
 *
 * @author SHOOTING_STAR_C
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SwiftAuthorityServiceImpl implements SwiftAuthorityService {

    private final SwiftAuthorityMapper authorityMapper;
    private final SwiftRoleAuthorityMapper roleAuthorityMapper;

    /**
     * 创建权限
     *
     * @param authority 权限信息
     * @return 创建的权限
     */
    @Override
    public SwiftAuthority createAuthority(SwiftAuthority authority) {
        // 检查权限名是否已存在
        SwiftAuthority existing = authorityMapper.findByName(authority.getName());
        if (existing != null) {
            throw new DuplicateEntityException("权限名称已存在");
        }

        // 生成ID
        if (authority.getAuthorityId() == null) {
            authority.setAuthorityId(SnowflakeIdGenerator.generateId());
        }

        authorityMapper.insert(authority);
        return authority;
    }

    /**
     * 更新权限信息
     *
     * @param authority 权限信息
     * @return 更新后的权限
     */
    @Override
    public SwiftAuthority updateAuthority(SwiftAuthority authority) {
        SwiftAuthority existing = getAuthorityById(authority.getAuthorityId());

        // 如果修改了权限名，检查新权限名是否已存在
        if (!existing.getName().equals(authority.getName())) {
            SwiftAuthority nameCheck = authorityMapper.findByName(authority.getName());
            if (nameCheck != null) {
                throw new DuplicateEntityException("权限名称已存在");
            }
        }

        existing.setName(authority.getName());
        existing.setDescription(authority.getDescription());
        authorityMapper.update(existing);
        return existing;
    }

    /**
     * 删除权限
     *
     * @param authorityId 权限ID
     */
    @Override
    public void deleteAuthority(Long authorityId) {
        // 检查权限是否存在
        SwiftAuthority authority = getAuthorityById(authorityId);

        // 检查是否有角色关联该权限
        List<SwiftRoleAuthority> roleAuthorities = roleAuthorityMapper.findByAuthority(authorityId);
        if (roleAuthorities != null && !roleAuthorities.isEmpty()) {
            throw new OperationNotAllowedException("无法删除已分配给角色的权限");
        }

        authorityMapper.deleteById(authorityId);
    }

    /**
     * 根据ID获取权限
     *
     * @param authorityId 权限ID
     * @return 权限信息
     */
    @Override
    public SwiftAuthority getAuthorityById(Long authorityId) {
        SwiftAuthority authority = authorityMapper.findById(authorityId);
        if (authority == null) {
            throw new BusinessException("权限不存在");
        }
        return authority;
    }

    /**
     * 根据权限名获取权限
     *
     * @param name 权限名
     * @return 权限信息
     */
    @Override
    public SwiftAuthority getAuthorityByName(String name) {
        SwiftAuthority authority = authorityMapper.findByName(name);
        if (authority == null) {
            throw new BusinessException("权限不存在");
        }
        return authority;
    }

    /**
     * 获取所有权限
     *
     * @return 权限列表
     */
    @Override
    public List<SwiftAuthority> getAllAuthorities() {
        return authorityMapper.findAll();
    }

    /**
     * 根据权限名列表获取权限
     *
     * @param names 权限名列表
     * @return 权限列表
     */
    @Override
    public List<SwiftAuthority> getAuthoritiesByNames(List<String> names) {
        return authorityMapper.findByNameIn(names);
    }

    /**
     * 获取拥有该权限的角色
     *
     * @param authorityId 权限ID
     * @return 角色集合
     */
    @Override
    public Set<SwiftRole> getRolesWithAuthority(Long authorityId) {
        getAuthorityById(authorityId); // 验证权限存在
        List<SwiftRoleAuthority> roleAuthorities = roleAuthorityMapper.findByAuthority(authorityId);
        return roleAuthorities.stream()
                .map(SwiftRoleAuthority::getRole)
                .collect(Collectors.toSet());
    }
}

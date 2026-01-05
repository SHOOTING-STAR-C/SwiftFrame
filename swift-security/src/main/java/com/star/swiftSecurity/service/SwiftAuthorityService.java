package com.star.swiftSecurity.service;

import com.star.swiftCommon.domain.PageResult;
import com.star.swiftSecurity.entity.SwiftAuthority;
import com.star.swiftSecurity.entity.SwiftRole;

import java.util.List;
import java.util.Set;

/**
 * 权限服务接口
 *
 * @author SHOOTING_STAR_C
 */
public interface SwiftAuthorityService {

    /**
     * 创建权限
     *
     * @param authority 权限信息
     * @return 创建的权限
     */
    SwiftAuthority createAuthority(SwiftAuthority authority);

    /**
     * 更新权限信息
     *
     * @param authority 权限信息
     * @return 更新后的权限
     */
    SwiftAuthority updateAuthority(SwiftAuthority authority);

    /**
     * 删除权限
     *
     * @param authorityId 权限ID
     */
    void deleteAuthority(Long authorityId);

    /**
     * 根据ID获取权限
     *
     * @param authorityId 权限ID
     * @return 权限信息
     */
    SwiftAuthority getAuthorityById(Long authorityId);

    /**
     * 根据权限名获取权限
     *
     * @param name 权限名
     * @return 权限信息
     */
    SwiftAuthority getAuthorityByName(String name);

    /**
     * 获取所有权限
     *
     * @return 权限列表
     */
    List<SwiftAuthority> getAllAuthorities();

    /**
     * 分页获取权限
     *
     * @param page 页码
     * @param size 每页大小
     * @return PageResult<SwiftAuthority>
     */
    PageResult<SwiftAuthority> getAuthorityPage(long page, long size);

    /**
     * 根据权限名列表获取权限
     *
     * @param names 权限名列表
     * @return 权限列表
     */
    List<SwiftAuthority> getAuthoritiesByNames(List<String> names);

    /**
     * 获取拥有该权限的角色
     *
     * @param authorityId 权限ID
     * @return 角色集合
     */
    Set<SwiftRole> getRolesWithAuthority(Long authorityId);
}

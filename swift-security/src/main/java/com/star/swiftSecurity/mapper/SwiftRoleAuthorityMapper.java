package com.star.swiftSecurity.mapper;

import com.star.swiftSecurity.entity.SwiftRoleAuthority;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 角色权限关联Mapper接口
 */
@Mapper
public interface SwiftRoleAuthorityMapper {

    /**
     * 查找角色的所有权限关联
     */
    List<SwiftRoleAuthority> findByRole(@Param("roleId") Long roleId);

    /**
     * 查找角色是否拥有特定权限
     */
    @Select("SELECT COUNT(1) FROM swift_role_authorities WHERE role_id = #{roleId} AND authority_id = #{authorityId}")
    boolean existsByRoleAndAuthority(@Param("roleId") Long roleId, @Param("authorityId") Long authorityId);

    /**
     * 删除角色的特定权限
     */
    @Delete("DELETE FROM swift_role_authorities WHERE role_id = #{roleId} AND authority_id = #{authorityId}")
    int deleteByRoleAndAuthority(@Param("roleId") Long roleId, @Param("authorityId") Long authorityId);

    /**
     * 统计角色拥有的权限数量
     */
    @Select("SELECT COUNT(1) FROM swift_role_authorities WHERE role_id = #{roleId}")
    long countByRole(@Param("roleId") Long roleId);

    /**
     * 保存角色权限关联
     */
    @Insert("INSERT INTO swift_role_authorities(role_id, authority_id, granted_at) " +
            "VALUES(#{id.roleId}, #{id.authorityId}, #{grantedAt})")
    int insert(SwiftRoleAuthority roleAuthority);
}

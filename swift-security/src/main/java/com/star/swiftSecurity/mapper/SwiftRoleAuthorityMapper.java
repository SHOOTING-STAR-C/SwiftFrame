package com.star.swiftSecurity.mapper;

import com.star.swiftSecurity.entity.SwiftRoleAuthority;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.UUID;

/**
 * 角色权限关联Mapper接口
 */
@Mapper
public interface SwiftRoleAuthorityMapper {

    /**
     * 查找角色的所有权限关联
     */
    List<SwiftRoleAuthority> findByRole(@Param("roleId") UUID roleId);

    /**
     * 查找角色是否拥有特定权限
     */
    @Select("SELECT COUNT(1) FROM swift_role_authorities WHERE role_id = #{roleId} AND authority_id = #{authorityId}")
    boolean existsByRoleAndAuthority(@Param("roleId") UUID roleId, @Param("authorityId") UUID authorityId);

    /**
     * 删除角色的特定权限
     */
    @Delete("DELETE FROM swift_role_authorities WHERE role_id = #{roleId} AND authority_id = #{authorityId}")
    int deleteByRoleAndAuthority(@Param("roleId") UUID roleId, @Param("authorityId") UUID authorityId);

    /**
     * 统计角色拥有的权限数量
     */
    @Select("SELECT COUNT(1) FROM swift_role_authorities WHERE role_id = #{roleId}")
    long countByRole(@Param("roleId") UUID roleId);

    /**
     * 保存角色权限关联
     */
    @Insert("INSERT INTO swift_role_authorities(role_id, authority_id, granted_at) " +
            "VALUES(#{id.roleId}, #{id.authorityId}, #{grantedAt})")
    int insert(SwiftRoleAuthority roleAuthority);
}

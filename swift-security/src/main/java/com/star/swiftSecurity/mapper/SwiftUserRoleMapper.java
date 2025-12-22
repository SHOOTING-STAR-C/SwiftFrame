package com.star.swiftSecurity.mapper;

import com.star.swiftSecurity.entity.SwiftRole;
import com.star.swiftSecurity.entity.SwiftUserDetails;
import com.star.swiftSecurity.entity.SwiftUserRole;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用户角色关联Mapper接口
 */
@Mapper
public interface SwiftUserRoleMapper {

    /**
     * 查找用户的所有角色关联
     */
    List<SwiftUserRole> findByUser(@Param("userId") String userId);

    /**
     * 查找拥有角色的用户
     */
    List<SwiftUserRole> findByRole(@Param("roleId") Long roleId);

    /**
     * 查找用户的特定角色
     */
    @Select("SELECT COUNT(1) FROM swift_user_roles WHERE user_id = #{userId} AND role_id = #{roleId}")
    boolean existsByUserAndRole(@Param("userId") String userId, @Param("roleId") Long roleId);

    /**
     * 删除用户的特定角色
     */
    @Delete("DELETE FROM swift_user_roles WHERE user_id = #{userId} AND role_id = #{roleId}")
    int deleteByUserAndRole(@Param("userId") String userId, @Param("roleId") Long roleId);

    /**
     * 统计用户拥有的角色数量
     */
    @Select("SELECT COUNT(1) FROM swift_user_roles WHERE user_id = #{userId}")
    long countByUser(@Param("userId") String userId);

    /**
     * 统计拥有该角色的用户数量
     */
    @Select("SELECT COUNT(1) FROM swift_user_roles WHERE role_id = #{roleId}")
    long countByRole(@Param("roleId") Long roleId);

    /**
     * 保存用户角色关联
     */
    @Insert("INSERT INTO swift_user_roles(user_id, role_id, assigned_at, assigned_by) " +
            "VALUES(#{userId.userId}, #{userId.roleId}, #{assignedAt}, #{assignedBy})")
    int insert(SwiftUserRole userRole);
}

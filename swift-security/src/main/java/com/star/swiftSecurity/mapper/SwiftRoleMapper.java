package com.star.swiftSecurity.mapper;

import com.star.swiftSecurity.entity.SwiftRole;
import org.apache.ibatis.annotations.*;

import java.util.Collection;
import java.util.List;

/**
 * 角色Mapper接口
 */
@Mapper
public interface SwiftRoleMapper {

    /**
     * 根据ID查找角色
     */
    @Select("SELECT * FROM swift_role WHERE role_id = #{roleId}")
    SwiftRole findById(@Param("roleId") Long roleId);

    /**
     * 根据角色名查找
     */
    @Select("SELECT * FROM swift_role WHERE name = #{name}")
    SwiftRole findByName(@Param("name") String name);

    /**
     * 根据多个角色名查找
     */
    List<SwiftRole> findByNameIn(@Param("names") Collection<String> names);

    /**
     * 检查角色名是否存在
     */
    @Select("SELECT COUNT(1) FROM swift_role WHERE name = #{name}")
    boolean existsByName(@Param("name") String name);

    /**
     * 保存角色
     */
    @Insert("INSERT INTO swift_role(name, description) VALUES(#{name}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "roleId")
    int insert(SwiftRole role);

    /**
     * 更新角色
     */
    @Update("UPDATE swift_role SET name=#{name}, description=#{description} WHERE role_id=#{roleId}")
    int update(SwiftRole role);

    /**
     * 删除角色
     */
    @Delete("DELETE FROM swift_role WHERE role_id=#{roleId}")
    int deleteById(@Param("roleId") Long roleId);

    /**
     * 查找所有角色
     */
    @Select("SELECT * FROM swift_role")
    List<SwiftRole> findAll();
}

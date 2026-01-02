package com.star.swiftSecurity.mapper.mysql;

import com.star.swiftSecurity.entity.SwiftRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
    SwiftRole findById(@Param("roleId") Long roleId);

    /**
     * 根据角色名查找
     */
    SwiftRole findByName(@Param("name") String name);

    /**
     * 根据多个角色名查找
     */
    List<SwiftRole> findByNameIn(@Param("names") Collection<String> names);

    /**
     * 检查角色名是否存在
     */
    boolean existsByName(@Param("name") String name);

    /**
     * 保存角色
     */
    int insert(SwiftRole role);

    /**
     * 更新角色
     */
    int update(SwiftRole role);

    /**
     * 删除角色
     */
    int deleteById(@Param("roleId") Long roleId);

    /**
     * 查找所有角色
     */
    List<SwiftRole> findAll();
}

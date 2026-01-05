package com.star.swiftSecurity.mapper.mysql;

import com.star.swiftSecurity.entity.SwiftAuthority;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 权限Mapper接口
 */
@Mapper
public interface SwiftAuthorityMapper {

    /**
     * 根据ID查找权限
     */
    SwiftAuthority findById(@Param("authorityId") Long authorityId);

    /**
     * 根据权限名查找
     */
    SwiftAuthority findByName(@Param("name") String name);

    /**
     * 根据多个权限名查找
     */
    List<SwiftAuthority> findByNameIn(@Param("names") Collection<String> names);

    /**
     * 保存权限
     */
    int insert(SwiftAuthority authority);

    /**
     * 更新权限
     */
    int update(SwiftAuthority authority);

    /**
     * 删除权限
     */
    int deleteById(@Param("authorityId") Long authorityId);

    /**
     * 分页查询权限
     */
    List<SwiftAuthority> findPage(@Param("offset") long offset, @Param("size") long size);

    /**
     * 统计权限总数
     */
    int countAll();
}

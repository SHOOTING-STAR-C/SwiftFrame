package com.star.swiftSecurity.mapper;

import com.star.swiftSecurity.entity.SwiftAuthority;
import org.apache.ibatis.annotations.*;

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
    @Select("SELECT * FROM swift_authorities WHERE authority_id = #{authorityId}")
    SwiftAuthority findById(@Param("authorityId") Long authorityId);

    /**
     * 根据权限名查找
     */
    @Select("SELECT * FROM swift_authorities WHERE name = #{name}")
    SwiftAuthority findByName(@Param("name") String name);

    /**
     * 根据多个权限名查找
     */
    List<SwiftAuthority> findByNameIn(@Param("names") Collection<String> names);

    /**
     * 保存权限
     */
    @Insert("INSERT INTO swift_authorities(name, description) VALUES(#{name}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "authorityId")
    int insert(SwiftAuthority authority);

    /**
     * 更新权限
     */
    @Update("UPDATE swift_authorities SET name=#{name}, description=#{description} WHERE authority_id=#{authorityId}")
    int update(SwiftAuthority authority);

    /**
     * 删除权限
     */
    @Delete("DELETE FROM swift_authorities WHERE authority_id=#{authorityId}")
    int deleteById(@Param("authorityId") Long authorityId);

    /**
     * 查找所有权限
     */
    @Select("SELECT * FROM swift_authorities")
    List<SwiftAuthority> findAll();
}

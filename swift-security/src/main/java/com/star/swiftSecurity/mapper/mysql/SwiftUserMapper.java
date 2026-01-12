package com.star.swiftSecurity.mapper.mysql;

import com.star.swiftSecurity.entity.SwiftUserDetails;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
public interface SwiftUserMapper {

    /**
     * 根据ID查找用户
     */
    @Select("SELECT * FROM swift_users WHERE user_id = #{userId}")
    SwiftUserDetails findById(@Param("userId") Long userId);

    /**
     * 根据ID查找用户（包含角色权限）
     */
    SwiftUserDetails findByIdWithAuthorities(@Param("userId") Long userId);
    
    /**
     * 根据ID查找用户密码
     * 用于缓存恢复时只查询密码字段
     */
    @Select("SELECT password FROM swift_users WHERE user_id = #{userId}")
    String findPasswordById(@Param("userId") Long userId);

    /**
     * 根据用户名查找用户
     */
    SwiftUserDetails findByUsername(@Param("username") String username);

    /**
     * 根据用户名查找用户基础信息（不含角色权限）
     */
    SwiftUserDetails findBaseByUsername(@Param("username") String username);

    /**
     * 根据ID查找用户基础信息（不含角色权限）
     */
    SwiftUserDetails findBaseById(@Param("userId") Long userId);

    /**
     * 根据邮箱查找用户
     */
    @Select("SELECT * FROM swift_users WHERE email = #{email}")
    SwiftUserDetails findByEmail(@Param("email") String email);

    /**
     * 根据手机号查找用户
     */
    @Select("SELECT * FROM swift_users WHERE phone = #{phone}")
    SwiftUserDetails findByPhone(@Param("phone") String phone);

    /**
     * 检查用户名是否存在
     */
    @Select("SELECT COUNT(1) FROM swift_users WHERE username = #{username}")
    boolean existsByUsername(@Param("username") String username);

    /**
     * 检查邮箱是否存在
     */
    @Select("SELECT COUNT(1) FROM swift_users WHERE email = #{email}")
    boolean existsByEmail(@Param("email") String email);

    /**
     * 保存用户（新增）
     */
    @Insert("INSERT INTO swift_users(user_id, username, full_name, password, email, phone, enabled, " +
            "account_non_expired, account_non_locked, credentials_non_expired, failed_login_attempts, " +
            "lock_until, password_changed_at, last_login_at, last_login_ip, created_at) " +
            "VALUES(#{userId}, #{username}, #{fullName}, #{password}, #{email}, #{phone}, #{enabled}, " +
            "#{accountNonExpired}, #{accountNonLocked}, #{credentialsNonExpired}, #{failedLoginAttempts}, " +
            "#{lockUntil}, #{passwordChangedAt}, #{lastLoginAt}, #{lastLoginIp}, #{createdAt})")
    int insert(SwiftUserDetails user);

    /**
     * 更新用户（使用动态SQL，只更新非null字段）
     */
    int update(SwiftUserDetails user);

    /**
     * 删除用户
     */
    @Delete("DELETE FROM swift_users WHERE user_id=#{userId}")
    int deleteById(@Param("userId") Long userId);

    /**
     * 查找需要解锁的用户
     */
    @Select("SELECT * FROM swift_users WHERE lock_until IS NOT NULL AND lock_until <= CURRENT_TIMESTAMP")
    List<SwiftUserDetails> findUsersToUnlock();

    /**
     * 查找密码过期的用户
     */
    @Select("SELECT * FROM swift_users WHERE password_changed_at IS NOT NULL AND password_changed_at <= #{expiryDate}")
    List<SwiftUserDetails> findUsersWithExpiredPassword(@Param("expiryDate") LocalDateTime expiryDate);

    /**
     * 分页查询所有用户
     */
    @Select("SELECT * FROM swift_users")
    List<SwiftUserDetails> findAll();

    /**
     * 分页查询用户
     */
    @Select("SELECT * FROM swift_users ORDER BY created_at DESC LIMIT #{offset}, #{size}")
    List<SwiftUserDetails> findPage(@Param("offset") Integer offset, @Param("size") Integer size);

    /**
     * 统计用户总数
     */
    @Select("SELECT COUNT(*) FROM swift_users")
    Integer countAll();
}

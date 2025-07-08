package com.star.swiftSecurity.repository;

import com.star.swiftSecurity.entity.SwiftUserDetails;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 用户dao层
 *
 * @author SHOOTING_STAR_C
 */
@Repository
public interface SwiftUserRepository extends JpaRepository<SwiftUserDetails, UUID> {
    /**
     * 根据用户名查找用户（包含角色）
     * @param username username
     * @return Optional<SwiftUserDetails>
     */
    @EntityGraph(attributePaths = {"userRoles.role.roleAuthorities.authority"})
    Optional<SwiftUserDetails> findByUsername(String username);

    /**
     * 根据邮箱查找用户
     * @param email email
     * @return Optional<SwiftUserDetails>
     */
    Optional<SwiftUserDetails> findByEmail(String email);

    /**
     * 检查用户名是否存在
     * @param username username
     * @return boolean
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     * @param email email
     * @return boolean
     */
    boolean existsByEmail(String email);

    /**
     * 查找需要解锁的用户（锁定时间已过）
     * @return List<SwiftUserDetails>
     */
    @Query("SELECT u FROM SwiftUserDetails u WHERE u.lockUntil IS NOT NULL AND u.lockUntil <= CURRENT_TIMESTAMP")
    List<SwiftUserDetails> findUsersToUnlock();

    /**
     * 查找密码过期的用户
     * @param expiryDate expiryDate
     * @return List<SwiftUserDetails>
     */
    @Query("SELECT u FROM SwiftUserDetails u WHERE u.passwordChangedAt IS NOT NULL " +
           "AND u.passwordChangedAt <= :expiryDate")
    List<SwiftUserDetails> findUsersWithExpiredPassword(@Param("expiryDate") LocalDateTime expiryDate);
}

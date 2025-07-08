package com.star.swiftSecurity.repository;

import com.star.swiftSecurity.entity.SwiftAuthority;
import com.star.swiftSecurity.entity.SwiftRole;
import com.star.swiftSecurity.entity.SwiftRoleAuthority;
import com.star.swiftSecurity.entity.SwiftRoleAuthorityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SwiftRoleAuthorityRepository extends JpaRepository<SwiftRoleAuthority, SwiftRoleAuthorityId> {
    /**
     * 查找角色的所有权限关联
     *
     * @param role role
     * @return List<SwiftRoleAuthority>
     */
    List<SwiftRoleAuthority> findByRole(SwiftRole role);


    /**
     * 查找角色是否拥有特定权限
     *
     * @param role      role
     * @param authority authority
     * @return boolean
     */
    boolean existsByRoleAndAuthority(SwiftRole role, SwiftAuthority authority);

    /**
     * 删除角色的特定权限
     *
     * @param role      role
     * @param authority authority
     */
    @Modifying
    @Query("DELETE FROM SwiftRoleAuthority ra WHERE ra.role = :role AND ra.authority = :authority")
    void deleteByRoleAndAuthority(@Param("role") SwiftRole role, @Param("authority") SwiftAuthority authority);

    /**
     * 统计角色拥有的权限数量
     *
     * @param role role
     * @return long
     */
    long countByRole(SwiftRole role);
}

package com.star.swiftSecurity.repository;

import com.star.swiftSecurity.entity.SwiftRole;
import com.star.swiftSecurity.entity.SwiftUserDetails;
import com.star.swiftSecurity.entity.SwiftUserRole;
import com.star.swiftSecurity.entity.SwiftUserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户角色关联仓库
 *
 * @author SHOOTING_STAR_C
 */
@Repository
public interface SwiftUserRoleRepository extends JpaRepository<SwiftUserRole, SwiftUserRoleId> {
    /**
     * 查找用户的所有角色关联
     *
     * @param user user
     * @return List<SwiftUserRole>
     */
    List<SwiftUserRole> findByUser(SwiftUserDetails user);

    /**
     * 查找拥有角色的有用户
     * @param role role
     * @return List<SwiftUserRole>
     */
    List<SwiftUserRole> findByRole(SwiftRole role);

    /**
     * 查找用户的特定角色
     *
     * @param user user
     * @param role role)
     * @return boolean
     */
    boolean existsByUserAndRole(SwiftUserDetails user, SwiftRole role);

    /**
     * 删除用户的特定角色
     *
     * @param user user
     * @param role role
     */
    @Modifying
    @Query("DELETE FROM SwiftUserRole ur WHERE ur.user = :user AND ur.role = :role")
    void deleteByUserAndRole(@Param("user") SwiftUserDetails user, @Param("role") SwiftRole role);

    /**
     * 统计用户拥有的角色数量
     *
     * @param user user
     * @return long
     */
    long countByUser(SwiftUserDetails user);

    /**
     * 统计用户拥有的角色数量
     *
     * @param role role
     * @return long
     */
    long countByRole(SwiftRole role);
}

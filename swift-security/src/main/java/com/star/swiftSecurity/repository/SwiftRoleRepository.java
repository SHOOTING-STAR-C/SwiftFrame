package com.star.swiftSecurity.repository;

import com.star.swiftSecurity.entity.SwiftRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 权限dao
 *
 * @author SHOOTING_STAR_C
 */
@Repository
public interface SwiftRoleRepository extends JpaRepository<SwiftRole, Long> {

    /**
     * 根据角色名查找
     *
     * @param name name
     * @return Optional<SwiftRole>
     */
    Optional<SwiftRole> findByName(String name);

    /**
     * 根据多个角色名查找
     *
     * @param names names
     * @return List<SwiftRole>
     */
    List<SwiftRole> findByNameIn(Collection<String> names);

    /**
     * 检查角色名是否存在
     *
     * @param name name
     * @return boolean
     */
    boolean existsByName(String name);

}

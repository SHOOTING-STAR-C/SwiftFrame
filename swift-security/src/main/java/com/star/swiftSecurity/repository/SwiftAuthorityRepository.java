package com.star.swiftSecurity.repository;

import com.star.swiftSecurity.entity.SwiftAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 权限查询接口
 *
 * @author SHOOTING_STAR_C
 */
@Repository
public interface SwiftAuthorityRepository extends JpaRepository<SwiftAuthority, Long> {
    /**
     * 根据权限名查找
     *
     * @param name name
     * @return Optional<SwiftAuthority>
     */
    Optional<SwiftAuthority> findByName(String name);

    /**
     * 根据多个权限名查找
     *
     * @param names names
     * @return List<SwiftAuthority>
     */
    List<SwiftAuthority> findByNameIn(Collection<String> names);
}

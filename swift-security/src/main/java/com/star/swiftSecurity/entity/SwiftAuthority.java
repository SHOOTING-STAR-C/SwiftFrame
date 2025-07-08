package com.star.swiftSecurity.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * 权限实体
 *
 * @author SHOOTING_STAR_C
 */
@Data
@Entity
@Table(name = "swift_authorities")
public class SwiftAuthority {
    @Id
    @Column(name = "authority_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long authorityId;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @OneToMany(mappedBy = "authority", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SwiftRoleAuthority> roleAuthorities = new HashSet<>();
}

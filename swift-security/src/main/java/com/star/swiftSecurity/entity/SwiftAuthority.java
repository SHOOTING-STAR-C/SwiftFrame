package com.star.swiftSecurity.entity;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * 权限实体
 *
 * @author SHOOTING_STAR_C
 */
import java.util.UUID;

/**
 * 权限实体
 *
 * @author SHOOTING_STAR_C
 */
@Data
public class SwiftAuthority {
    private UUID authorityId;

    private String name;

    private String description;

    private Set<SwiftRoleAuthority> roleAuthorities = new HashSet<>();
}

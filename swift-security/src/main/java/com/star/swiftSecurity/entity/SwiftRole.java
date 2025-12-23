package com.star.swiftSecurity.entity;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * 角色实体
 *
 * @author SHOOTING_STAR_C
 */
import java.util.UUID;

/**
 * 角色实体
 *
 * @author SHOOTING_STAR_C
 */
@Data
public class SwiftRole {
    private UUID roleId;

    private String name;

    private String description;

    private Set<SwiftUserRole> userRoles = new HashSet<>();

    private Set<SwiftRoleAuthority> roleAuthorities = new HashSet<>();
}

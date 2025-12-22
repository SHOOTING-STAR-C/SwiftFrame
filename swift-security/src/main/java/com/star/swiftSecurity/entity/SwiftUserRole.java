package com.star.swiftSecurity.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户角色关系表
 *
 * @author SHOOTING_STAR_C
 */
@Data
public class SwiftUserRole {

    private SwiftUserRoleId userId;

    private SwiftUserDetails user;

    private SwiftRole role;

    private LocalDateTime assignedAt;

    private String assignedBy; // 分配人用户名
}

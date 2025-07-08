package com.star.swiftSecurity.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 用户角色关系表
 *
 * @author SHOOTING_STAR_C
 */
@Entity
@Data
@Table(name = "swift_user_roles")
public class SwiftUserRole {

    @EmbeddedId
    private SwiftUserRoleId userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private SwiftUserDetails user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private SwiftRole role;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime assignedAt;

    private String assignedBy; // 分配人用户名
}

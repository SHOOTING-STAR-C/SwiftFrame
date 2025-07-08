package com.star.swiftSecurity.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "swift_role_authorities")
public class SwiftRoleAuthority {

    @EmbeddedId
    private SwiftRoleAuthorityId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private SwiftRole role;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authority_id", nullable = false)
    private SwiftAuthority authority;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime grantedAt;
}

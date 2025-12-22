package com.star.swiftSecurity.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SwiftRoleAuthority {

    private SwiftRoleAuthorityId id;

    private SwiftRole role;


    private SwiftAuthority authority;

    private LocalDateTime grantedAt;
}

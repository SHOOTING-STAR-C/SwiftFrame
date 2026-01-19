package com.star.swiftSecurity.entity;

import lombok.Data;

@Data
public class SwiftRoleAuthority {

    private SwiftRoleAuthorityId id;

    private SwiftRole role;

    private SwiftAuthority authority;
}

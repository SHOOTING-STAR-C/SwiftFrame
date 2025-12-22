package com.star.swiftSecurity.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 角色权限复合主键类
 *
 * @author SHOOTING_STAR_C
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SwiftRoleAuthorityId implements Serializable {
    private Long roleId;
    private Long authorityId;
}

package com.star.swiftSecurity.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.UUID;

/**
 * 复合主键类
 *
 * @author SHOOTING_STAR_C
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SwiftUserRoleId implements Serializable {
    private UUID userId;
    private UUID roleId;
}

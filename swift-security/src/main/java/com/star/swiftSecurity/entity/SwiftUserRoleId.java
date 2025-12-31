package com.star.swiftSecurity.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 复合主键类
 *
 * @author SHOOTING_STAR_C
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SwiftUserRoleId implements Serializable {
    private Long userId;
    private Long roleId;
}

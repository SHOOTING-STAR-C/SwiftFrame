package com.star.swiftSecurity.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * token实体
 *
 * @author SHOOTING_STAR_C
 */
@Data
@AllArgsConstructor
public class JwtToken {
    private String accessToken;
    private String refreshToken;
}

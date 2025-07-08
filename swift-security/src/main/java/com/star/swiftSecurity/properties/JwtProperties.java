package com.star.swiftSecurity.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * jwt配置类
 *
 * @author SHOOTING_STAR_C
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.security.jwt")
public class JwtProperties {
    private String secret; //32位密钥
    private long expiration; //有效期
    private long refreshExpiration; //刷新有效期
    private String issuer;

}

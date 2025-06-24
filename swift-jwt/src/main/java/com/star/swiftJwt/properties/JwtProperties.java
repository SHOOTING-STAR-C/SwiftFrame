package com.star.swiftJwt.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * jwt配置类
 *
 * @author SHOOTING_STAR_C
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret; //32位密钥
    private long expiration; //有效期
    private long refreshExpiration; //刷新有效期
}

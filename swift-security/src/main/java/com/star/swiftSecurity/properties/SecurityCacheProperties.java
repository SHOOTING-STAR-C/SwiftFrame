package com.star.swiftSecurity.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 安全模块缓存配置
 *
 * @author SHOOTING_STAR_C
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.security.cache")
public class SecurityCacheProperties {
    
    /**
     * 是否启用用户缓存
     */
    private boolean enabled = true;
    
    /**
     * 用户信息缓存过期时间（分钟）
     */
    private long userCacheExpirationMinutes = 30;
}

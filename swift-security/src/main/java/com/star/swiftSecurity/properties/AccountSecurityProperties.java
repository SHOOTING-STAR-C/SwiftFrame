package com.star.swiftSecurity.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 安全登录参数
 *
 * @author SHOOTING_STAR_C
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.security.account")
public class AccountSecurityProperties {
    /**
     * 最大登录错误次数
     */
    private int maxLoginAttempts = 5;
    /**
     * 账户锁定时间（分钟）
     */
    private int lockDurationMinutes = 30;
    /**
     * 密码过期时间（天）
     */
    private int passwordExpiryDays = 90;
}

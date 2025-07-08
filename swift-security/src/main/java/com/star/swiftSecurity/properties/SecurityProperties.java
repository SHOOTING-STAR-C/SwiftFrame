package com.star.swiftSecurity.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * 安全相关配置
 *
 * @author SHOOTING_STAR_C
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "security.auth")
public class SecurityProperties {

    private String whiteList;

    /**
     * 转换为数组
     *
     * @return String[]
     */
    public String[] getWhitelistArray() {
        return whiteList.split(",");
    }
}

package com.star.swiftSecurity.config;

import com.star.swiftSecurity.properties.SecurityProperties;
import jakarta.servlet.ServletContext;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * SecurityProperties配置类
 *
 * @author SHOOTING_STAR_C
 */
@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityPropertiesConfig {

    public SecurityPropertiesConfig(SecurityProperties securityProperties, ServletContext servletContext) {
        securityProperties.setServletContext(servletContext);
    }
}

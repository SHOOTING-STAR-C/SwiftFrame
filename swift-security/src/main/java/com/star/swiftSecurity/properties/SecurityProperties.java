package com.star.swiftSecurity.properties;

import jakarta.servlet.ServletContext;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;


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
    
    private ServletContext servletContext;

    /**
     * 设置ServletContext
     *
     * @param servletContext ServletContext
     */
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * 转换为数组
     *
     * @return String[]
     */
    public String[] getWhitelistArray() {
        if (whiteList == null || whiteList.isEmpty()) {
            return new String[0];
        }
        
        return Arrays.stream(whiteList.split(","))
                .map(String::trim)
                .toArray(String[]::new);
    }
}

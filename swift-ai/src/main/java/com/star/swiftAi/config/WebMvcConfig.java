package com.star.swiftAi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvc异步配置
 * 用于支持SSE等异步请求的权限上下文传递
 *
 * @author SHOOTING_STAR_C
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        // 设置默认超时时间
        configurer.setDefaultTimeout(5 * 60 * 1000L);
    }
}

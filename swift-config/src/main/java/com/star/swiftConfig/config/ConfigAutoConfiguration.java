package com.star.swiftConfig.config;

import com.star.swiftConfig.service.SysConfigService;
import com.star.swiftDatasource.config.PostgreSqlDataSourceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;

/**
 * 配置模块自动配置类
 * 负责扫描配置相关的组件
 * 在数据源模块加载完成后加载
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@AutoConfiguration
@AutoConfigureAfter(PostgreSqlDataSourceConfig.class)
@ConditionalOnClass(SysConfigService.class)
@ComponentScan(basePackages = "com.star.swiftConfig")
public class ConfigAutoConfiguration {
    
    public ConfigAutoConfiguration() {
        log.info("配置模块自动配置已加载");
    }
}

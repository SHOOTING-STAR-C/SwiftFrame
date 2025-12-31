package com.star.swiftMonitor.config;

import com.star.swiftMonitor.health.SwiftSystemHealthIndicator;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SwiftFrame 监控模块自动配置
 *
 * @author SwiftFrame
 * @since 1.0
 */
@Configuration
@ConditionalOnProperty(prefix = "swift.monitor", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MonitorAutoConfiguration {

    /**
     * 配置系统健康检查指示器
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(SwiftSystemHealthIndicator.class)
    public SwiftSystemHealthIndicator swiftSystemHealthIndicator() {
        return new SwiftSystemHealthIndicator();
    }

    /**
     * 配置 Micrometer 指标注册器
     * 为所有指标添加应用名称标签
     */
    @Bean
    @ConditionalOnMissingBean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config().commonTags(
                "application", System.getProperty("spring.application.name", "swift")
        );
    }
}

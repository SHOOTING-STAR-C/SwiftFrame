package com.star.swiftConfig.init;

import com.star.swiftConfig.service.SysConfigService;
import com.star.swiftredis.service.ConfigCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 系统配置初始化器
 * 在应用启动时自动将数据库中的配置加载到Redis缓存中
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Order(2) // 在SecurityDataInitializer之后执行
public class ConfigInitializer implements CommandLineRunner {

    private final SysConfigService sysConfigService;
    private final ConfigCacheService configCacheService;

    @Override
    public void run(String... args) {
        log.info("开始初始化系统配置到Redis缓存...");

        try {
            // 获取所有启用的配置
            var configMap = sysConfigService.getAllEnabledConfigMap();

            if (configMap.isEmpty()) {
                log.info("数据库中没有启用的配置，跳过缓存加载");
                return;
            }

            // 批量缓存配置到Redis
            configCacheService.batchCacheConfigs(configMap);

            log.info("系统配置初始化完成，共加载{}个配置到Redis缓存", configMap.size());

            // 打印配置类型统计
            var configs = sysConfigService.getAllEnabledConfigs();
            var typeCount = configs.stream()
                    .collect(java.util.stream.Collectors.groupingBy(
                            com.star.swiftConfig.domain.SysConfig::getConfigType,
                            java.util.stream.Collectors.counting()
                    ));
            log.info("配置类型统计: {}", typeCount);

        } catch (Exception e) {
            log.error("系统配置初始化失败", e);
            // 不抛出异常，避免影响应用启动
        }
    }
}

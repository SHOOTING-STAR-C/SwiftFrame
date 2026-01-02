package com.star.swiftConfig.init;

import com.star.swiftConfig.service.SysConfigService;
import com.star.swiftredis.service.ConfigCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 系统配置初始化器
 * 在应用启动时自动将数据库中的配置加载到Redis缓存中
 * 使用分布式锁确保多实例部署时只有一个实例执行初始化
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
    private final RedisTemplate<String, String> redisTemplate;

    // 分布式锁的键名
    private static final String INIT_LOCK_KEY = "config:init:lock";
    // 分布式锁的过期时间（秒）
    private static final long LOCK_EXPIRE_TIME = 30;

    @Override
    public void run(String... args) {
        log.info("开始初始化系统配置到Redis缓存...");

        // 尝试获取分布式锁
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(
                INIT_LOCK_KEY, 
                "1", 
                LOCK_EXPIRE_TIME, 
                TimeUnit.SECONDS
        );

        if (Boolean.TRUE.equals(locked)) {
            // 成功获取锁，执行初始化
            try {
                log.info("成功获取分布式锁，开始执行配置初始化");
                doInitialize();
            } finally {
                // 释放锁
                redisTemplate.delete(INIT_LOCK_KEY);
                log.info("释放分布式锁");
            }
        } else {
            // 未获取到锁，说明其他实例正在初始化
            log.info("其他实例正在初始化配置，跳过本次初始化");
            log.info("等待其他实例完成初始化...");
            
            // 等待一段时间，确保其他实例完成初始化
            try {
                TimeUnit.SECONDS.sleep(5);
                log.info("等待完成，配置应该已由其他实例加载到Redis");
            } catch (InterruptedException e) {
                log.warn("等待初始化完成时被中断", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 执行配置初始化的实际逻辑
     */
    private void doInitialize() {
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

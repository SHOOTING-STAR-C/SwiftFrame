package com.star.swiftredis.service.impl;

import com.star.swiftCommon.properties.CommonProperties;
import com.star.swiftredis.service.ConfigCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 配置缓存服务实现类
 * 用于将系统配置缓存到Redis中
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigCacheServiceImpl implements ConfigCacheService {

    private final RedisTemplate<String, String> redisTemplate;
    private final CommonProperties commonProperties;

    // 配置缓存的前缀
    private static final String CONFIG_CACHE_PREFIX = "config:";

    // 配置缓存的默认过期时间（24小时）
    private static final long DEFAULT_CACHE_EXPIRATION = 24 * 60 * 60;

    /**
     * 获取配置缓存的完整键名
     *
     * @param configKey 配置键
     * @return 完整键名
     */
    private String getFullKey(String configKey) {
        return commonProperties.getName() + ":" + CONFIG_CACHE_PREFIX + configKey;
    }

    @Override
    public void cacheConfig(String configKey, String configValue) {
        String fullKey = getFullKey(configKey);
        redisTemplate.opsForValue().set(fullKey, configValue, DEFAULT_CACHE_EXPIRATION, TimeUnit.SECONDS);
        log.debug("缓存配置到Redis: {}", configKey);
    }

    @Override
    public String getCachedConfig(String configKey) {
        String fullKey = getFullKey(configKey);
        String value = redisTemplate.opsForValue().get(fullKey);
        log.debug("从Redis获取配置: {}, 结果: {}", configKey, value != null ? "存在" : "不存在");
        return value;
    }

    @Override
    public void removeCachedConfig(String configKey) {
        String fullKey = getFullKey(configKey);
        redisTemplate.delete(fullKey);
        log.debug("从Redis删除配置: {}", configKey);
    }

    @Override
    public void clearAllConfigs() {
        String pattern = commonProperties.getName() + ":" + CONFIG_CACHE_PREFIX + "*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("清空所有配置缓存，共删除{}个配置", keys.size());
        }
    }

    @Override
    public boolean hasCachedConfig(String configKey) {
        String fullKey = getFullKey(configKey);
        Boolean hasKey = redisTemplate.hasKey(fullKey);
        return hasKey != null && hasKey;
    }

    @Override
    public void batchCacheConfigs(Map<String, String> configMap) {
        if (configMap == null || configMap.isEmpty()) {
            return;
        }

        // 批量设置配置
        configMap.forEach((configKey, configValue) -> {
            String fullKey = getFullKey(configKey);
            redisTemplate.opsForValue().set(fullKey, configValue, DEFAULT_CACHE_EXPIRATION, TimeUnit.SECONDS);
        });

        log.info("批量缓存配置到Redis，共{}个配置", configMap.size());
    }
}

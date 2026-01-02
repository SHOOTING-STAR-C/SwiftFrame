package com.star.swiftredis.service;

/**
 * 配置缓存服务接口
 * 用于将系统配置缓存到Redis中
 *
 * @author SHOOTING_STAR_C
 */
public interface ConfigCacheService {

    /**
     * 缓存配置到Redis
     *
     * @param configKey   配置键
     * @param configValue 配置值
     */
    void cacheConfig(String configKey, String configValue);

    /**
     * 从Redis获取配置
     *
     * @param configKey 配置键
     * @return 配置值
     */
    String getCachedConfig(String configKey);

    /**
     * 从Redis删除配置
     *
     * @param configKey 配置键
     */
    void removeCachedConfig(String configKey);

    /**
     * 清空所有配置缓存
     */
    void clearAllConfigs();

    /**
     * 检查配置是否在缓存中
     *
     * @param configKey 配置键
     * @return 是否存在
     */
    boolean hasCachedConfig(String configKey);

    /**
     * 批量缓存配置
     *
     * @param configMap 配置键值对Map
     */
    void batchCacheConfigs(java.util.Map<String, String> configMap);
}

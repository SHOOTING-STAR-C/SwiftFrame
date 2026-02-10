package com.star.swiftAi.core.factory;

import com.star.swiftAi.core.model.ProviderMetaData;
import com.star.swiftAi.core.provider.AbstractProvider;
import com.star.swiftAi.core.provider.Provider;
import com.star.swiftAi.core.registry.ProviderRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 提供商工厂
 * 用于创建Provider实例（工厂+单例模式）
 * 
 * 工厂内部维护实例缓存池，相同配置的Provider实例会被复用
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
public class ProviderFactory {
    
    /**
     * Provider实例缓存池（单例管理）
     * Key: providerTypeName + configHash
     * Value: Provider实例
     */
    private static final Map<String, Provider> PROVIDER_INSTANCE_POOL = new ConcurrentHashMap<>();
    
    /**
     * 创建Provider实例（单例模式）
     * 相同配置的Provider实例会被复用
     *
     * @param providerTypeName 提供商类型名称
     * @param providerConfig 提供商配置
     * @param providerSettings 提供商设置
     * @return Provider实例
     * @throws Exception 创建失败时抛出异常
     */
    public static Provider createProvider(
        String providerTypeName,
        Map<String, Object> providerConfig,
        Map<String, Object> providerSettings
    ) throws Exception {
        ProviderMetaData metadata = ProviderRegistry.getProviderMetadata(providerTypeName);
        if (metadata == null) {
            throw new IllegalArgumentException("未知的提供商类型: " + providerTypeName);
        }
        
        Class<?> clazz = metadata.getClsType();
        if (!Provider.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("提供商类型不是Provider: " + providerTypeName);
        }
        
        // 生成缓存键
        String cacheKey = generateCacheKey(providerTypeName, providerConfig, providerSettings);
        
        // 从缓存池获取或创建实例（线程安全）
        Provider provider = PROVIDER_INSTANCE_POOL.computeIfAbsent(cacheKey, key -> {
            try {
                Provider newProvider = (Provider) clazz
                    .getDeclaredConstructor(Map.class, Map.class)
                    .newInstance(providerConfig, providerSettings);
                
                log.info("创建Provider实例成功: {} (缓存键: {})", providerTypeName, cacheKey);
                return newProvider;
            } catch (Exception e) {
                log.error("创建Provider实例失败: {}", providerTypeName, e);
                throw new RuntimeException("创建Provider实例失败: " + e.getMessage(), e);
            }
        });
        
        return provider;
    }
    
    /**
     * 获取所有已注册的提供商类型
     *
     * @return 提供商元数据列表
     */
    public static List<ProviderMetaData> getRegisteredProviders() {
        return ProviderRegistry.getProviderRegistry();
    }
    
    /**
     * 检查提供商类型是否已注册
     *
     * @param providerTypeName 提供商类型名称
     * @return 是否已注册
     */
    public static boolean isProviderRegistered(String providerTypeName) {
        return ProviderRegistry.isRegistered(providerTypeName);
    }
    
    /**
     * 生成缓存键
     * 使用providerTypeName + 配置的哈希值作为唯一标识
     *
     * @param providerTypeName 提供商类型名称
     * @param providerConfig 提供商配置
     * @param providerSettings 提供商设置（可为null）
     * @return 缓存键
     */
    private static String generateCacheKey(
        String providerTypeName,
        Map<String, Object> providerConfig,
        Map<String, Object> providerSettings
    ) {
        int configHash = Objects.hash(providerConfig);
        int settingsHash = providerSettings != null ? Objects.hash(providerSettings) : 0;
        return providerTypeName + ":" + configHash + ":" + settingsHash;
    }
    
    /**
     * 清除指定类型的所有Provider实例缓存
     *
     * @param providerTypeName 提供商类型名称
     */
    public static void clearProviderCache(String providerTypeName) {
        PROVIDER_INSTANCE_POOL.entrySet().removeIf(entry -> 
            entry.getKey().startsWith(providerTypeName + ":"));
        
        log.info("清除Provider实例缓存: {}", providerTypeName);
    }
    
    /**
     * 清除所有Provider实例缓存（主要用于测试）
     */
    public static void clearAllCache() {
        int providerCount = PROVIDER_INSTANCE_POOL.size();
        
        PROVIDER_INSTANCE_POOL.clear();
        
        log.info("清除所有Provider实例缓存 - Provider: {}", providerCount);
    }
    
    /**
     * 获取当前缓存的Provider实例数量
     *
     * @return 缓存实例数量
     */
    public static int getCachedProviderCount() {
        return PROVIDER_INSTANCE_POOL.size();
    }
}

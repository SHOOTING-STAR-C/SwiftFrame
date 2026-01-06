package com.star.swiftAi.core.factory;

import com.star.swiftAi.core.model.ProviderMetaData;
import com.star.swiftAi.core.provider.AbstractProvider;
import com.star.swiftAi.core.provider.EmbeddingProvider;
import com.star.swiftAi.core.provider.Provider;
import com.star.swiftAi.core.provider.RerankProvider;
import com.star.swiftAi.core.registry.ProviderRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * 提供商工厂
 * 用于创建Provider实例
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
public class ProviderFactory {
    
    /**
     * 创建Provider实例
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
        
        try {
            // 创建实例
            Provider provider = (Provider) clazz
                .getDeclaredConstructor(Map.class, Map.class)
                .newInstance(providerConfig, providerSettings);
            
            log.info("创建Provider实例成功: {}", providerTypeName);
            return provider;
        } catch (Exception e) {
            log.error("创建Provider实例失败: {}", providerTypeName, e);
            throw e;
        }
    }
    
    /**
     * 创建EmbeddingProvider实例
     *
     * @param providerTypeName 提供商类型名称
     * @param providerConfig 提供商配置
     * @return EmbeddingProvider实例
     * @throws Exception 创建失败时抛出异常
     */
    public static EmbeddingProvider createEmbeddingProvider(
        String providerTypeName,
        Map<String, Object> providerConfig
    ) throws Exception {
        ProviderMetaData metadata = ProviderRegistry.getProviderMetadata(providerTypeName);
        if (metadata == null) {
            throw new IllegalArgumentException("未知的提供商类型: " + providerTypeName);
        }
        
        Class<?> clazz = metadata.getClsType();
        if (!EmbeddingProvider.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("提供商类型不是EmbeddingProvider: " + providerTypeName);
        }
        
        try {
            // 创建实例
            EmbeddingProvider provider = (EmbeddingProvider) clazz
                .getDeclaredConstructor(Map.class)
                .newInstance(providerConfig);
            
            log.info("创建EmbeddingProvider实例成功: {}", providerTypeName);
            return provider;
        } catch (Exception e) {
            log.error("创建EmbeddingProvider实例失败: {}", providerTypeName, e);
            throw e;
        }
    }
    
    /**
     * 创建RerankProvider实例
     *
     * @param providerTypeName 提供商类型名称
     * @param providerConfig 提供商配置
     * @return RerankProvider实例
     * @throws Exception 创建失败时抛出异常
     */
    public static RerankProvider createRerankProvider(
        String providerTypeName,
        Map<String, Object> providerConfig
    ) throws Exception {
        ProviderMetaData metadata = ProviderRegistry.getProviderMetadata(providerTypeName);
        if (metadata == null) {
            throw new IllegalArgumentException("未知的提供商类型: " + providerTypeName);
        }
        
        Class<?> clazz = metadata.getClsType();
        if (!RerankProvider.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("提供商类型不是RerankProvider: " + providerTypeName);
        }
        
        try {
            // 创建实例
            RerankProvider provider = (RerankProvider) clazz
                .getDeclaredConstructor(Map.class)
                .newInstance(providerConfig);
            
            log.info("创建RerankProvider实例成功: {}", providerTypeName);
            return provider;
        } catch (Exception e) {
            log.error("创建RerankProvider实例失败: {}", providerTypeName, e);
            throw e;
        }
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
}

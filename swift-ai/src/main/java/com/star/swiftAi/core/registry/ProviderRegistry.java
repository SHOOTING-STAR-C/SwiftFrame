package com.star.swiftAi.core.registry;

import com.star.swiftAi.core.model.ProviderMetaData;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 提供商注册表
 * 维护了通过装饰器注册的Provider
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
public class ProviderRegistry {
    
    /**
     * 维护了通过装饰器注册的Provider
     */
    private static final List<ProviderMetaData> PROVIDER_REGISTRY = new ArrayList<>();
    
    /**
     * 维护了Provider类型名称和ProviderMetadata的映射
     */
    private static final Map<String, ProviderMetaData> PROVIDER_CLS_MAP = new ConcurrentHashMap<>();
    
    /**
     * 注册提供商适配器
     *
     * @param providerTypeName 提供商类型名称
     * @param desc 描述
     * @param defaultConfigTmpl 默认配置模板
     * @param providerDisplayName 提供商显示名称
     * @param clsType 类类型
     */
    public static void registerProviderAdapter(
        String providerTypeName,
        String desc,
        Map<String, Object> defaultConfigTmpl,
        String providerDisplayName,
        Class<?> clsType
    ) {
        ProviderMetaData metadata = new ProviderMetaData();
        metadata.setId(providerTypeName);
        metadata.setType(providerTypeName);
        metadata.setDesc(desc);
        metadata.setDefaultConfigTmpl(defaultConfigTmpl);
        metadata.setProviderDisplayName(providerDisplayName);
        metadata.setClsType(clsType);
        
        PROVIDER_REGISTRY.add(metadata);
        PROVIDER_CLS_MAP.put(providerTypeName, metadata);
        
        log.info("注册提供商适配器: {} ({})", providerDisplayName, providerTypeName);
    }
    
    /**
     * 获取所有已注册的提供商
     *
     * @return 提供商列表
     */
    public static List<ProviderMetaData> getProviderRegistry() {
        return new ArrayList<>(PROVIDER_REGISTRY);
    }
    
    /**
     * 根据类型名称获取提供商元数据
     *
     * @param typeName 类型名称
     * @return 提供商元数据
     */
    public static ProviderMetaData getProviderMetadata(String typeName) {
        return PROVIDER_CLS_MAP.get(typeName);
    }
    
    /**
     * 检查提供商类型是否已注册
     *
     * @param typeName 类型名称
     * @return 是否已注册
     */
    public static boolean isRegistered(String typeName) {
        return PROVIDER_CLS_MAP.containsKey(typeName);
    }
    
    /**
     * 清空注册表（主要用于测试）
     */
    public static void clear() {
        PROVIDER_REGISTRY.clear();
        PROVIDER_CLS_MAP.clear();
    }
}

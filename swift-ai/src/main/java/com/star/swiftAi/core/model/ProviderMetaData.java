package com.star.swiftAi.core.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 提供商适配器的注册元数据
 *
 * @author SHOOTING_STAR_C
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProviderMetaData extends ProviderMeta {
    /**
     * 简短描述
     */
    private String desc = "";
    
    /**
     * 类类型
     */
    private Class<?> clsType;
    
    /**
     * 默认配置模板
     */
    private Map<String, Object> defaultConfigTmpl;
    
    /**
     * WebUI 显示名称
     */
    private String providerDisplayName;
}

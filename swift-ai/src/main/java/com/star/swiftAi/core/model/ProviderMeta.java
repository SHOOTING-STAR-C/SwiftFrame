package com.star.swiftAi.core.model;

import com.star.swiftAi.enums.ProviderType;
import lombok.Data;

/**
 * 提供商实例的基本元数据
 *
 * @author SHOOTING_STAR_C
 */
@Data
public class ProviderMeta {
    /**
     * 唯一标识符
     */
    private String id;
    
    /**
     * 当前使用的模型名称
     */
    private String model;
    
    /**
     * 提供商适配器类型（如 openai, ollama）
     */
    private String type;
    
    /**
     * 能力类型
     */
    private ProviderType providerType;
}

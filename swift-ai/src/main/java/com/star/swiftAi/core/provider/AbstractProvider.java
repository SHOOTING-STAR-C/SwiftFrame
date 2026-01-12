package com.star.swiftAi.core.provider;

import com.star.swiftAi.core.model.ProviderMeta;

import java.util.Map;

/**
 * 所有提供商的抽象基类
 *
 * @author SHOOTING_STAR_C
 */
public abstract class AbstractProvider {
    
    /**
     * 提供商配置
     */
    protected Map<String, Object> providerConfig;
    
    /**
     * 提供商设置
     */
    protected Map<String, Object> providerSettings;
    
    /**
     * 模型名称
     */
    protected String modelName = "";
    
    public AbstractProvider(Map<String, Object> providerConfig) {
        this.providerConfig = providerConfig;
    }
    
    public AbstractProvider(Map<String, Object> providerConfig, Map<String, Object> providerSettings) {
        this.providerConfig = providerConfig;
        this.providerSettings = providerSettings;
    }
    
    /**
     * 设置当前模型名称
     *
     * @param modelName 模型名称
     */
    public void setModel(String modelName) {
        this.modelName = modelName;
    }
    
    /**
     * 获取当前模型名称
     *
     * @return 模型名称
     */
    public String getModel() {
        return this.modelName;
    }
    
    /**
     * 获取提供商元数据
     *
     * @return 提供商元数据
     */
    public abstract ProviderMeta meta();
    
    /**
     * 测试提供商可用性
     *
     * @param model 模型名称（可选，如果为空则使用供应商第一个可用模型）
     * @throws Exception 测试失败时抛出异常
     */
    public abstract void test(String model) throws Exception;
}

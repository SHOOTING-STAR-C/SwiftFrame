package com.star.swiftAi.enums;

import lombok.Getter;

/**
 * AI 模型枚举
 * 包含主流 AI 服务提供商的模型
 */
@Getter
public enum AiModel {
    
    // OpenAI 模型
    GPT_4O("gpt-4o", "GPT-4o", AiProvider.OPENAI),
    GPT_4O_MINI("gpt-4o-mini", "GPT-4o Mini", AiProvider.OPENAI),
    GPT_4("gpt-4", "GPT-4", AiProvider.OPENAI),
    GPT_4_TURBO("gpt-4-turbo", "GPT-4 Turbo", AiProvider.OPENAI),
    GPT_3_5_TURBO("gpt-3.5-turbo", "GPT-3.5 Turbo", AiProvider.OPENAI),
    
    // DeepSeek 模型
    DEEPSEEK_CHAT("deepseek-chat", "DeepSeek Chat", AiProvider.DEEPSEEK),
    DEEPSEEK_CODER("deepseek-coder", "DeepSeek Coder", AiProvider.DEEPSEEK),
    DEEPSEEK_V3("deepseek-v3", "DeepSeek V3", AiProvider.DEEPSEEK),
    DEEPSEEK_R1("deepseek-r1", "DeepSeek R1", AiProvider.DEEPSEEK),
    
    // 自定义模型
    CUSTOM("custom", "Custom Model", AiProvider.CUSTOM);
    
    private final String code;
    private final String name;
    private final AiProvider provider;
    
    AiModel(String code, String name, AiProvider provider) {
        this.code = code;
        this.name = name;
        this.provider = provider;
    }
    
    public static AiModel fromCode(String code) {
        if (code == null || code.isEmpty()) {
            return CUSTOM;
        }
        
        for (AiModel model : values()) {
            if (model.getCode().equalsIgnoreCase(code)) {
                return model;
            }
        }
        
        // 如果找不到对应模型，返回自定义模型
        return CUSTOM;
    }
    
    public static AiModel fromCodeAndProvider(String code, AiProvider provider) {
        if (code == null || code.isEmpty() || provider == null) {
            return CUSTOM;
        }
        
        for (AiModel model : values()) {
            if (model.getCode().equalsIgnoreCase(code) 
                && model.getProvider() == provider) {
                return model;
            }
        }
        
        return CUSTOM;
    }
}

package com.star.swiftAi.enums;

import lombok.Getter;

/**
 * AI 服务提供商枚举
 * 支持 OpenAI 兼容格式的服务商
 */
@Getter
public enum AiProvider {
    
    OPENAI("openai", "OpenAI", "https://api.openai.com"),
    DEEPSEEK("deepseek", "DeepSeek", "https://api.deepseek.com"),
    CUSTOM("custom", "Custom", "");
    
    private final String code;
    private final String name;
    private final String defaultBaseUrl;
    
    AiProvider(String code, String name, String defaultBaseUrl) {
        this.code = code;
        this.name = name;
        this.defaultBaseUrl = defaultBaseUrl;
    }
    
    public static AiProvider fromCode(String code) {
        for (AiProvider provider : values()) {
            if (provider.getCode().equalsIgnoreCase(code)) {
                return provider;
            }
        }
        return CUSTOM;
    }
}

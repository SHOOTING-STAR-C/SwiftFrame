package com.star.swiftAi.enums;

/**
 * 提供商类型枚举
 *
 * @author SHOOTING_STAR_C
 */
public enum ProviderType {
    /**
     * 聊天完成
     */
    CHAT_COMPLETION("chat_completion"),
    
    /**
     * 向量嵌入
     */
    EMBEDDING("embedding"),
    
    /**
     * 重排序
     */
    RERANK("rerank");
    
    private final String value;
    
    ProviderType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static ProviderType fromValue(String value) {
        for (ProviderType type : ProviderType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown provider type: " + value);
    }
}

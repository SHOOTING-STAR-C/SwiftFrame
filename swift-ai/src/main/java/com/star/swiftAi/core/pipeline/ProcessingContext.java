package com.star.swiftAi.core.pipeline;

import com.star.swiftAi.core.model.MessageChain;
import com.star.swiftAi.core.model.ModelConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 处理上下文
 * 在处理器之间传递数据和状态
 *
 * @author SHOOTING_STAR_C
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingContext {
    
    /**
     * chain-based消息管理模型
     */
    private MessageChain messageChain;
    
    /**
     * 对话ID
     */
    private String conversationId;
    
    /**
     * 模型配置
     */
    private ModelConfig modelConfig;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 请求ID（用于追踪）
     */
    private String requestId;
    
    /**
     * 处理器间共享的数据
     */
    @Builder.Default
    private Map<String, Object> sharedData = new HashMap<>();
    
    /**
     * 处理开始时间
     */
    private Long startTime;
    
    /**
     * 是否跳过后续处理器
     */
    private boolean skipRemaining;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 添加共享数据
     *
     * @param key   键
     * @param value 值
     */
    public void putSharedData(String key, Object value) {
        if (sharedData == null) {
            sharedData = new HashMap<>();
        }
        sharedData.put(key, value);
    }
    
    /**
     * 获取共享数据
     *
     * @param key  键
     * @param type 类型
     * @param <T>  泛型类型
     * @return 值
     */
    @SuppressWarnings("unchecked")
    public <T> T getSharedData(String key, Class<T> type) {
        if (sharedData == null) {
            return null;
        }
        Object value = sharedData.get(key);
        return value != null ? (T) value : null;
    }
    
    /**
     * 判断是否有错误
     *
     * @return 是否有错误
     */
    public boolean hasError() {
        return errorMessage != null && !errorMessage.isEmpty();
    }
}
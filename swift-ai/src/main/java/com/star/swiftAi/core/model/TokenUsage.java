package com.star.swiftAi.core.model;

import lombok.Data;

/**
 * Token使用统计
 *
 * @author SHOOTING_STAR_C
 */
@Data
public class TokenUsage {
    /**
     * 非缓存输入tokens
     */
    private int inputOther = 0;
    
    /**
     * 缓存输入tokens
     */
    private int inputCached = 0;
    
    /**
     * 输出tokens
     */
    private int output = 0;
    
    /**
     * 总token数
     */
    public int getTotal() {
        return inputOther + inputCached + output;
    }
}

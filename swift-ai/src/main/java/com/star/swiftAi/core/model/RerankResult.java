package com.star.swiftAi.core.model;

import lombok.Data;

/**
 * 重排序结果
 *
 * @author SHOOTING_STAR_C
 */
@Data
public class RerankResult {
    /**
     * 在候选列表中的索引位置
     */
    private int index;
    
    /**
     * 相关性分数
     */
    private double relevanceScore;
}

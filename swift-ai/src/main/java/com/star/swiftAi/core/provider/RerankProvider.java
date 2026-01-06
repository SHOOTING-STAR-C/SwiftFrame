package com.star.swiftAi.core.provider;

import com.star.swiftAi.core.model.RerankResult;

import java.util.List;
import java.util.Map;

/**
 * 重排序提供商
 *
 * @author SHOOTING_STAR_C
 */
public abstract class RerankProvider extends AbstractProvider {
    
    public RerankProvider(Map<String, Object> providerConfig, Map<String, Object> providerSettings) {
        super(providerConfig);
    }
    
    /**
     * 获取查询和文档的重排序分数
     *
     * @param query 查询文本
     * @param documents 文档列表
     * @param topN 返回前N个结果
     * @return 重排序结果列表
     * @throws Exception 调用失败时抛出异常
     */
    public abstract List<RerankResult> rerank(
        String query,
        List<String> documents,
        Integer topN
    ) throws Exception;
}

package com.star.swiftAi.core.provider;

import java.util.List;
import java.util.Map;

/**
 * 向量嵌入提供商
 *
 * @author SHOOTING_STAR_C
 */
public abstract class EmbeddingProvider extends AbstractProvider {
    
    public EmbeddingProvider(Map<String, Object> providerConfig, Map<String, Object> providerSettings) {
        super(providerConfig);
    }
    
    /**
     * 获取文本的向量
     *
     * @param text 文本
     * @return 向量
     * @throws Exception 获取失败时抛出异常
     */
    public abstract List<Float> getEmbedding(String text) throws Exception;
    
    /**
     * 批量获取文本的向量
     *
     * @param texts 文本列表
     * @return 向量列表
     * @throws Exception 获取失败时抛出异常
     */
    public abstract List<List<Float>> getEmbeddings(List<String> texts) throws Exception;
    
    /**
     * 获取向量的维度
     *
     * @return 向量维度
     */
    public abstract int getDim();
    
    /**
     * 批量获取文本的向量，分批处理以节省内存
     *
     * @param texts 文本列表
     * @param batchSize 批次大小
     * @param tasksLimit 并发任务限制
     * @param maxRetries 最大重试次数
     * @return 向量列表
     * @throws Exception 获取失败时抛出异常
     */
    public List<List<Float>> getEmbeddingsBatch(
        List<String> texts,
        int batchSize,
        int tasksLimit,
        int maxRetries
    ) throws Exception {
        List<List<Float>> results = new java.util.ArrayList<>();
        
        for (int i = 0; i < texts.size(); i += batchSize) {
            int end = Math.min(i + batchSize, texts.size());
            List<String> batch = texts.subList(i, end);
            
            int retryCount = 0;
            while (retryCount < maxRetries) {
                try {
                    results.addAll(getEmbeddings(batch));
                    break;
                } catch (Exception e) {
                    retryCount++;
                    if (retryCount >= maxRetries) {
                        throw e;
                    }
                    Thread.sleep(1000 * retryCount);
                }
            }
        }
        
        return results;
    }
}

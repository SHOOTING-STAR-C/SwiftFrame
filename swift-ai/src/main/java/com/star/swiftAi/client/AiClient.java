package com.star.swiftAi.client;

import com.star.swiftAi.core.request.ChatRequest;
import com.star.swiftAi.core.response.ChatResponse;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * AI 客户端统一接口
 * 定义与 AI 服务交互的标准方法
 */
public interface AiClient {

    /**
     * 同步对话调用
     *
     * @param request 对话请求
     * @return 对话响应
     */
    ChatResponse chat(ChatRequest request);

    /**
     * 异步流式对话调用
     *
     * @param request 对话请求
     * @param consumer 响应数据消费者（逐条处理流式响应）
     */
    void streamChat(ChatRequest request, Consumer<ChatResponse> consumer);

    /**
     * 异步对话调用（返回 Future）
     *
     * @param request 对话请求
     * @return 异步响应结果
     */
    java.util.concurrent.CompletableFuture<ChatResponse> asyncChat(ChatRequest request);

    /**
     * 检查服务是否可用
     *
     * @return 是否可用
     */
    boolean isAvailable();

    /**
     * 获取客户端配置信息
     *
     * @return 配置信息
     */
    ClientConfig getConfig();

    /**
     * 客户端配置信息
     */
    class ClientConfig {
        private final String provider;
        private final String baseUrl;
        private final String model;
        private final boolean healthy;

        public ClientConfig(String provider, String baseUrl, String model, boolean healthy) {
            this.provider = provider;
            this.baseUrl = baseUrl;
            this.model = model;
            this.healthy = healthy;
        }

        public String getProvider() {
            return provider;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public String getModel() {
            return model;
        }

        public boolean isHealthy() {
            return healthy;
        }
    }
}

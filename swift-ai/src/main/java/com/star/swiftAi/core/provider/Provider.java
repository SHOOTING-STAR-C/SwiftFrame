package com.star.swiftAi.core.provider;

import com.star.swiftAi.core.model.ContentPart;
import com.star.swiftAi.core.model.LLMResponse;
import com.star.swiftAi.core.model.ProviderRequest;
import com.star.swiftAi.core.model.ToolCallsResult;
import com.star.swiftAi.core.model.ToolSet;

import java.util.List;
import java.util.Map;

/**
 * 聊天完成提供商
 *
 * @author SHOOTING_STAR_C
 */
public abstract class Provider extends AbstractProvider {
    
    /**
     * 提供商设置
     */
    protected Map<String, Object> providerSettings;
    
    public Provider(Map<String, Object> providerConfig, Map<String, Object> providerSettings) {
        super(providerConfig);
        this.providerSettings = providerSettings;
    }
    
    /**
     * 获取当前API Key
     *
     * @return 当前API Key
     */
    public abstract String getCurrentKey();
    
    /**
     * 获取所有API Keys
     *
     * @return API Keys列表
     */
    public abstract List<String> getKeys();
    
    /**
     * 设置API Key
     *
     * @param key API Key
     */
    public abstract void setKey(String key);
    
    /**
     * 获取支持的模型列表
     *
     * @return 模型列表
     * @throws Exception 获取失败时抛出异常
     */
    public abstract List<String> getModels() throws Exception;
    
    /**
     * 获得LLM的文本对话结果（非流式）
     *
     * @param prompt 提示词
     * @param sessionId 会话ID（已废弃）
     * @param imageUrls 图片URL列表
     * @param funcTool 可用函数工具
     * @param contexts OpenAI格式上下文
     * @param systemPrompt 系统提示词
     * @param toolCallsResult 工具调用结果
     * @param model 模型名称
     * @param extraUserContentParts 额外内容块
     * @return LLM响应
     * @throws Exception 调用失败时抛出异常
     */
    public abstract LLMResponse textChat(
        String prompt,
        String sessionId,
        List<String> imageUrls,
        ToolSet funcTool,
        List<Map<String, Object>> contexts,
        String systemPrompt,
        List<ToolCallsResult> toolCallsResult,
        String model,
        List<ContentPart> extraUserContentParts
    ) throws Exception;
    
    /**
     * 获得LLM的流式文本对话结果
     *
     * @param prompt 提示词
     * @param sessionId 会话ID（已废弃）
     * @param imageUrls 图片URL列表
     * @param funcTool 可用函数工具
     * @param contexts OpenAI格式上下文
     * @param systemPrompt 系统提示词
     * @param toolCallsResult 工具调用结果
     * @param model 模型名称
     * @return LLM响应流
     * @throws Exception 调用失败时抛出异常
     */
    public abstract List<LLMResponse> textChatStream(
        String prompt,
        String sessionId,
        List<String> imageUrls,
        ToolSet funcTool,
        List<Map<String, Object>> contexts,
        String systemPrompt,
        List<ToolCallsResult> toolCallsResult,
        String model
    ) throws Exception;
    
    /**
     * 对话调用（便捷方法）
     *
     * @param request 请求参数
     * @return LLM响应
     * @throws Exception 调用失败时抛出异常
     */
    public LLMResponse chat(ProviderRequest request) throws Exception {
        return textChat(
            request.getPrompt(),
            request.getSessionId(),
            request.getImageUrls(),
            request.getFuncTool(),
            request.getContexts(),
            request.getSystemPrompt(),
            request.getToolCallsResult(),
            request.getModel(),
            request.getExtraUserContentParts()
        );
    }
    
    /**
     * 流式对话调用（便捷方法）
     *
     * @param request 请求参数
     * @param consumer 响应消费者
     * @throws Exception 调用失败时抛出异常
     */
    public void streamChat(ProviderRequest request, java.util.function.Consumer<LLMResponse> consumer) throws Exception {
        List<LLMResponse> responses = textChatStream(
            request.getPrompt(),
            request.getSessionId(),
            request.getImageUrls(),
            request.getFuncTool(),
            request.getContexts(),
            request.getSystemPrompt(),
            request.getToolCallsResult(),
            request.getModel()
        );
        responses.forEach(consumer);
    }
}

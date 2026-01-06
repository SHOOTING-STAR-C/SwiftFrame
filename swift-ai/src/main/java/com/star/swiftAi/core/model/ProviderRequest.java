package com.star.swiftAi.core.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 提供商请求参数
 *
 * @author SHOOTING_STAR_C
 */
@Data
public class ProviderRequest {
    /**
     * 提示词
     */
    private String prompt;
    
    /**
     * 会话ID（已废弃）
     */
    private String sessionId = "";
    
    /**
     * 图片URL列表
     */
    private List<String> imageUrls = new ArrayList<>();
    
    /**
     * 额外内容块
     */
    private List<ContentPart> extraUserContentParts = new ArrayList<>();
    
    /**
     * 可用函数工具
     */
    private ToolSet funcTool;
    
    /**
     * OpenAI格式上下文
     */
    private List<Map<String, Object>> contexts = new ArrayList<>();
    
    /**
     * 系统提示词
     */
    private String systemPrompt = "";
    
    /**
     * 关联的对话对象
     */
    private Conversation conversation;
    
    /**
     * 工具调用结果
     */
    private List<ToolCallsResult> toolCallsResult;
    
    /**
     * 模型名称
     */
    private String model;
    
    /**
     * 温度参数（控制随机性）
     */
    private Double temperature;
    
    /**
     * 最大生成token数
     */
    private Integer maxTokens;
    
    /**
     * Top P参数（核采样）
     */
    private Double topP;
}

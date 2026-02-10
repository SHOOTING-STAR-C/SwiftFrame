package com.star.swiftAi.core.processor;

import com.star.swiftAi.client.AiClient;
import com.star.swiftAi.core.model.Message;
import com.star.swiftAi.core.model.MessageStatus;
import com.star.swiftAi.core.pipeline.MessageProcessor;
import com.star.swiftAi.core.pipeline.ProcessingContext;
import com.star.swiftAi.core.request.ChatRequest;
import com.star.swiftAi.core.response.ChatResponse;
import com.star.swiftAi.exception.AiException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 消息发送处理器
 * 将消息发送到AI模型并获取响应
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Data
public class MessageSendingProcessor implements MessageProcessor {
    
    /**
     * AI客户端
     */
    private AiClient aiClient;
    
    public MessageSendingProcessor(AiClient aiClient) {
        this.aiClient = aiClient;
    }
    
    @Override
    public ProcessingContext process(ProcessingContext context) throws AiException {
        if (aiClient == null) {
            throw new AiException("AIClient cannot be null");
        }
        
        if (context.getMessageChain() == null || context.getMessageChain().getMessages() == null) {
            throw new AiException("Message chain or messages cannot be null");
        }
        
        List<Message> messages = context.getMessageChain().getMessages();
        
        // 更新所有消息状态为发送中
        for (Message message : messages) {
            message.setStatus(MessageStatus.SENDING);
        }
        
        try {
            log.info("Sending {} messages to AI model", messages.size());
            
            // 构建ChatRequest - 使用链式调用
            ChatRequest request = ChatRequest.builder()
                    .model(context.getModelConfig().getModel())
                    .messages(messages)
                    .temperature(context.getModelConfig().getTemperature())
                    .maxTokens(context.getModelConfig().getMaxTokens())
                    .topP(context.getModelConfig().getTopP())
                    .frequencyPenalty(context.getModelConfig().getFrequencyPenalty())
                    .presencePenalty(context.getModelConfig().getPresencePenalty())
                    .stream(context.getModelConfig().getStream())
                    .build();
            
            // 调用AI客户端发送消息
            ChatResponse chatResponse = aiClient.chat(request);
            
            if (chatResponse == null) {
                throw new AiException("AI client returned null response");
            }
            
            // 从ChatResponse中提取消息
            Message response = extractMessageFromResponse(chatResponse);
            
            if (response == null) {
                throw new AiException("Failed to extract message from response");
            }
            
            // 初始化响应消息
            response.init();
            response.setStatus(MessageStatus.SUCCESS);
            
            // 将响应添加到MessageChain
            context.getMessageChain().addMessage(response);
            
            log.info("Message sent successfully. Response ID: {}", response.getMessageId());
            
            // 将响应放入共享数据
            context.putSharedData("response", response);
            context.putSharedData("chatResponse", chatResponse);
            
            return context;
            
        } catch (Exception e) {
            log.error("Failed to send messages to AI model", e);
            
            // 更新消息状态为失败
            for (Message message : messages) {
                message.setStatus(MessageStatus.FAILED);
            }
            
            throw new AiException("Failed to send messages to AI model: " + e.getMessage(), e);
        }
    }
    
    /**
     * 从ChatResponse中提取Message
     *
     * @param chatResponse Chat响应
     * @return Message对象
     */
    private Message extractMessageFromResponse(ChatResponse chatResponse) {
        if (chatResponse.getChoices() == null || chatResponse.getChoices().isEmpty()) {
            return null;
        }
        
        // 获取第一个选择的消息
        ChatResponse.Choice choice = chatResponse.getChoices().get(0);
        if (choice.getMessage() == null) {
            return null;
        }
        
        return choice.getMessage();
    }
    
    @Override
    public String getName() {
        return "MessageSendingProcessor";
    }
    
    @Override
    public int getPriority() {
        return 100; // 最高优先级，最后执行
    }
}
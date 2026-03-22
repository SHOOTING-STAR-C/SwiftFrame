package com.star.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * WebSocket 服务类
 * 供其他模块调用发送消息
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final WebSocketHandler webSocketHandler;

    /**
     * 发送消息给指定用户
     *
     * @param userId  用户ID
     * @param message 消息内容
     * @return 是否发送成功
     */
    public boolean sendToUser(Long userId, String message) {
        boolean result = webSocketHandler.sendMessageToUser(userId, message);
        if (!result) {
            log.warn("发送消息失败，用户 {} 不在线", userId);
        }
        return result;
    }

    /**
     * 发送消息给指定用户（对象会被转为 JSON）
     *
     * @param userId  用户ID
     * @param message 消息对象
     * @return 是否发送成功
     */
    public boolean sendToUser(Long userId, Object message) {
        String json = toJson(message);
        return sendToUser(userId, json);
    }

    /**
     * 广播消息给所有在线用户
     *
     * @param message 消息内容
     */
    public void broadcast(String message) {
        webSocketHandler.broadcast(message);
    }

    /**
     * 广播消息给所有在线用户（对象会被转为 JSON）
     *
     * @param message 消息对象
     */
    public void broadcast(Object message) {
        String json = toJson(message);
        broadcast(json);
    }

    /**
     * 判断用户是否在线
     *
     * @param userId 用户ID
     * @return 是否在线
     */
    public boolean isOnline(Long userId) {
        return webSocketHandler.isUserOnline(userId);
    }

    /**
     * 获取在线用户数
     *
     * @return 在线用户数
     */
    public int getOnlineCount() {
        return webSocketHandler.getOnlineCount();
    }

    /**
     * 简单对象转 JSON（实际项目中建议使用 Jackson）
     */
    private String toJson(Object obj) {
        // 这里简化处理，实际应该注入 ObjectMapper
        return obj.toString();
    }
}
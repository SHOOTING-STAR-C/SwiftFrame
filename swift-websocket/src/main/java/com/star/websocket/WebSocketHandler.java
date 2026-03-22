package com.star.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 处理器
 * 处理连接、消息、断开等事件
 */
@Slf4j
@Component
public class WebSocketHandler extends TextWebSocketHandler {

    /**
     * 存储所有连接的会话
     * key: sessionId
     */
    private static final ConcurrentHashMap<String, WebSocketSession> SESSION_MAP = new ConcurrentHashMap<>();

    /**
     * 用户ID与sessionId的映射
     * key: userId, value: sessionId
     */
    private static final ConcurrentHashMap<Long, String> USER_SESSION_MAP = new ConcurrentHashMap<>();

    /**
     * 连接建立时触发
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String sessionId = session.getId();
        Long userId = (Long) session.getAttributes().get("userId");

        SESSION_MAP.put(sessionId, session);
        if (userId != null) {
            USER_SESSION_MAP.put(userId, sessionId);
        }

        log.info("WebSocket 连接建立: {}, userId: {}, 当前在线数: {}",
                sessionId, userId, SESSION_MAP.size());
    }

    /**
     * 收到文本消息时触发
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        Long userId = (Long) session.getAttributes().get("userId");
        log.info("收到消息 from userId: {}, sessionId: {}, message: {}",
                userId, session.getId(), payload);

        // 回显消息给客户端（可根据业务修改）
        sendMessage(session.getId(), "服务器收到: " + payload);
    }

    /**
     * 连接关闭时触发
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String sessionId = session.getId();
        Long userId = (Long) session.getAttributes().get("userId");

        SESSION_MAP.remove(sessionId);
        if (userId != null) {
            USER_SESSION_MAP.remove(userId);
        }

        log.info("WebSocket 连接关闭: {}, userId: {}, 当前在线数: {}",
                sessionId, userId, SESSION_MAP.size());
    }

    /**
     * 发生错误时触发
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("WebSocket 传输错误: {}", session.getId(), exception);
    }

    /**
     * 发送消息给指定会话
     *
     * @param sessionId 会话ID
     * @param message   消息内容
     * @return 是否发送成功
     */
    public boolean sendMessage(String sessionId, String message) {
        WebSocketSession session = SESSION_MAP.get(sessionId);
        if (session == null || !session.isOpen()) {
            return false;
        }
        try {
            session.sendMessage(new TextMessage(message));
            return true;
        } catch (IOException e) {
            log.error("发送消息失败: {}", sessionId, e);
            return false;
        }
    }

    /**
     * 发送消息给指定用户
     *
     * @param userId  用户ID
     * @param message 消息内容
     * @return 是否发送成功
     */
    public boolean sendMessageToUser(Long userId, String message) {
        String sessionId = USER_SESSION_MAP.get(userId);
        if (sessionId == null) {
            log.warn("用户 {} 不在线", userId);
            return false;
        }
        return sendMessage(sessionId, message);
    }

    /**
     * 广播消息给所有连接
     *
     * @param message 消息内容
     */
    public void broadcast(String message) {
        SESSION_MAP.forEach((id, session) -> {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (IOException e) {
                    log.error("广播消息失败: {}", id, e);
                }
            }
        });
    }

    /**
     * 获取当前在线连接数
     */
    public int getOnlineCount() {
        return SESSION_MAP.size();
    }

    /**
     * 判断用户是否在线
     */
    public boolean isUserOnline(Long userId) {
        String sessionId = USER_SESSION_MAP.get(userId);
        if (sessionId == null) {
            return false;
        }
        WebSocketSession session = SESSION_MAP.get(sessionId);
        return session != null && session.isOpen();
    }

    /**
     * 根据 sessionId 获取用户ID
     */
    public Long getUserIdBySession(String sessionId) {
        WebSocketSession session = SESSION_MAP.get(sessionId);
        if (session == null) {
            return null;
        }
        return (Long) session.getAttributes().get("userId");
    }
}
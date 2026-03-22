package com.star.business.controller;

import com.star.swiftCommon.domain.PubResult;
import com.star.websocket.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * WebSocket 示例接口
 * 演示如何通过 HTTP 接口触发 WebSocket 消息发送
 */
@Slf4j
@RestController
@RequestMapping("/websocket/demo")
@RequiredArgsConstructor
public class WebSocketDemoController {

    private final WebSocketService webSocketService;

    /**
     * 发送消息给指定用户
     *
     * @param userId  用户ID
     * @param message 消息内容
     */
    @PostMapping("/send-to-user")
    public PubResult<String> sendToUser(
            @RequestParam Long userId,
            @RequestParam String message) {

        log.info("发送消息给用户: {}, 内容: {}", userId, message);
        boolean success = webSocketService.sendToUser(userId, message);

        if (success) {
            return PubResult.success("消息已发送");
        } else {
            return PubResult.error("用户不在线或发送失败");
        }
    }

    /**
     * 广播消息给所有在线用户
     *
     * @param message 消息内容
     */
    @PostMapping("/broadcast")
    public PubResult<String> broadcast(@RequestParam String message) {
        log.info("广播消息: {}", message);
        webSocketService.broadcast(message);
        return PubResult.success("广播已发送");
    }

    /**
     * 检查用户是否在线
     *
     * @param userId 用户ID
     */
    @GetMapping("/is-online")
    public PubResult<Boolean> isOnline(@RequestParam Long userId) {
        boolean online = webSocketService.isOnline(userId);
        return PubResult.success(online);
    }

    /**
     * 获取在线用户数量
     */
    @GetMapping("/online-count")
    public PubResult<Integer> getOnlineCount() {
        int count = webSocketService.getOnlineCount();
        return PubResult.success(count);
    }

    /**
     * 发送 JSON 消息给指定用户（模拟通知）
     *
     * @param userId 用户ID
     * @param title  通知标题
     * @param content 通知内容
     */
    @PostMapping("/send-notification")
    public PubResult<String> sendNotification(
            @RequestParam Long userId,
            @RequestParam String title,
            @RequestParam String content) {

        // 构造 JSON 消息
        String jsonMessage = String.format(
            "{\"type\":\"notification\",\"title\":\"%s\",\"content\":\"%s\",\"timestamp\":%d}",
            title, content, System.currentTimeMillis()
        );

        log.info("发送通知给用户: {}, 标题: {}", userId, title);
        boolean success = webSocketService.sendToUser(userId, jsonMessage);

        if (success) {
            return PubResult.success("通知已发送");
        } else {
            return PubResult.error("用户不在线或发送失败");
        }
    }
}
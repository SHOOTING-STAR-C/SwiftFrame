package com.star.websocket;

import com.star.swiftSecurity.service.JwtValidator;
import com.star.swiftSecurity.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket 握手拦截器
 * 在握手阶段进行 JWT 鉴权（复用 JwtValidator）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketInterceptor implements HandshakeInterceptor {

    private final JwtValidator jwtValidator;
    private final JwtUtil jwtUtil;

    /**
     * 握手前触发
     * 进行 JWT 鉴权校验（复用 JwtValidator）
     *
     * @return true 允许握手，false 拒绝握手
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            log.warn("非 Servlet 请求，拒绝握手");
            return false;
        }

        // 从 URL 参数获取 token
        String token = servletRequest.getServletRequest().getParameter("token");

        // 也可以从 Header 获取（格式：Authorization: Bearer xxx）
        if (token == null || token.isEmpty()) {
            String authHeader = servletRequest.getServletRequest().getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }
        }

        // 复用 JwtValidator 进行鉴权
        JwtValidator.ValidationResult result = jwtValidator.validate(token);

        if (!result.valid()) {
            log.warn("WebSocket 握手失败: {}", result.errorMessage());
            return false;
        }

        // 提取用户信息存入 attributes
        try {
            Claims claims = jwtUtil.extractClaim(token);
            String username = claims.get("username", String.class);

            attributes.put("userId", result.userId());
            attributes.put("username", username);
            attributes.put("token", token);

            log.info("WebSocket 握手成功, userId: {}, username: {}", result.userId(), username);
            return true;
        } catch (Exception e) {
            log.error("WebSocket 握手失败: 提取用户信息错误, {}", e.getMessage());
            return false;
        }
    }

    /**
     * 握手后触发
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 握手完成后的处理
    }
}
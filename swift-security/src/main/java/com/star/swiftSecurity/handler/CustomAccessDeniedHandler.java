package com.star.swiftSecurity.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.star.swiftCommon.domain.PubResult;
import com.star.swiftCommon.constant.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 自定义访问拒绝处理器
 * 处理已认证用户访问无权限资源的情况
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        log.error("访问被拒绝: {} - {}", request.getRequestURI(), accessDeniedException.getMessage());

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        PubResult<?> result = PubResult.error(
                ResultCode.FORBIDDEN.getCode(),
                "权限不足，无法访问该资源"
        );

        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}

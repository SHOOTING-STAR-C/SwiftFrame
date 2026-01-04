package com.star.swiftSecurity.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.star.swiftCommon.domain.PubResult;
import com.star.swiftSecurity.constant.UserReCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 自定义认证入口点
 * 处理认证失败的情况，包括用户名或密码错误
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        log.error("认证失败: {}", authException.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        UserReCode reCode = UserReCode.UNAUTHORIZED;
        if (authException instanceof BadCredentialsException) {
            reCode = UserReCode.BAD_CREDENTIALS;
        }

        PubResult<?> result = PubResult.error(
                reCode.getCode(),
                reCode.getMessage()
        );

        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}

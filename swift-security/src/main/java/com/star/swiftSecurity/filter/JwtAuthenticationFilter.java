package com.star.swiftSecurity.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.star.swiftCommon.domain.PubResult;
import com.star.swiftSecurity.constant.TokenConstants;
import com.star.swiftSecurity.constant.TokenReCode;
import com.star.swiftSecurity.properties.SecurityProperties;
import com.star.swiftSecurity.service.SwiftUserService;
import com.star.swiftSecurity.utils.JwtUtil;
import com.star.swiftredis.service.TokenStorageService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

/**
 * 自定义jwt过滤器
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final SwiftUserService userDetailsService;
    private final TokenStorageService tokenStorageService;
    private final SecurityProperties securityProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthenticationFilter(JwtUtil jwtUtil, SwiftUserService userDetailsService, 
                                   TokenStorageService tokenStorageService, SecurityProperties securityProperties) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.tokenStorageService = tokenStorageService;
        this.securityProperties = securityProperties;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if (isWhitelisted(requestURI, request)) {
            log.debug("路径 {} 在白名单中，跳过 JWT 过滤器", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("请求 {} 缺少有效的Authorization头", requestURI);
            sendErrorResponse(response, TokenReCode.TOKEN_INVALID);
            return;
        }

        final String jwt = authHeader.substring(7);
        Long userId;
        
        // 提取用户ID，捕获JWT过期异常
        try {
            userId = jwtUtil.extractUserId(jwt);
        } catch (ExpiredJwtException e) {
            log.error("JWT expired during userId extraction: {}", e.getMessage());
            sendErrorResponse(response, TokenReCode.TOKEN_EXPIRED);
            return;
        } catch (Exception e) {
            log.error("JWT extraction error: {}", e.getMessage());
            sendErrorResponse(response, TokenReCode.TOKEN_INVALID);
            return;
        }

        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUserId(userId);

            // 验证JWT签名和Redis中的令牌有效性
            try {
                if (!jwtUtil.validateToken(jwt)) {
                    sendErrorResponse(response, TokenReCode.TOKEN_INVALID);
                    return;
                }
            } catch (ExpiredJwtException e) {
                log.error("JWT expired: {}", e.getMessage());
                sendErrorResponse(response, TokenReCode.TOKEN_EXPIRED);
                return;
            } catch (Exception e) {
                log.error("JWT validation error: {}", e.getMessage());
                sendErrorResponse(response, TokenReCode.TOKEN_INVALID);
                return;
            }
            
            if (!tokenStorageService.validateToken(TokenConstants.accessToken, userId, jwt)) {
                sendErrorResponse(response, TokenReCode.TOKEN_NOT_FOUND);
                return;
            }
            
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 发送错误响应
     */
    private void sendErrorResponse(HttpServletResponse response, TokenReCode tokenReCode) throws IOException {
        response.reset();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("X-Auth-Error", "true");
        
        PubResult<?> result = PubResult.error(tokenReCode.getCode(), tokenReCode.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(result));
        response.flushBuffer();
    }

    /**
     * 检查请求路径是否在白名单中
     */
    private boolean isWhitelisted(String requestURI, HttpServletRequest request) {
        SecurityProperties.WhitelistItem[] whitelistItems = securityProperties.getWhitelistItems();
        String httpMethod = request.getMethod();
        
        // 获取 context-path 并从请求 URI 中去除
        String contextPath = securityProperties.getServletContext() != null 
                ? securityProperties.getServletContext().getContextPath() 
                : "";
        
        String pathToMatch;
        if (!contextPath.isEmpty() && requestURI.startsWith(contextPath)) {
            pathToMatch = requestURI.substring(contextPath.length());
        } else {
            pathToMatch = requestURI;
        }

        for (SecurityProperties.WhitelistItem item : whitelistItems) {
            // 如果路径匹配
            if (pathMatcher.match(item.getPath(), pathToMatch)) {
                // 如果是ANY方法，允许所有HTTP方法
                if (item.isAnyMethod()) {
                    return true;
                }
                // 如果指定了HTTP方法，检查是否匹配
                if (item.getMethod().equalsIgnoreCase(httpMethod)) {
                    return true;
                }
            }
        }
        
        return false;
    }
}

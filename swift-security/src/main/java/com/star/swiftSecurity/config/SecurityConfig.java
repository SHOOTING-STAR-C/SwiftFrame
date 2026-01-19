package com.star.swiftSecurity.config;

import com.star.swiftSecurity.filter.JwtAuthenticationFilter;
import com.star.swiftSecurity.handler.CustomAccessDeniedHandler;
import com.star.swiftSecurity.handler.CustomAuthenticationEntryPoint;
import com.star.swiftSecurity.properties.SecurityProperties;
import com.star.swiftSecurity.service.SwiftUserService;
import com.star.swiftSecurity.utils.JwtUtil;
import com.star.swiftredis.service.TokenStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.Map;

/**
 * Security配置
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final SecurityProperties securityProperties;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    public SecurityConfig(SecurityProperties securityProperties, 
                         CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
                         CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.securityProperties = securityProperties;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        // 打印白名单配置用于调试
        SecurityProperties.WhitelistItem[] whitelistItems = securityProperties.getWhitelistItems();
        log.info("Security白名单配置: {}", Arrays.toString(whitelistItems));
        
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    // 完全放行的路径（所有HTTP方法）
                    String[] permitAllPaths = securityProperties.getPermitAllPaths();
                    if (permitAllPaths.length > 0) {
                        auth.requestMatchers(permitAllPaths).permitAll();
                    }
                    
                    // 按HTTP方法分组的路径
                    Map<String, String[]> methodPaths = securityProperties.getMethodPaths();
                    methodPaths.forEach((method, paths) -> {
                        HttpMethod httpMethod = HttpMethod.valueOf(method.toUpperCase());
                        auth.requestMatchers(httpMethod, paths).permitAll();
                    });
                    
                    // SSE流式端点特殊处理：在请求进入时进行权限检查，避免响应已提交后再次检查
                    auth.requestMatchers("/ai/chat/stream").permitAll();
                    auth.requestMatchers("/ai/chat/anonymous/stream").permitAll();
                    
                    // 其他所有请求需要认证
                    auth.anyRequest().authenticated();
                })
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil, SwiftUserService userDetailsService, 
                                                           TokenStorageService tokenStorageService, SecurityProperties securityProperties) {
        return new JwtAuthenticationFilter(jwtUtil, userDetailsService, tokenStorageService, securityProperties);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

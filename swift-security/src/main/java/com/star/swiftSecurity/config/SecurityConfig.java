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
        String[] whitelist = securityProperties.getWhitelistArray();
        log.info("Security白名单配置: {}", Arrays.toString(whitelist));
        
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(whitelist).permitAll()
                        .anyRequest().authenticated()
                )
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

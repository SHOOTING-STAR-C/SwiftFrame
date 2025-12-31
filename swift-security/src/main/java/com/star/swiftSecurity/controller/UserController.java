package com.star.swiftSecurity.controller;

import com.star.swiftCommon.domain.PubResult;
import com.star.swiftSecurity.entity.SwiftUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户管理接口
 *
 * @author SHOOTING_STAR_C
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "系统用户相关接口")
public class UserController {

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/info")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    public PubResult<Map<String, Object>> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof SwiftUserDetails) {
            SwiftUserDetails userDetails = (SwiftUserDetails) authentication.getPrincipal();
            
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", userDetails.getUserId());
            userInfo.put("username", userDetails.getUsername());
            userInfo.put("fullName", userDetails.getFullName());
            userInfo.put("email", userDetails.getEmail());
            userInfo.put("phone", userDetails.getPhone());
            userInfo.put("enabled", userDetails.isEnabled());
            userInfo.put("lastLoginAt", userDetails.getLastLoginAt());
            userInfo.put("createdAt", userDetails.getCreatedAt());
            userInfo.put("roles", userDetails.getUserRoles().stream()
                    .map(userRole -> userRole.getRole().getName())
                    .toList());
            userInfo.put("authorities", userDetails.getAuthorities().stream()
                    .map(Object::toString)
                    .toList());
            
            return PubResult.success(userInfo);
        }
        
        return PubResult.error("未找到用户信息");
    }
}

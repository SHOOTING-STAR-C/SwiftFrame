package com.star.swiftLogin.controller;

import com.star.swiftCommon.domain.PubResult;
import com.star.swiftLogin.dto.LoginRequest;
import com.star.swiftLogin.dto.RegisterRequest;
import com.star.swiftLogin.service.AuthService;
import com.star.swiftSecurity.domain.JwtToken;
import com.star.swiftSecurity.entity.SwiftUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 应用鉴权入口
 *
 * @author SHOOTING_STAR_C
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "鉴权管理", description = "登录和注册接口")
public class LoginController {
    private final AuthService authService;

    /**
     * 登录接口
     *
     * @param user user
     * @return PubResult<JwtToken>PubResult<JwtToken>
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "通过用户名和密码获取 JWT Token")
    public PubResult<JwtToken> login(@Valid @RequestBody LoginRequest user) {
        return authService.login(user.getUsername(), user.getPassword());
    }

    /**
     * 用户注册接口
     *
     * @param registerRequest RegisterRequest
     * @return PubResult<SwiftUserDetails>
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "注册新用户")
    public PubResult<SwiftUserDetails> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        SwiftUserDetails user = new SwiftUserDetails();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(registerRequest.getPassword());
        user.setEmail(registerRequest.getEmail());
        user.setPhone(registerRequest.getPhone());
        return PubResult.success(authService.createUser(user));

    }


}

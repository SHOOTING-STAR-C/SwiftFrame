package com.star.swiftLogin.controller;

import com.star.swiftCommon.domain.PubResult;
import com.star.swiftLogin.dto.LoginRequest;
import com.star.swiftLogin.dto.RegisterRequest;
import com.star.swiftLogin.service.AuthService;
import com.star.swiftSecurity.domain.JwtToken;
import com.star.swiftSecurity.entity.SwiftUserDetails;
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
public class LoginController {
    private final AuthService authService;

    /**
     * 登录接口
     *
     * @param user user
     * @return PubResult<JwtToken>PubResult<JwtToken>
     */
    @PostMapping("/login")
    public PubResult<JwtToken> login(@Valid @RequestBody LoginRequest user) {
        return authService.login(user.getUsername(), user.getPassword());
    }

    /**
     * 用户登录接口
     *
     * @param registerRequest RegisterRequest
     * @return PubResult<SwiftUserDetails>
     */
    @PostMapping("/register")
    public PubResult<SwiftUserDetails> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        SwiftUserDetails user = new SwiftUserDetails();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(registerRequest.getPassword());
        user.setEmail(registerRequest.getEmail());
        user.setPhone(registerRequest.getPhone());
        return PubResult.success(authService.createUser(user));

    }


}

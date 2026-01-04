package com.star.swiftLogin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 刷新Token请求
 *
 * @author SHOOTING_STAR_C
 */
@Data
@Schema(description = "刷新Token请求")
public class RefreshTokenRequest {
    
    @NotBlank(message = "刷新令牌不能为空")
    @Schema(description = "刷新令牌", required = true)
    private String refreshToken;
}

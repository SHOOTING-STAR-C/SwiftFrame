package com.star.swiftAi.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.star.swiftAi.dto.ProviderDTO;
import com.star.swiftAi.dto.TestResultDTO;
import com.star.swiftAi.entity.AiProvider;
import com.star.swiftAi.service.AiProviderService;
import com.star.swiftCommon.domain.PubResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * AI供应商控制器
 *
 * @author SHOOTING_STAR_C
 */
@Tag(name = "AI-供应商", description = "AI 供应商相关接口")
@RestController
@RequestMapping("/ai/providers")
@RequiredArgsConstructor
public class AiProviderController {

    private final AiProviderService aiProviderService;

    /**
     * 创建供应商
     */
    @Operation(summary = "创建供应商", description = "创建一个新的 AI 供应商配置")
    @ApiResponse(responseCode = "200", description = "创建成功", content = @Content(schema = @Schema(implementation = AiProvider.class)))
    @PostMapping
    public PubResult<AiProvider> createProvider(
            @Valid @RequestBody AiProvider provider) {
        AiProvider created = aiProviderService.createProvider(provider);
        return PubResult.success(created);
    }

    /**
     * 更新供应商
     */
    @Operation(summary = "更新供应商", description = "更新指定 ID 的供应商信息")
    @ApiResponse(responseCode = "200", description = "更新成功", content = @Content(schema = @Schema(implementation = AiProvider.class)))
    @PutMapping("/{id}")
    public PubResult<AiProvider> updateProvider(
            @Parameter(description = "供应商 ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody AiProvider provider) {
        AiProvider updated = aiProviderService.updateProvider(id, provider);
        return PubResult.success(updated);
    }

    /**
     * 删除供应商
     */
    @Operation(summary = "删除供应商", description = "删除指定ID的供应商")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @DeleteMapping("/{id}")
    public PubResult<Void> deleteProvider(
            @Parameter(description = "供应商ID") @PathVariable Long id) {
        aiProviderService.deleteProvider(id);
        return PubResult.success();
    }

    /**
     * 获取供应商详情
     */
    @Operation(summary = "获取供应商详情", description = "根据ID获取供应商详细信息")
    @ApiResponse(responseCode = "200", description = "获取成功", content = @Content(schema = @Schema(implementation = ProviderDTO.class)))
    @GetMapping("/{id}")
    public PubResult<ProviderDTO> getProviderById(
            @Parameter(description = "供应商ID") @PathVariable Long id) {
        ProviderDTO provider = aiProviderService.getProviderById(id);
        return PubResult.success(provider);
    }

    /**
     * 获取供应商列表（分页）
     */
    @Operation(summary = "获取供应商列表", description = "分页获取供应商列表，支持按启用状态筛选")
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping
    public PubResult<IPage<ProviderDTO>> getProviders(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "是否启用") @RequestParam(required = false) Boolean enabled) {
        IPage<ProviderDTO> providers = aiProviderService.getProviders(page, size, enabled);
        return PubResult.success(providers);
    }

    /**
     * 测试供应商连接
     */
    @Operation(summary = "测试供应商连接", description = "测试指定供应商的API连接是否正常")
    @ApiResponse(responseCode = "200", description = "测试完成", content = @Content(schema = @Schema(implementation = TestResultDTO.class)))
    @PostMapping("/{id}/test")
    public PubResult<TestResultDTO> testConnection(
            @Parameter(description = "供应商ID") @PathVariable Long id) {
        TestResultDTO result = aiProviderService.testConnection(id);
        return PubResult.success(result);
    }
}

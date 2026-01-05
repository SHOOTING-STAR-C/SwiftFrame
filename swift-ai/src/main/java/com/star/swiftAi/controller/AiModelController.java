package com.star.swiftAi.controller;

import com.star.swiftAi.dto.ModelDTO;
import com.star.swiftAi.dto.TestResultDTO;
import com.star.swiftAi.entity.AiModel;
import com.star.swiftAi.core.response.ModelsResponse;
import com.star.swiftAi.service.AiModelService;
import com.star.swiftCommon.domain.PageResult;
import com.star.swiftCommon.domain.PubResult;
import com.star.swiftSecurity.constant.AuthorityConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * AI模型控制器
 *
 * @author SHOOTING_STAR_C
 */
@Tag(name = "AI-模型", description = "AI 模型相关接口")
@RestController
@RequestMapping("/ai/models")
@RequiredArgsConstructor
public class AiModelController {

    private final AiModelService aiModelService;

    /**
     * 创建模型
     */
    @Operation(summary = "创建模型", description = "创建一个新的 AI 模型配置")
    @ApiResponse(responseCode = "200", description = "创建成功", content = @Content(schema = @Schema(implementation = AiModel.class)))
    @PostMapping
    @PreAuthorize("hasAuthority('" + AuthorityConstants.AI_MODEL_CREATE + "')")
    public PubResult<AiModel> createModel(
            @Valid @RequestBody AiModel model) {
        AiModel created = aiModelService.createModel(model);
        return PubResult.success(created);
    }

    /**
     * 更新模型
     */
    @Operation(summary = "更新模型", description = "更新指定 ID 的模型信息")
    @ApiResponse(responseCode = "200", description = "更新成功", content = @Content(schema = @Schema(implementation = AiModel.class)))
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.AI_MODEL_UPDATE + "')")
    public PubResult<AiModel> updateModel(
            @Parameter(description = "模型 ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody AiModel model) {
        AiModel updated = aiModelService.updateModel(id, model);
        return PubResult.success(updated);
    }

    /**
     * 删除模型
     */
    @Operation(summary = "删除模型", description = "删除指定ID的模型")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.AI_MODEL_DELETE + "')")
    public PubResult<Void> deleteModel(
            @Parameter(description = "模型ID") @PathVariable Long id) {
        aiModelService.deleteModel(id);
        return PubResult.success();
    }

    /**
     * 获取模型详情
     */
    @Operation(summary = "获取模型详情", description = "根据ID获取模型详细信息")
    @ApiResponse(responseCode = "200", description = "获取成功", content = @Content(schema = @Schema(implementation = ModelDTO.class)))
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.AI_MODEL_READ + "')")
    public PubResult<ModelDTO> getModelById(
            @Parameter(description = "模型ID") @PathVariable Long id) {
        ModelDTO model = aiModelService.getModelById(id);
        return PubResult.success(model);
    }

    /**
     * 获取模型列表（分页）
     */
    @Operation(summary = "获取模型列表", description = "分页获取模型列表，支持按供应商 ID 和启用状态筛选")
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping
    @PreAuthorize("hasAuthority('" + AuthorityConstants.AI_MODEL_READ + "')")
    public PageResult<ModelDTO> getModels(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "供应商 ID", example = "1") @RequestParam(required = false) Long providerId,
            @Parameter(description = "是否启用") @RequestParam(required = false) Boolean enabled) {
        return aiModelService.getModels(page, size, providerId, enabled);
    }

    /**
     * 测试模型连接
     */
    @Operation(summary = "测试模型连接", description = "测试指定模型的API连接是否正常")
    @ApiResponse(responseCode = "200", description = "测试完成", content = @Content(schema = @Schema(implementation = TestResultDTO.class)))
    @PostMapping("/{id}/test")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.AI_MODEL_TEST + "')")
    public PubResult<TestResultDTO> testModel(
            @Parameter(description = "模型ID") @PathVariable Long id) {
        TestResultDTO result = aiModelService.testModel(id);
        return PubResult.success(result);
    }

    /**
     * 从供应商获取所有模型
     */
    @Operation(summary = "从供应商获取所有模型", description = "调用供应商API获取所有可用模型列表")
    @ApiResponse(responseCode = "200", description = "获取成功", content = @Content(schema = @Schema(implementation = ModelsResponse.class)))
    @GetMapping("/provider/{providerId}/models")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.AI_MODEL_READ + "')")
    public PubResult<ModelsResponse> getModelsFromProvider(
            @Parameter(description = "供应商ID") @PathVariable Long providerId) {
        ModelsResponse models = aiModelService.getModelsFromProvider(providerId);
        return PubResult.success(models);
    }
}

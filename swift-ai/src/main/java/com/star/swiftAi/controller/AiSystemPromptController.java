package com.star.swiftAi.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.star.swiftAi.dto.SystemPromptDTO;
import com.star.swiftAi.dto.SystemPromptRequestDTO;
import com.star.swiftAi.entity.AiSystemPrompt;
import com.star.swiftAi.service.AiSystemPromptService;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI系统提示词控制器
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Tag(name = "AI-系统提示词", description = "AI系统提示词管理接口")
@RestController
@RequestMapping("/ai/system-prompt")
@RequiredArgsConstructor
public class AiSystemPromptController {

    private final AiSystemPromptService aiSystemPromptService;

    /**
     * 创建系统提示词
     */
    @Operation(summary = "创建系统提示词", description = "创建新的系统提示词")
    @ApiResponse(responseCode = "200", description = "创建成功", content = @Content(schema = @Schema(implementation = SystemPromptDTO.class)))
    @PostMapping
    @PreAuthorize("hasAuthority('" + AuthorityConstants.AI_SYSTEM_PROMPT_MANAGE + "')")
    public PubResult<SystemPromptDTO> create(@Valid @RequestBody SystemPromptRequestDTO request) {
        AiSystemPrompt prompt = aiSystemPromptService.create(request);
        return PubResult.success(aiSystemPromptService.toDTO(prompt));
    }

    /**
     * 更新系统提示词
     */
    @Operation(summary = "更新系统提示词", description = "更新指定的系统提示词")
    @ApiResponse(responseCode = "200", description = "更新成功", content = @Content(schema = @Schema(implementation = SystemPromptDTO.class)))
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.AI_SYSTEM_PROMPT_MANAGE + "')")
    public PubResult<SystemPromptDTO> update(
            @Parameter(description = "提示词ID", required = true) @PathVariable Long id,
            @Valid @RequestBody SystemPromptRequestDTO request) {
        AiSystemPrompt prompt = aiSystemPromptService.update(id, request);
        return PubResult.success(aiSystemPromptService.toDTO(prompt));
    }

    /**
     * 删除系统提示词
     */
    @Operation(summary = "删除系统提示词", description = "删除指定的系统提示词")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.AI_SYSTEM_PROMPT_MANAGE + "')")
    public PubResult<Void> delete(
            @Parameter(description = "提示词ID", required = true) @PathVariable Long id) {
        aiSystemPromptService.delete(id);
        return PubResult.success();
    }

    /**
     * 获取系统提示词详情
     */
    @Operation(summary = "获取系统提示词详情", description = "根据ID获取系统提示词的详细信息")
    @ApiResponse(responseCode = "200", description = "获取成功", content = @Content(schema = @Schema(implementation = SystemPromptDTO.class)))
    @GetMapping("/{id}")
    public PubResult<SystemPromptDTO> getById(
            @Parameter(description = "提示词ID", required = true) @PathVariable Long id) {
        AiSystemPrompt prompt = aiSystemPromptService.getById(id);
        if (prompt == null) {
            throw new RuntimeException("提示词不存在");
        }
        return PubResult.success(aiSystemPromptService.toDTO(prompt));
    }

    /**
     * 分页查询系统提示词列表
     */
    @Operation(summary = "分页查询系统提示词列表", description = "分页查询系统提示词列表，支持关键词搜索和启用状态筛选")
    @ApiResponse(responseCode = "200", description = "查询成功")
    @GetMapping
    public PubResult<IPage<SystemPromptDTO>> pageList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "启用状态") @RequestParam(required = false) Boolean enabled) {
        IPage<SystemPromptDTO> result = aiSystemPromptService.pageList(page, size, keyword, enabled);
        return PubResult.success(result);
    }

    /**
     * 获取所有启用的系统提示词
     */
    @Operation(summary = "获取所有启用的系统提示词", description = "获取所有启用的系统提示词列表，用于前端下拉选择")
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/enabled")
    public PubResult<List<SystemPromptDTO>> getEnabledPrompts() {
        List<SystemPromptDTO> prompts = aiSystemPromptService.getEnabledPrompts();
        return PubResult.success(prompts);
    }
}

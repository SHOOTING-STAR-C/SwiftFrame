package com.star.swiftAi.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.star.swiftAi.dto.SessionDTO;
import com.star.swiftAi.entity.AiChatSession;
import com.star.swiftAi.service.AiChatSessionService;
import com.star.swiftCommon.domain.PubResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * AI聊天会话控制器
 *
 * @author SHOOTING_STAR_C
 */
@Tag(name = "AI-会话", description = "AI 聊天会话相关接口")
@RestController
@RequestMapping("/ai/sessions")
@RequiredArgsConstructor
public class AiChatSessionController {

    private final AiChatSessionService aiChatSessionService;

    /**
     * 创建会话
     */
    @Operation(summary = "创建会话", description = "创建一个新的 AI 聊天会话")
    @ApiResponse(responseCode = "200", description = "创建成功", content = @Content(schema = @Schema(implementation = AiChatSession.class)))
    @PostMapping
    public PubResult<AiChatSession> createSession(
            @Parameter(description = "用户 ID", example = "user123") @RequestParam String userId,
            @Parameter(description = "模型 ID", example = "1") @RequestParam Long modelId,
            @Parameter(description = "会话标题", example = "新会话") @RequestParam(required = false) String title) {
        AiChatSession session = aiChatSessionService.createSession(userId, modelId, title);
        return PubResult.success(session);
    }

    /**
     * 更新会话
     */
    @Operation(summary = "更新会话", description = "更新指定会话的标题")
    @ApiResponse(responseCode = "200", description = "更新成功")
    @PutMapping("/{sessionId}")
    public PubResult<Void> updateSession(
            @Parameter(description = "会话 ID", example = "1723456789012345678") @PathVariable String sessionId,
            @Parameter(description = "会话标题", example = "新标题") @RequestParam String title) {
        aiChatSessionService.updateSession(sessionId, title);
        return PubResult.success();
    }

    /**
     * 删除会话
     */
    @Operation(summary = "删除会话", description = "删除指定会话及其所有消息")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @DeleteMapping("/{sessionId}")
    public PubResult<Void> deleteSession(
            @Parameter(description = "会话ID") @PathVariable String sessionId) {
        aiChatSessionService.deleteSession(sessionId);
        return PubResult.success();
    }

    /**
     * 获取会话详情
     */
    @Operation(summary = "获取会话详情", description = "根据会话ID获取会话详细信息")
    @ApiResponse(responseCode = "200", description = "获取成功", content = @Content(schema = @Schema(implementation = SessionDTO.class)))
    @GetMapping("/{sessionId}")
    public PubResult<SessionDTO> getSessionById(
            @Parameter(description = "会话ID") @PathVariable String sessionId) {
        SessionDTO session = aiChatSessionService.getSessionById(sessionId);
        return PubResult.success(session);
    }

    /**
     * 获取用户的会话列表（分页）
     */
    @Operation(summary = "获取用户的会话列表", description = "分页获取指定用户的所有会话")
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping
    public PubResult<IPage<SessionDTO>> getSessionsByUserId(
            @Parameter(description = "用户 ID", example = "user123") @RequestParam String userId,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer size) {
        IPage<SessionDTO> sessions = aiChatSessionService.getSessionsByUserId(userId, page, size);
        return PubResult.success(sessions);
    }
}

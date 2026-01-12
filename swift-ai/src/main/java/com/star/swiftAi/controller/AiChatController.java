package com.star.swiftAi.controller;

import com.star.swiftAi.dto.ChatRequestDTO;
import com.star.swiftAi.dto.ChatResponseDTO;
import com.star.swiftAi.dto.ImportChatRequestDTO;
import com.star.swiftAi.entity.AiChatMessage;
import com.star.swiftAi.entity.AiChatSession;
import com.star.swiftAi.entity.AiModel;
import com.star.swiftAi.service.AiChatMessageService;
import com.star.swiftAi.service.AiChatService;
import com.star.swiftAi.service.AiChatSessionService;
import com.star.swiftAi.service.AiModelService;
import com.star.swiftCommon.domain.PubResult;
import com.star.swiftSecurity.constant.AuthorityConstants;
import com.star.swiftSecurity.utils.SecurityUtils;
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
 * AI聊天控制器
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Tag(name = "AI-聊天", description = "AI 聊天相关接口")
@RestController
@RequestMapping("/ai/chat")
@RequiredArgsConstructor
public class AiChatController {

    private final AiChatSessionService aiChatSessionService;
    private final AiChatMessageService aiChatMessageService;
    private final AiModelService aiModelService;
    private final AiChatService aiChatService;

    /**
     * 发送聊天消息
     */
    @Operation(summary = "发送聊天消息", description = "向 AI 模型发送消息并获取回复，支持新会话和已有会话。如果是新会话，会自动创建一个默认会话。")
    @ApiResponse(responseCode = "200", description = "发送成功", content = @Content(schema = @Schema(implementation = ChatResponseDTO.class)))
    @PostMapping
    @PreAuthorize("hasAuthority('" + AuthorityConstants.AI_CHAT_SEND + "')")
    public PubResult<ChatResponseDTO> chat(
            @Valid @RequestBody ChatRequestDTO request) {
        return PubResult.success(aiChatService.chat(request));
    }

    /**
     * 匿名聊天（不保存到数据库）
     * 用于未登录用户的临时聊天
     */
    @Operation(summary = "匿名聊天", description = "匿名用户聊天，不保存到数据库，适合未登录用户使用")
    @ApiResponse(responseCode = "200", description = "聊天成功", content = @Content(schema = @Schema(implementation = ChatResponseDTO.class)))
    @PostMapping("/anonymous")
    public PubResult<ChatResponseDTO> anonymousChat(
            @Valid @RequestBody ChatRequestDTO request) {
        return PubResult.success(aiChatService.anonymousChat(request));
    }

    /**
     * 导入聊天记录
     * 用于将匿名用户的聊天记录迁移到登录用户账户
     */
    @Operation(summary = "导入聊天记录", description = "将匿名用户的聊天记录导入到当前登录用户账户")
    @ApiResponse(responseCode = "200", description = "导入成功")
    @PostMapping("/import")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.AI_CHAT_SEND + "')")
    public PubResult<Integer> importChatHistory(
            @Valid @RequestBody ImportChatRequestDTO request) {
        String userId = SecurityUtils.getCurrentUserId();
        int importedCount = aiChatService.importChatHistory(userId, request);
        return PubResult.success(importedCount);
    }

    /**
     * 获取聊天历史
     */
    @Operation(summary = "获取聊天历史", description = "获取指定会话的所有聊天消息历史")
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/history")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.AI_CHAT_HISTORY + "')")
    public PubResult<List<ChatResponseDTO>> getChatHistory(
            @Parameter(description = "会话ID", required = true) @RequestParam String sessionId) {
        // 验证会话是否存在
        AiChatSession session = aiChatSessionService.getBySessionId(sessionId);
        if (session == null) {
            throw new RuntimeException("会话不存在");
        }

        // 获取消息列表
        var messages = aiChatMessageService.getMessagesBySessionId(sessionId);

        // 转换为ChatResponseDTO
        List<ChatResponseDTO> history = messages.stream().map(msg -> {
            ChatResponseDTO dto = new ChatResponseDTO();
            dto.setSessionId(sessionId);
            dto.setMessageId(msg.getId());
            dto.setRole(msg.getRole());
            dto.setContent(msg.getContent());
            dto.setTokensUsed(msg.getTokensUsed());
            dto.setCreatedAt(msg.getCreatedAt());
            return dto;
        }).toList();

        return PubResult.success(history);
    }
}

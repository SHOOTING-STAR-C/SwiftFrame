package com.star.swiftAi.controller;

import com.star.swiftAi.dto.MessageDTO;
import com.star.swiftAi.service.AiChatMessageService;
import com.star.swiftCommon.domain.PubResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI聊天消息控制器
 *
 * @author SHOOTING_STAR_C
 */
@Tag(name = "AI-消息", description = "AI 聊天消息相关接口")
@RestController
@RequestMapping("/ai/messages")
@RequiredArgsConstructor
public class AiChatMessageController {

    private final AiChatMessageService aiChatMessageService;

    /**
     * 获取会话的所有消息
     */
    @Operation(summary = "获取会话的所有消息", description = "获取指定会话的所有聊天消息，按时间顺序返回")
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping
    public PubResult<List<MessageDTO>> getMessagesBySessionId(
            @Parameter(description = "会话 ID", example = "1723456789012345678") @RequestParam String sessionId) {
        List<MessageDTO> messages = aiChatMessageService.getMessagesBySessionId(sessionId);
        return PubResult.success(messages);
    }
}

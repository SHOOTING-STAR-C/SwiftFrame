package com.star.swiftAi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.swiftAi.dto.SessionDTO;
import com.star.swiftAi.entity.AiChatSession;
import com.star.swiftCommon.domain.PageResult;

import java.util.List;

/**
 * AI聊天会话服务接口
 *
 * @author SHOOTING_STAR_C
 */
public interface AiChatSessionService extends IService<AiChatSession> {

    /**
     * 创建会话
     *
     * @param userId  用户ID
     * @param modelId 模型ID
     * @param title   会话标题
     * @return 创建的会话
     */
    AiChatSession createSession(String userId, Long modelId, String title);

    /**
     * 更新会话
     *
     * @param sessionId 会话ID
     * @param title     会话标题
     */
    void updateSession(String sessionId, String title);

    /**
     * 删除会话
     *
     * @param sessionId 会话ID
     */
    void deleteSession(String sessionId);

    /**
     * 根据会话ID获取会话
     *
     * @param sessionId 会话ID
     * @return 会话
     */
    AiChatSession getBySessionId(String sessionId);

    /**
     * 获取会话详情
     *
     * @param sessionId 会话ID
     * @return 会话DTO
     */
    SessionDTO getSessionById(String sessionId);

    /**
     * 获取用户的会话列表（分页）
     *
     * @param userId 用户ID
     * @param page   页码
     * @param size   每页大小
     * @return 会话列表
     */
    PageResult<SessionDTO> getSessionsByUserId(String userId, Integer page, Integer size);

    /**
     * 获取用户的会话列表（不分页）
     *
     * @param userId 用户ID
     * @return 会话列表
     */
    List<SessionDTO> getAllSessionsByUserId(String userId);
}

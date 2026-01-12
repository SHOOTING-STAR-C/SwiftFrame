package com.star.swiftAi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.swiftAi.dto.SessionDTO;
import com.star.swiftAi.entity.AiChatSession;
import com.star.swiftAi.entity.AiModel;
import com.star.swiftAi.mapper.postgresql.AiChatSessionMapper;
import com.star.swiftCommon.domain.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * AI聊天会话服务
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatSessionService extends ServiceImpl<AiChatSessionMapper, AiChatSession> {

    private final AiModelService aiModelService;

    /**
     * 创建会话
     *
     * @param userId  用户ID
     * @param modelId 模型ID
     * @param title   会话标题
     * @return 创建的会话
     */
    @Transactional(rollbackFor = Exception.class)
    public AiChatSession createSession(String userId, Long modelId, String title) {
        // 检查模型是否存在
        AiModel model = aiModelService.getById(modelId);
        if (model == null) {
            throw new RuntimeException("模型不存在");
        }
        
        AiChatSession session = new AiChatSession();
        session.setSessionId(UUID.randomUUID().toString().replace("-", ""));
        session.setUserId(userId);
        session.setModelId(modelId);
        session.setTitle(title != null ? title : "新对话");
        
        this.save(session);
        log.info("创建会话成功: sessionId={}, userId={}", session.getSessionId(), userId);
        return session;
    }

    /**
     * 更新会话
     *
     * @param sessionId 会话ID
     * @param title     会话标题
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateSession(String sessionId, String title) {
        AiChatSession session = this.getBySessionId(sessionId);
        if (session == null) {
            throw new RuntimeException("会话不存在");
        }
        
        session.setTitle(title);
        this.updateById(session);
        log.info("更新会话成功: sessionId={}", sessionId);
    }

    /**
     * 删除会话
     *
     * @param sessionId 会话ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteSession(String sessionId) {
        AiChatSession session = this.getBySessionId(sessionId);
        if (session == null) {
            throw new RuntimeException("会话不存在");
        }
        
        this.removeById(session.getId());
        log.info("删除会话成功: sessionId={}", sessionId);
    }

    /**
     * 根据会话ID获取会话
     *
     * @param sessionId 会话ID
     * @return 会话
     */
    public AiChatSession getBySessionId(String sessionId) {
        LambdaQueryWrapper<AiChatSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiChatSession::getSessionId, sessionId);
        return this.getOne(wrapper);
    }

    /**
     * 获取会话详情
     *
     * @param sessionId 会话ID
     * @return 会话DTO
     */
    public SessionDTO getSessionById(String sessionId) {
        AiChatSession session = this.getBySessionId(sessionId);
        if (session == null) {
            throw new RuntimeException("会话不存在");
        }
        
        SessionDTO dto = new SessionDTO();
        BeanUtils.copyProperties(session, dto);
        
        // 获取模型名称
        AiModel model = aiModelService.getById(session.getModelId());
        if (model != null) {
            dto.setModelName(model.getModelName());
        }
        
        return dto;
    }

    /**
     * 获取用户的会话列表（分页）
     *
     * @param userId 用户ID
     * @param page   页码
     * @param size   每页大小
     * @return 会话列表
     */
    public PageResult<SessionDTO> getSessionsByUserId(String userId, Integer page, Integer size) {
        Page<AiChatSession> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<AiChatSession> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(AiChatSession::getUserId, userId);
        wrapper.orderByDesc(AiChatSession::getUpdatedAt);
        
        IPage<AiChatSession> sessionPage = this.page(pageParam, wrapper);
        
        // 转换为DTO
        IPage<SessionDTO> dtoPage = sessionPage.convert(session -> {
            SessionDTO dto = new SessionDTO();
            BeanUtils.copyProperties(session, dto);
            
            // 获取模型名称
            AiModel model = aiModelService.getById(session.getModelId());
            if (model != null) {
                dto.setModelName(model.getModelName());
            }
            
            return dto;
        });
        
        // 转换为 PageResult
        return PageResult.success(dtoPage.getRecords(), dtoPage.getTotal(), dtoPage.getCurrent(), dtoPage.getSize());
    }

    /**
     * 获取用户的会话列表（不分页）
     *
     * @param userId 用户ID
     * @return 会话列表
     */
    public java.util.List<SessionDTO> getAllSessionsByUserId(String userId) {
        LambdaQueryWrapper<AiChatSession> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(AiChatSession::getUserId, userId);
        wrapper.orderByDesc(AiChatSession::getUpdatedAt);
        
        java.util.List<AiChatSession> sessions = this.list(wrapper);
        
        // 转换为DTO
        return sessions.stream().map(session -> {
            SessionDTO dto = new SessionDTO();
            BeanUtils.copyProperties(session, dto);
            
            // 获取模型名称
            AiModel model = aiModelService.getById(session.getModelId());
            if (model != null) {
                dto.setModelName(model.getModelName());
            }
            
            return dto;
        }).collect(java.util.stream.Collectors.toList());
    }
}

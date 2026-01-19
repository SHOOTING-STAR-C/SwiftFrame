package com.star.swiftAi.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.star.swiftAi.dto.SystemPromptDTO;
import com.star.swiftAi.dto.SystemPromptRequestDTO;
import com.star.swiftAi.entity.AiSystemPrompt;

import java.util.List;

/**
 * AI系统提示词服务
 *
 * @author SHOOTING_STAR_C
 */
public interface AiSystemPromptService extends IService<AiSystemPrompt> {

    /**
     * 创建系统提示词
     *
     * @param request 请求参数
     * @return 创建的提示词
     */
    AiSystemPrompt create(SystemPromptRequestDTO request);

    /**
     * 更新系统提示词
     *
     * @param id 提示词ID
     * @param request 请求参数
     * @return 更新后的提示词
     */
    AiSystemPrompt update(Long id, SystemPromptRequestDTO request);

    /**
     * 删除系统提示词
     *
     * @param id 提示词ID
     */
    void delete(Long id);

    /**
     * 根据ID获取提示词
     *
     * @param id 提示词ID
     * @return 提示词
     */
    AiSystemPrompt getById(Long id);

    /**
     * 分页查询提示词列表
     *
     * @param page 页码
     * @param size 每页大小
     * @param keyword 关键词（可选）
     * @param enabled 启用状态（可选）
     * @return 分页结果
     */
    IPage<SystemPromptDTO> pageList(int page, int size, String keyword, Boolean enabled);

    /**
     * 获取所有启用的提示词
     *
     * @return 启用的提示词列表
     */
    List<SystemPromptDTO> getEnabledPrompts();

    /**
     * 转换为DTO
     *
     * @param entity 实体
     * @return DTO
     */
    SystemPromptDTO toDTO(AiSystemPrompt entity);
}

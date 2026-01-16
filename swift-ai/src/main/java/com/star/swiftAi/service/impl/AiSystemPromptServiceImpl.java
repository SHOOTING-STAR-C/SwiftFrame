package com.star.swiftAi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.swiftAi.cache.AiConfigCacheService;
import com.star.swiftAi.dto.SystemPromptDTO;
import com.star.swiftAi.dto.SystemPromptRequestDTO;
import com.star.swiftAi.entity.AiSystemPrompt;
import com.star.swiftAi.mapper.postgresql.AiSystemPromptMapper;
import com.star.swiftAi.service.AiSystemPromptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AI系统提示词服务实现
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiSystemPromptServiceImpl extends ServiceImpl<AiSystemPromptMapper, AiSystemPrompt>
        implements AiSystemPromptService {

    private final AiConfigCacheService aiConfigCacheService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiSystemPrompt create(SystemPromptRequestDTO request) {
        AiSystemPrompt prompt = new AiSystemPrompt();
        BeanUtils.copyProperties(request, prompt);
        
        this.save(prompt);
        
        // 清除启用提示词列表缓存
        aiConfigCacheService.removeEnabledPromptsCache();
        
        log.info("创建系统提示词成功: {}", prompt.getPromptName());
        return prompt;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiSystemPrompt update(Long id, SystemPromptRequestDTO request) {
        AiSystemPrompt existing = this.getById(id);
        if (existing == null) {
            throw new RuntimeException("提示词不存在");
        }
        
        AiSystemPrompt prompt = new AiSystemPrompt();
        BeanUtils.copyProperties(request, prompt);
        prompt.setId(id);
        
        this.updateById(prompt);
        
        // 更新缓存
        aiConfigCacheService.cacheSystemPrompt(id, toDTO(prompt));
        // 清除启用提示词列表缓存
        aiConfigCacheService.removeEnabledPromptsCache();
        
        log.info("更新系统提示词成功: id={}, name={}", id, prompt.getPromptName());
        return prompt;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        AiSystemPrompt prompt = this.getById(id);
        if (prompt == null) {
            throw new RuntimeException("提示词不存在");
        }
        
        this.removeById(id);
        
        // 删除缓存
        aiConfigCacheService.removeSystemPromptCache(id);
        // 清除启用提示词列表缓存
        aiConfigCacheService.removeEnabledPromptsCache();
        
        log.info("删除系统提示词成功: id={}, name={}", id, prompt.getPromptName());
    }

    @Override
    public AiSystemPrompt getById(Long id) {
        // 先尝试从缓存获取
        SystemPromptDTO cached = aiConfigCacheService.getCachedSystemPrompt(id);
        if (cached != null) {
            AiSystemPrompt entity = new AiSystemPrompt();
            BeanUtils.copyProperties(cached, entity);
            return entity;
        }
        
        // 缓存未命中，从数据库查询
        AiSystemPrompt prompt = super.getById(id);
        if (prompt != null) {
            // 写入缓存
            aiConfigCacheService.cacheSystemPrompt(id, toDTO(prompt));
        }
        
        return prompt;
    }

    @Override
    public IPage<SystemPromptDTO> pageList(int page, int size, String keyword, Boolean enabled) {
        Page<AiSystemPrompt> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<AiSystemPrompt> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(AiSystemPrompt::getPromptName, keyword)
                    .or()
                    .like(AiSystemPrompt::getPromptContent, keyword)
                    .or()
                    .like(AiSystemPrompt::getDescription, keyword));
        }
        
        if (enabled != null) {
            wrapper.eq(AiSystemPrompt::getEnabled, enabled);
        }
        
        wrapper.orderByDesc(AiSystemPrompt::getCreatedAt);
        
        IPage<AiSystemPrompt> promptPage = this.page(pageParam, wrapper);
        
        // 转换为DTO
        return promptPage.convert(this::toDTO);
    }

    @Override
    public List<SystemPromptDTO> getEnabledPrompts() {
        // 先尝试从缓存获取
        List<SystemPromptDTO> cached = aiConfigCacheService.getCachedEnabledPrompts();
        if (cached != null) {
            return cached;
        }
        
        // 缓存未命中，从数据库查询
        LambdaQueryWrapper<AiSystemPrompt> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiSystemPrompt::getEnabled, true);
        wrapper.orderByDesc(AiSystemPrompt::getCreatedAt);
        
        List<AiSystemPrompt> prompts = this.list(wrapper);
        List<SystemPromptDTO> result = prompts.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        
        // 写入缓存
        aiConfigCacheService.cacheEnabledPrompts(result);
        
        return result;
    }

    @Override
    public SystemPromptDTO toDTO(AiSystemPrompt entity) {
        SystemPromptDTO dto = new SystemPromptDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}

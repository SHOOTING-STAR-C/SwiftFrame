package com.star.swiftAi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.swiftAi.dto.ProviderDTO;
import com.star.swiftAi.dto.TestResultDTO;
import com.star.swiftAi.entity.AiProvider;
import com.star.swiftAi.mapper.postgresql.AiProviderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * AI供应商服务
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiProviderService extends ServiceImpl<AiProviderMapper, AiProvider> {

    /**
     * 创建供应商
     *
     * @param provider 供应商信息
     * @return 创建的供应商
     */
    @Transactional(rollbackFor = Exception.class)
    public AiProvider createProvider(AiProvider provider) {
        // 检查供应商代码是否已存在
        LambdaQueryWrapper<AiProvider> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiProvider::getProviderCode, provider.getProviderCode());
        if (this.count(wrapper) > 0) {
            throw new RuntimeException("供应商代码已存在");
        }
        
        this.save(provider);
        log.info("创建供应商成功: {}", provider.getProviderCode());
        return provider;
    }

    /**
     * 更新供应商
     *
     * @param id       供应商ID
     * @param provider 供应商信息
     * @return 更新后的供应商
     */
    @Transactional(rollbackFor = Exception.class)
    public AiProvider updateProvider(Long id, AiProvider provider) {
        AiProvider existing = this.getById(id);
        if (existing == null) {
            throw new RuntimeException("供应商不存在");
        }
        
        // 检查供应商代码是否被其他供应商使用
        if (!existing.getProviderCode().equals(provider.getProviderCode())) {
            LambdaQueryWrapper<AiProvider> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AiProvider::getProviderCode, provider.getProviderCode());
            wrapper.ne(AiProvider::getId, id);
            if (this.count(wrapper) > 0) {
                throw new RuntimeException("供应商代码已存在");
            }
        }
        
        provider.setId(id);
        this.updateById(provider);
        log.info("更新供应商成功: {}", provider.getProviderCode());
        return provider;
    }

    /**
     * 删除供应商
     *
     * @param id 供应商ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteProvider(Long id) {
        AiProvider provider = this.getById(id);
        if (provider == null) {
            throw new RuntimeException("供应商不存在");
        }
        
        this.removeById(id);
        log.info("删除供应商成功: {}", provider.getProviderCode());
    }

    /**
     * 获取供应商详情
     *
     * @param id 供应商ID
     * @return 供应商DTO
     */
    public ProviderDTO getProviderById(Long id) {
        AiProvider provider = this.getById(id);
        if (provider == null) {
            throw new RuntimeException("供应商不存在");
        }
        
        ProviderDTO dto = new ProviderDTO();
        BeanUtils.copyProperties(provider, dto);
        dto.setHealthy(true); // 默认健康状态，实际应该通过健康检查获取
        return dto;
    }

    /**
     * 获取供应商列表（分页）
     *
     * @param page    页码
     * @param size    每页大小
     * @param enabled 是否启用
     * @return 供应商列表
     */
    public IPage<ProviderDTO> getProviders(Integer page, Integer size, Boolean enabled) {
        Page<AiProvider> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<AiProvider> wrapper = new LambdaQueryWrapper<>();
        
        if (enabled != null) {
            wrapper.eq(AiProvider::getEnabled, enabled);
        }
        
        wrapper.orderByAsc(AiProvider::getPriority);
        
        IPage<AiProvider> providerPage = this.page(pageParam, wrapper);
        
        // 转换为DTO
        return providerPage.convert(provider -> {
            ProviderDTO dto = new ProviderDTO();
            BeanUtils.copyProperties(provider, dto);
            dto.setHealthy(true); // 默认健康状态
            return dto;
        });
    }

    /**
     * 测试供应商连接
     *
     * @param id 供应商ID
     * @return 测试结果
     */
    public TestResultDTO testConnection(Long id) {
        AiProvider provider = this.getById(id);
        if (provider == null) {
            throw new RuntimeException("供应商不存在");
        }
        
        // TODO: 实现实际的连接测试逻辑
        // 这里只是模拟测试
        long startTime = System.currentTimeMillis();
        try {
            // 模拟网络延迟
            Thread.sleep(100);
            long latency = System.currentTimeMillis() - startTime;
            
            return new TestResultDTO(true, "连接成功", latency);
        } catch (InterruptedException e) {
            return new TestResultDTO(false, "连接失败: " + e.getMessage(), 0);
        }
    }
}

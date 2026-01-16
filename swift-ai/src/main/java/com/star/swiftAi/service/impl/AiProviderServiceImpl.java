package com.star.swiftAi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.swiftAi.cache.AiConfigCacheService;
import com.star.swiftAi.core.factory.ProviderFactory;
import com.star.swiftAi.core.model.ProviderMetaData;
import com.star.swiftAi.core.provider.AbstractProvider;
import com.star.swiftAi.dto.ProviderDTO;
import com.star.swiftAi.dto.TestResultDTO;
import com.star.swiftAi.entity.AiProvider;
import com.star.swiftAi.mapper.postgresql.AiProviderMapper;
import com.star.swiftAi.service.AiProviderService;
import com.star.swiftAi.util.ApiKeyCryptoUtil;
import com.star.swiftCommon.domain.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * AI供应商服务实现
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiProviderServiceImpl extends ServiceImpl<AiProviderMapper, AiProvider> 
        implements AiProviderService {

    private final ApiKeyCryptoUtil apiKeyCryptoUtil;
    private final AiConfigCacheService aiConfigCacheService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiProvider createProvider(AiProvider provider) {
        // 验证提供商类型是否已注册
        if (!ProviderFactory.isProviderRegistered(provider.getProviderCode())) {
            throw new RuntimeException("未知的提供商类型: " + provider.getProviderCode());
        }
        
        // 前端传递的密钥已加密，直接存储
        this.save(provider);
        
        // 清除启用供应商列表缓存
        aiConfigCacheService.removeEnabledProvidersCache();
        
        log.info("创建供应商成功: {}", provider.getProviderName());
        return provider;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiProvider updateProvider(Long id, AiProvider provider) {
        AiProvider existing = this.getById(id);
        if (existing == null) {
            throw new RuntimeException("供应商不存在");
        }
        
        // 验证提供商类型是否已注册
        if (!ProviderFactory.isProviderRegistered(provider.getProviderCode())) {
            throw new RuntimeException("未知的提供商类型: " + provider.getProviderCode());
        }
        
        // 前端传递的密钥已加密，直接存储
        provider.setId(id);
        this.updateById(provider);
        
        // 更新缓存
        ProviderDTO dto = new ProviderDTO();
        BeanUtils.copyProperties(provider, dto);
        dto.setHealthy(true);
        aiConfigCacheService.cacheProvider(id, dto);
        // 清除启用供应商列表缓存
        aiConfigCacheService.removeEnabledProvidersCache();
        
        log.info("更新供应商成功: {}", provider.getProviderName());
        return provider;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProvider(Long id) {
        AiProvider provider = this.getById(id);
        if (provider == null) {
            throw new RuntimeException("供应商不存在");
        }
        
        this.removeById(id);
        
        // 删除缓存
        aiConfigCacheService.removeProviderCache(id);
        // 清除启用供应商列表缓存
        aiConfigCacheService.removeEnabledProvidersCache();
        
        log.info("删除供应商成功: {}", provider.getProviderName());
    }

    @Override
    public ProviderDTO getProviderById(Long id) {
        // 先尝试从缓存获取
        ProviderDTO cached = aiConfigCacheService.getCachedProvider(id);
        if (cached != null) {
            return cached;
        }
        
        // 缓存未命中，从数据库查询
        AiProvider provider = this.getById(id);
        if (provider == null) {
            throw new RuntimeException("供应商不存在");
        }
        
        // 直接返回加密的密钥，不解密
        ProviderDTO dto = new ProviderDTO();
        BeanUtils.copyProperties(provider, dto);
        dto.setHealthy(true); // 默认健康状态，实际应该通过健康检查获取
        
        // 写入缓存
        aiConfigCacheService.cacheProvider(id, dto);
        
        return dto;
    }

    @Override
    public PageResult<ProviderDTO> getProviders(Integer page, Integer size, Boolean enabled) {
        // 如果查询所有启用的供应商且不分页（page=1, size很大），尝试使用缓存
        if (Boolean.TRUE.equals(enabled) && page == 1 && size >= 1000) {
            List<ProviderDTO> cached = aiConfigCacheService.getCachedEnabledProviders();
            if (cached != null) {
                return PageResult.success(cached, (long) cached.size(), 1L, size);
            }
        }
        
        Page<AiProvider> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<AiProvider> wrapper = new LambdaQueryWrapper<>();
        
        if (enabled != null) {
            wrapper.eq(AiProvider::getEnabled, enabled);
        }
        
        wrapper.orderByAsc(AiProvider::getPriority);
        
        IPage<AiProvider> providerPage = this.page(pageParam, wrapper);
        
        // 转换为DTO，不解密API密钥
        IPage<ProviderDTO> dtoPage = providerPage.convert(provider -> {
            ProviderDTO dto = new ProviderDTO();
            BeanUtils.copyProperties(provider, dto);
            dto.setHealthy(true); // 默认健康状态
            return dto;
        });
        
        // 如果查询所有启用的供应商，缓存结果
        if (Boolean.TRUE.equals(enabled) && page == 1) {
            aiConfigCacheService.cacheEnabledProviders(dtoPage.getRecords());
        }
        
        // 转换为 PageResult
        return PageResult.success(dtoPage.getRecords(), dtoPage.getTotal(), dtoPage.getCurrent(), dtoPage.getSize());
    }

    @Override
    public TestResultDTO testConnection(Long id) {
        AiProvider provider = this.getById(id);
        if (provider == null) {
            throw new RuntimeException("供应商不存在");
        }
        
        long startTime = System.currentTimeMillis();
        try {
            // 解密API密钥
            String decryptedApiKey = apiKeyCryptoUtil.decryptApiKeyString(provider.getApiKey());
            
            // 构建配置Map（只包含连接配置）
            Map<String, Object> providerConfig = Map.of(
                "api_key", decryptedApiKey != null ? decryptedApiKey : "",
                "base_url", provider.getBaseUrl() != null ? provider.getBaseUrl() : "",
                "timeout", provider.getTimeout() != null ? provider.getTimeout() : 60,
                "max_retries", provider.getMaxRetries() != null ? provider.getMaxRetries() : 3
            );
            
            // 创建Provider实例（不需要providerSettings）
            AbstractProvider abstractProvider = ProviderFactory.createProvider(
                provider.getProviderCode(),
                providerConfig,
                Map.of() // 空的settings，测试连接不需要模型参数
            );
            
            // 测试连接（不指定模型，会自动使用第一个可用模型）
            abstractProvider.test(null);
            
            long latency = System.currentTimeMillis() - startTime;
            log.info("供应商连接测试成功: {}, 耗时: {}ms", provider.getProviderName(), latency);
            
            return new TestResultDTO(true, "连接成功", latency);
        } catch (Exception e) {
            long latency = System.currentTimeMillis() - startTime;
            log.error("供应商连接测试失败: {}", provider.getProviderName(), e);
            return new TestResultDTO(false, "连接失败: " + e.getMessage(), latency);
        }
    }
    
    @Override
    public List<ProviderMetaData> getRegisteredProviders() {
        return ProviderFactory.getRegisteredProviders();
    }
    
    @Override
    public ProviderMetaData getProviderMetadata(String typeName) {
        return ProviderFactory.getRegisteredProviders().stream()
            .filter(meta -> meta.getType().equals(typeName))
            .findFirst()
            .orElse(null);
    }
}

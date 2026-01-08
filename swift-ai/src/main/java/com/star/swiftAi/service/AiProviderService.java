package com.star.swiftAi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.star.swiftAi.core.factory.ProviderFactory;
import com.star.swiftAi.core.model.ProviderMetaData;
import com.star.swiftAi.core.provider.AbstractProvider;
import com.star.swiftAi.dto.ProviderDTO;
import com.star.swiftAi.dto.TestResultDTO;
import com.star.swiftAi.entity.AiProvider;
import com.star.swiftAi.mapper.postgresql.AiProviderMapper;
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
 * AI供应商服务
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiProviderService extends ServiceImpl<AiProviderMapper, AiProvider> {

    private final ObjectMapper objectMapper;
    private final ApiKeyCryptoUtil apiKeyCryptoUtil;

    /**
     * 创建供应商
     *
     * @param provider 供应商信息
     * @return 创建的供应商
     */
    @Transactional(rollbackFor = Exception.class)
    public AiProvider createProvider(AiProvider provider) {
        // 验证提供商类型是否已注册
        if (!ProviderFactory.isProviderRegistered(provider.getProviderCode())) {
            throw new RuntimeException("未知的提供商类型: " + provider.getProviderCode());
        }
        
        // 前端传递的密钥已加密，直接存储
        this.save(provider);
        log.info("创建供应商成功: {}", provider.getProviderName());
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
        
        // 验证提供商类型是否已注册
        if (!ProviderFactory.isProviderRegistered(provider.getProviderCode())) {
            throw new RuntimeException("未知的提供商类型: " + provider.getProviderCode());
        }
        
        // 前端传递的密钥已加密，直接存储
        provider.setId(id);
        this.updateById(provider);
        log.info("更新供应商成功: {}", provider.getProviderName());
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
        log.info("删除供应商成功: {}", provider.getProviderName());
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
        
        // 直接返回加密的密钥，不解密
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
    public PageResult<ProviderDTO> getProviders(Integer page, Integer size, Boolean enabled) {
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
        
        // 转换为 PageResult
        return PageResult.success(dtoPage.getRecords(), dtoPage.getTotal(), dtoPage.getCurrent(), dtoPage.getSize());
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
        
        long startTime = System.currentTimeMillis();
        try {
            // 解密API密钥
            String decryptedApiKey = provider.getApiKey();
            if (decryptedApiKey != null) {
                decryptedApiKey = apiKeyCryptoUtil.decryptApiKey(decryptedApiKey);
            }
            
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
            
            // 测试连接
            abstractProvider.test();
            
            long latency = System.currentTimeMillis() - startTime;
            log.info("供应商连接测试成功: {}, 耗时: {}ms", provider.getProviderName(), latency);
            
            return new TestResultDTO(true, "连接成功", latency);
        } catch (Exception e) {
            long latency = System.currentTimeMillis() - startTime;
            log.error("供应商连接测试失败: {}", provider.getProviderName(), e);
            return new TestResultDTO(false, "连接失败: " + e.getMessage(), latency);
        }
    }
    
    /**
     * 获取所有已注册的提供商类型
     *
     * @return 提供商元数据列表
     */
    public List<ProviderMetaData> getRegisteredProviders() {
        return ProviderFactory.getRegisteredProviders();
    }
    
    /**
     * 根据类型获取提供商元数据
     *
     * @param typeName 提供商类型名称
     * @return 提供商元数据
     */
    public ProviderMetaData getProviderMetadata(String typeName) {
        return ProviderFactory.getRegisteredProviders().stream()
            .filter(meta -> meta.getType().equals(typeName))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * 解析JSON配置
     *
     * @param jsonConfig JSON配置字符串
     * @return 配置Map
     */
    private Map<String, Object> parseJsonConfig(String jsonConfig) {
        try {
            if (jsonConfig == null || jsonConfig.isEmpty()) {
                return Map.of();
            }
            return objectMapper.readValue(jsonConfig, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("解析JSON配置失败", e);
            return Map.of();
        }
    }
}

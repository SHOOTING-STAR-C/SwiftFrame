package com.star.swiftAi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.swiftAi.cache.AiConfigCacheService;
import com.star.swiftAi.core.factory.ProviderFactory;
import com.star.swiftAi.core.provider.Provider;
import com.star.swiftAi.core.response.ModelsResponse;
import com.star.swiftAi.dto.ModelDTO;
import com.star.swiftAi.dto.TestResultDTO;
import com.star.swiftAi.entity.AiModel;
import com.star.swiftAi.entity.AiProvider;
import com.star.swiftAi.mapper.postgresql.AiModelMapper;
import com.star.swiftAi.service.AiModelService;
import com.star.swiftAi.service.AiProviderService;
import com.star.swiftAi.util.ApiKeyCryptoUtil;
import com.star.swiftCommon.domain.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * AI模型服务实现
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiModelServiceImpl extends ServiceImpl<AiModelMapper, AiModel> 
        implements AiModelService {

    private final AiProviderService aiProviderService;
    private final ApiKeyCryptoUtil apiKeyCryptoUtil;
    private final AiConfigCacheService aiConfigCacheService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiModel createModel(AiModel model) {
        // 检查供应商是否存在
        AiProvider provider = aiProviderService.getById(model.getProviderId());
        if (provider == null) {
            throw new RuntimeException("供应商不存在");
        }
        
        // 检查模型代码是否已存在
        LambdaQueryWrapper<AiModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiModel::getModelCode, model.getModelCode());
        if (this.count(wrapper) > 0) {
            throw new RuntimeException("模型代码已存在");
        }
        
        this.save(model);
        
        // 清除启用模型列表缓存
        aiConfigCacheService.removeEnabledModelsCache();
        
        log.info("创建模型成功: {}", model.getModelCode());
        return model;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiModel updateModel(Long id, AiModel model) {
        AiModel existing = this.getById(id);
        if (existing == null) {
            throw new RuntimeException("模型不存在");
        }
        
        // 检查供应商是否存在
        AiProvider provider = aiProviderService.getById(model.getProviderId());
        if (provider == null) {
            throw new RuntimeException("供应商不存在");
        }
        
        // 检查模型代码是否被其他模型使用
        if (!existing.getModelCode().equals(model.getModelCode())) {
            LambdaQueryWrapper<AiModel> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AiModel::getModelCode, model.getModelCode());
            wrapper.ne(AiModel::getId, id);
            if (this.count(wrapper) > 0) {
                throw new RuntimeException("模型代码已存在");
            }
        }
        
        model.setId(id);
        this.updateById(model);
        
        // 更新缓存
        ModelDTO dto = new ModelDTO();
        BeanUtils.copyProperties(model, dto);
        dto.setProviderName(provider.getProviderName());
        aiConfigCacheService.cacheModel(id, dto);
        // 清除启用模型列表缓存
        aiConfigCacheService.removeEnabledModelsCache();
        
        log.info("更新模型成功: {}", model.getModelCode());
        return model;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteModel(Long id) {
        AiModel model = this.getById(id);
        if (model == null) {
            throw new RuntimeException("模型不存在");
        }
        
        this.removeById(id);
        
        // 删除缓存
        aiConfigCacheService.removeModelCache(id);
        // 清除启用模型列表缓存
        aiConfigCacheService.removeEnabledModelsCache();
        
        log.info("删除模型成功: {}", model.getModelCode());
    }

    @Override
    public ModelDTO getModelById(Long id) {
        // 先尝试从缓存获取
        ModelDTO cached = aiConfigCacheService.getCachedModel(id);
        if (cached != null) {
            return cached;
        }
        
        // 缓存未命中，从数据库查询
        AiModel model = this.getById(id);
        if (model == null) {
            throw new RuntimeException("模型不存在");
        }
        
        ModelDTO dto = new ModelDTO();
        BeanUtils.copyProperties(model, dto);
        
        // 获取供应商名称
        AiProvider provider = aiProviderService.getById(model.getProviderId());
        if (provider != null) {
            dto.setProviderName(provider.getProviderName());
        }
        
        // 写入缓存
        aiConfigCacheService.cacheModel(id, dto);
        
        return dto;
    }

    @Override
    public PageResult<ModelDTO> getModels(Integer page, Integer size, Long providerId, Boolean enabled) {
        // 如果查询所有启用的模型且不分页（page=1, size很大），尝试使用缓存
        if (Boolean.TRUE.equals(enabled) && providerId == null && page == 1 && size >= 1000) {
            List<ModelDTO> cached = aiConfigCacheService.getCachedEnabledModels();
            if (cached != null) {
                return PageResult.success(cached, (long) cached.size(), 1L, size);
            }
        }
        
        Page<AiModel> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<AiModel> wrapper = new LambdaQueryWrapper<>();
        
        if (providerId != null) {
            wrapper.eq(AiModel::getProviderId, providerId);
        }
        
        if (enabled != null) {
            wrapper.eq(AiModel::getEnabled, enabled);
        }
        
        wrapper.orderByDesc(AiModel::getCreatedAt);
        
        IPage<AiModel> modelPage = this.page(pageParam, wrapper);
        
        // 转换为DTO
        IPage<ModelDTO> dtoPage = modelPage.convert(model -> {
            ModelDTO dto = new ModelDTO();
            BeanUtils.copyProperties(model, dto);
            
            // 获取供应商名称
            AiProvider provider = aiProviderService.getById(model.getProviderId());
            if (provider != null) {
                dto.setProviderName(provider.getProviderName());
            }
            
            return dto;
        });
        
        // 如果查询所有启用的模型，缓存结果
        if (Boolean.TRUE.equals(enabled) && providerId == null && page == 1) {
            aiConfigCacheService.cacheEnabledModels(dtoPage.getRecords());
        }
        
        // 转换为 PageResult
        return PageResult.success(dtoPage.getRecords(), dtoPage.getTotal(), dtoPage.getCurrent(), dtoPage.getSize());
    }

    @Override
    public ModelsResponse getModelsFromProvider(Long providerId) {
        AiProvider provider = aiProviderService.getById(providerId);
        if (provider == null) {
            throw new RuntimeException("供应商不存在");
        }
        
        if (!provider.getEnabled()) {
            throw new RuntimeException("供应商未启用");
        }
        
        try {
            // 获取解密后的API密钥
            String decryptedApiKey = apiKeyCryptoUtil.decryptApiKeyString(provider.getApiKey());
            
            // 创建Provider配置
            java.util.Map<String, Object> providerConfig = new java.util.HashMap<>();
            providerConfig.put("api_key", decryptedApiKey);
            providerConfig.put("base_url", provider.getBaseUrl());
            providerConfig.put("timeout", 30);
            
            // 创建Provider实例
            Provider aiProvider = ProviderFactory.createProvider(
                provider.getProviderCode(),
                providerConfig,
                new java.util.HashMap<>()
            );
            
            // 获取模型列表
            java.util.List<String> modelIds = aiProvider.getModels();
            
            // 转换为ModelsResponse格式
            ModelsResponse modelsResponse = new ModelsResponse();
            java.util.List<ModelsResponse.Model> modelData = new java.util.ArrayList<>();
            for (String modelId : modelIds) {
                ModelsResponse.Model modelInfo = new ModelsResponse.Model();
                modelInfo.setId(modelId);
                modelInfo.setObject("model");
                modelData.add(modelInfo);
            }
            modelsResponse.setData(modelData);
            
            log.info("从供应商获取模型成功: providerCode={}, modelCount={}", 
                    provider.getProviderCode(), modelIds.size());
            
            return modelsResponse;
        } catch (Exception e) {
            log.error("从供应商获取模型失败: providerCode={}, error={}", 
                    provider.getProviderCode(), e.getMessage());
            throw new RuntimeException("获取模型失败: " + e.getMessage());
        }
    }

    @Override
    public TestResultDTO testModel(Long id) {
        AiModel model = this.getById(id);
        if (model == null) {
            throw new RuntimeException("模型不存在");
        }
        
        AiProvider provider = aiProviderService.getById(model.getProviderId());
        if (provider == null) {
            throw new RuntimeException("供应商不存在");
        }
        
        long startTime = System.currentTimeMillis();
        try {
            // 获取解密后的API密钥
            String decryptedApiKey = apiKeyCryptoUtil.decryptApiKeyString(provider.getApiKey());
            
            // 创建Provider配置
            java.util.Map<String, Object> providerConfig = new java.util.HashMap<>();
            providerConfig.put("api_key", decryptedApiKey);
            providerConfig.put("base_url", provider.getBaseUrl());
            providerConfig.put("timeout", 10);
            
            // 创建Provider实例
            Provider aiProvider = ProviderFactory.createProvider(
                provider.getProviderCode(),
                providerConfig,
                new java.util.HashMap<>()
            );
            
            // 测试连接，使用指定的模型
            aiProvider.test(model.getModelCode());
            long latency = System.currentTimeMillis() - startTime;
            
            return new TestResultDTO(true, "连接成功", latency);
        } catch (Exception e) {
            long latency = System.currentTimeMillis() - startTime;
            log.error("测试模型连接失败: modelCode={}, error={}", model.getModelCode(), e.getMessage());
            return new TestResultDTO(false, "连接失败: " + e.getMessage(), latency);
        }
    }
}

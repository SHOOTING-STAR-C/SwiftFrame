package com.star.swiftAi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.swiftAi.dto.ModelDTO;
import com.star.swiftAi.dto.TestResultDTO;
import com.star.swiftAi.entity.AiModel;
import com.star.swiftAi.entity.AiProvider;
import com.star.swiftAi.enums.AiProviderEnum;
import com.star.swiftAi.mapper.postgresql.AiModelMapper;
import com.star.swiftCommon.domain.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AI模型服务
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiModelService extends ServiceImpl<AiModelMapper, AiModel> {

    private final AiProviderService aiProviderService;

    /**
     * 创建模型
     *
     * @param model 模型信息
     * @return 创建的模型
     */
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
        log.info("创建模型成功: {}", model.getModelCode());
        return model;
    }

    /**
     * 更新模型
     *
     * @param id    模型ID
     * @param model 模型信息
     * @return 更新后的模型
     */
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
        log.info("更新模型成功: {}", model.getModelCode());
        return model;
    }

    /**
     * 删除模型
     *
     * @param id 模型ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteModel(Long id) {
        AiModel model = this.getById(id);
        if (model == null) {
            throw new RuntimeException("模型不存在");
        }
        
        this.removeById(id);
        log.info("删除模型成功: {}", model.getModelCode());
    }

    /**
     * 获取模型详情
     *
     * @param id 模型ID
     * @return 模型DTO
     */
    public ModelDTO getModelById(Long id) {
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
        
        return dto;
    }

    /**
     * 获取模型列表（分页）
     *
     * @param page       页码
     * @param size       每页大小
     * @param providerId 供应商ID
     * @param enabled    是否启用
     * @return 模型列表
     */
    public PageResult<ModelDTO> getModels(Integer page, Integer size, Long providerId, Boolean enabled) {
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
        
        // 转换为 PageResult
        return PageResult.success(dtoPage.getRecords(), dtoPage.getTotal(), dtoPage.getCurrent(), dtoPage.getSize());
    }

    /**
     * 测试模型连接
     *
     * @param id 模型ID
     * @return 测试结果
     */
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
            // 创建OpenAI兼容客户端
            com.star.swiftAi.client.OpenAiCompatibleClient.Config clientConfig = 
                com.star.swiftAi.client.OpenAiCompatibleClient.Config.builder()
                    .apiKey(provider.getApiKey())
                    .baseUrl(provider.getBaseUrl())
                    .model(model.getModelCode())
                    .provider(AiProviderEnum.fromCode(provider.getProviderCode()))
                    .timeout(java.time.Duration.ofSeconds(10))
                    .build();
            
            com.star.swiftAi.client.OpenAiCompatibleClient client = 
                new com.star.swiftAi.client.OpenAiCompatibleClient(clientConfig);
            
            // 测试连接
            boolean isAvailable = client.isAvailable();
            long latency = System.currentTimeMillis() - startTime;
            
            if (isAvailable) {
                return new TestResultDTO(true, "连接成功", latency);
            } else {
                return new TestResultDTO(false, "连接失败：服务不可用", latency);
            }
        } catch (Exception e) {
            long latency = System.currentTimeMillis() - startTime;
            log.error("测试模型连接失败: modelCode={}, error={}", model.getModelCode(), e.getMessage());
            return new TestResultDTO(false, "连接失败: " + e.getMessage(), latency);
        }
    }
}

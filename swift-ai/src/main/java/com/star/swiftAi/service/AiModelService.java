package com.star.swiftAi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.swiftAi.core.response.ModelsResponse;
import com.star.swiftAi.dto.ModelDTO;
import com.star.swiftAi.dto.TestResultDTO;
import com.star.swiftAi.entity.AiModel;
import com.star.swiftCommon.domain.PageResult;

/**
 * AI模型服务接口
 *
 * @author SHOOTING_STAR_C
 */
public interface AiModelService extends IService<AiModel> {

    /**
     * 创建模型
     *
     * @param model 模型信息
     * @return 创建的模型
     */
    AiModel createModel(AiModel model);

    /**
     * 更新模型
     *
     * @param id    模型ID
     * @param model 模型信息
     * @return 更新后的模型
     */
    AiModel updateModel(Long id, AiModel model);

    /**
     * 删除模型
     *
     * @param id 模型ID
     */
    void deleteModel(Long id);

    /**
     * 获取模型详情
     *
     * @param id 模型ID
     * @return 模型DTO
     */
    ModelDTO getModelById(Long id);

    /**
     * 获取模型列表（分页）
     *
     * @param page       页码
     * @param size       每页大小
     * @param providerId 供应商ID
     * @param enabled    是否启用
     * @return 模型列表
     */
    PageResult<ModelDTO> getModels(Integer page, Integer size, Long providerId, Boolean enabled);

    /**
     * 从供应商获取所有模型
     *
     * @param providerId 供应商ID
     * @return 模型列表
     */
    ModelsResponse getModelsFromProvider(Long providerId);

    /**
     * 测试模型连接
     *
     * @param id 模型ID
     * @return 测试结果
     */
    TestResultDTO testModel(Long id);
}

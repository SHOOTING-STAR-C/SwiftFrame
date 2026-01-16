package com.star.swiftAi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.swiftAi.core.model.ProviderMetaData;
import com.star.swiftAi.dto.ProviderDTO;
import com.star.swiftAi.dto.TestResultDTO;
import com.star.swiftAi.entity.AiProvider;
import com.star.swiftCommon.domain.PageResult;

import java.util.List;

/**
 * AI供应商服务接口
 *
 * @author SHOOTING_STAR_C
 */
public interface AiProviderService extends IService<AiProvider> {

    /**
     * 创建供应商
     *
     * @param provider 供应商信息
     * @return 创建的供应商
     */
    AiProvider createProvider(AiProvider provider);

    /**
     * 更新供应商
     *
     * @param id       供应商ID
     * @param provider 供应商信息
     * @return 更新后的供应商
     */
    AiProvider updateProvider(Long id, AiProvider provider);

    /**
     * 删除供应商
     *
     * @param id 供应商ID
     */
    void deleteProvider(Long id);

    /**
     * 获取供应商详情
     *
     * @param id 供应商ID
     * @return 供应商DTO
     */
    ProviderDTO getProviderById(Long id);

    /**
     * 获取供应商列表（分页）
     *
     * @param page    页码
     * @param size    每页大小
     * @param enabled 是否启用
     * @return 供应商列表
     */
    PageResult<ProviderDTO> getProviders(Integer page, Integer size, Boolean enabled);

    /**
     * 测试供应商连接
     *
     * @param id 供应商ID
     * @return 测试结果
     */
    TestResultDTO testConnection(Long id);
    
    /**
     * 获取所有已注册的提供商类型
     *
     * @return 提供商元数据列表
     */
    List<ProviderMetaData> getRegisteredProviders();
    
    /**
     * 根据类型获取提供商元数据
     *
     * @param typeName 提供商类型名称
     * @return 提供商元数据
     */
    ProviderMetaData getProviderMetadata(String typeName);
}

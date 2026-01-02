package com.star.swiftConfig.service;

import com.star.swiftConfig.domain.SysConfig;

import java.util.List;
import java.util.Map;

/**
 * 系统配置服务接口
 * 提供配置的存储和读取功能
 * 支持通过标记控制是否加密：
 * - DEC(value): 存储时加密value
 * - ENC(value): 读取时解密value
 * - 无标记: 明文存储和读取
 *
 * @author SHOOTING_STAR_C
 */
public interface SysConfigService {

    /**
     * 根据配置键获取配置值
     * 如果值以ENC()包裹，会自动解密
     *
     * @param configKey 配置键
     * @return 配置值（已解密或明文）
     */
    String getConfigValue(String configKey);

    /**
     * 根据配置键获取配置对象
     * 如果值以ENC()包裹，会自动解密
     *
     * @param configKey 配置键
     * @return 配置对象
     */
    SysConfig getConfig(String configKey);

    /**
     * 根据配置ID获取配置对象
     * 如果值以ENC()包裹，会自动解密
     *
     * @param configId 配置ID
     * @return 配置对象
     */
    SysConfig getConfigById(Long configId);

    /**
     * 根据配置类型获取所有启用的配置
     * 如果值以ENC()包裹，会自动解密
     *
     * @param configType 配置类型
     * @return 配置列表
     */
    List<SysConfig> getConfigsByType(String configType);

    /**
     * 获取所有启用的配置
     * 如果值以ENC()包裹，会自动解密
     *
     * @return 配置列表
     */
    List<SysConfig> getAllEnabledConfigs();

    /**
     * 获取所有配置
     * 如果值以ENC()包裹，会自动解密
     *
     * @return 配置列表
     */
    List<SysConfig> getAllConfigs();

    /**
     * 保存配置
     * 如果值以DEC()包裹，会自动加密后存储
     *
     * @param config 配置对象（configValue可以是明文或DEC(明文)）
     * @return 保存后的配置对象
     */
    SysConfig saveConfig(SysConfig config);

    /**
     * 更新配置
     * 如果值以DEC()包裹，会自动加密后存储
     *
     * @param config 配置对象（configValue可以是明文或DEC(明文)）
     * @return 更新后的配置对象
     */
    SysConfig updateConfig(SysConfig config);

    /**
     * 根据配置键更新配置值
     * 如果值以DEC()包裹，会自动加密后存储
     *
     * @param configKey  配置键
     * @param configValue 配置值（可以是明文或DEC(明文)）
     * @param updatedBy  更新人
     * @return 是否成功
     */
    boolean updateConfigValue(String configKey, String configValue, String updatedBy);

    /**
     * 删除配置
     *
     * @param configId 配置ID
     * @return 是否成功
     */
    boolean deleteConfig(Long configId);

    /**
     * 根据配置键删除配置
     *
     * @param configKey 配置键
     * @return 是否成功
     */
    boolean deleteConfigByKey(String configKey);

    /**
     * 启用或禁用配置
     *
     * @param configId 配置ID
     * @param enabled  是否启用
     * @param updatedBy 更新人
     * @return 是否成功
     */
    boolean toggleConfig(Long configId, Boolean enabled, String updatedBy);

    /**
     * 检查配置键是否存在
     *
     * @param configKey 配置键
     * @return 是否存在
     */
    boolean existsConfigKey(String configKey);

    /**
     * 根据配置类型获取配置键值对Map
     * 如果值以ENC()包裹，会自动解密
     *
     * @param configType 配置类型
     * @return 配置键值对Map
     */
    Map<String, String> getConfigMapByType(String configType);

    /**
     * 获取所有启用的配置键值对Map
     * 如果值以ENC()包裹，会自动解密
     *
     * @return 配置键值对Map
     */
    Map<String, String> getAllEnabledConfigMap();
}

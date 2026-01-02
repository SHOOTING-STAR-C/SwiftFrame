package com.star.swiftConfig.mapper.postgresql;

import com.star.swiftConfig.domain.SysConfig;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 系统配置Mapper接口
 * 用于操作sys_config表，支持配置的增删改查
 * 所有配置值在存储前都会进行AES加密处理
 *
 * @author SHOOTING_STAR_C
 */
@Mapper
public interface SysConfigMapper {

    /**
     * 根据配置ID查找配置
     *
     * @param configId 配置ID
     * @return 配置对象
     */
    @Select("SELECT * FROM sys_config WHERE config_id = #{configId}")
    SysConfig findById(@Param("configId") Long configId);

    /**
     * 根据配置键查找配置
     *
     * @param configKey 配置键
     * @return 配置对象
     */
    @Select("SELECT * FROM sys_config WHERE config_key = #{configKey}")
    SysConfig findByConfigKey(@Param("configKey") String configKey);

    /**
     * 检查配置键是否存在
     *
     * @param configKey 配置键
     * @return 是否存在
     */
    @Select("SELECT COUNT(1) FROM sys_config WHERE config_key = #{configKey}")
    boolean existsByConfigKey(@Param("configKey") String configKey);

    /**
     * 根据配置类型查找所有启用的配置
     *
     * @param configType 配置类型
     * @return 配置列表
     */
    @Select("SELECT * FROM sys_config WHERE config_type = #{configType} AND is_enabled = true ORDER BY config_id")
    List<SysConfig> findByConfigType(@Param("configType") String configType);

    /**
     * 查找所有启用的配置
     *
     * @return 配置列表
     */
    @Select("SELECT * FROM sys_config WHERE is_enabled = true ORDER BY config_type, config_id")
    List<SysConfig> findAllEnabled();

    /**
     * 查找所有配置
     *
     * @return 配置列表
     */
    @Select("SELECT * FROM sys_config ORDER BY config_type, config_id")
    List<SysConfig> findAll();

    /**
     * 保存配置（新增）
     *
     * @param config 配置对象
     * @return 影响行数
     */
    @Insert("INSERT INTO sys_config(config_key, config_value, config_type, description, is_enabled, " +
            "created_at, updated_at, created_by, updated_by) " +
            "VALUES(#{configKey}, #{configValue}, #{configType}, #{description}, #{isEnabled}, " +
            "#{createdAt}, #{updatedAt}, #{createdBy}, #{updatedBy})")
    @Options(useGeneratedKeys = true, keyProperty = "configId")
    int insert(SysConfig config);

    /**
     * 更新配置
     *
     * @param config 配置对象
     * @return 影响行数
     */
    @Update("UPDATE sys_config SET config_key = #{configKey}, config_value = #{configValue}, " +
            "config_type = #{configType}, description = #{description}, is_enabled = #{isEnabled}, " +
            "updated_at = #{updatedAt}, updated_by = #{updatedBy} WHERE config_id = #{configId}")
    int update(SysConfig config);

    /**
     * 根据配置键更新配置值
     *
     * @param configKey  配置键
     * @param configValue 配置值（加密后）
     * @param updatedBy  更新人
     * @return 影响行数
     */
    @Update("UPDATE sys_config SET config_value = #{configValue}, updated_at = CURRENT_TIMESTAMP, " +
            "updated_by = #{updatedBy} WHERE config_key = #{configKey}")
    int updateValueByConfigKey(@Param("configKey") String configKey, 
                               @Param("configValue") String configValue,
                               @Param("updatedBy") String updatedBy);

    /**
     * 删除配置
     *
     * @param configId 配置ID
     * @return 影响行数
     */
    @Delete("DELETE FROM sys_config WHERE config_id = #{configId}")
    int deleteById(@Param("configId") Long configId);

    /**
     * 根据配置键删除配置
     *
     * @param configKey 配置键
     * @return 影响行数
     */
    @Delete("DELETE FROM sys_config WHERE config_key = #{configKey}")
    int deleteByConfigKey(@Param("configKey") String configKey);

    /**
     * 启用或禁用配置
     *
     * @param configId 配置ID
     * @param enabled  是否启用
     * @param updatedBy 更新人
     * @return 影响行数
     */
    @Update("UPDATE sys_config SET is_enabled = #{enabled}, updated_at = CURRENT_TIMESTAMP, " +
            "updated_by = #{updatedBy} WHERE config_id = #{configId}")
    int updateEnabledStatus(@Param("configId") Long configId, 
                           @Param("enabled") Boolean enabled,
                           @Param("updatedBy") String updatedBy);
}

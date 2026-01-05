package com.star.swiftConfig.controller;

import com.star.swiftCommon.domain.PubResult;
import com.star.swiftConfig.domain.SysConfig;
import com.star.swiftConfig.service.SysConfigService;
import com.star.swiftSecurity.constant.AuthorityConstants;
import com.star.swiftredis.service.ConfigCacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 系统配置管理控制器
 * 提供配置的增删改查API接口
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@RestController
@RequestMapping("/api/config")
@RequiredArgsConstructor
@Tag(name = "系统配置管理", description = "系统配置的增删改查接口")
public class SysConfigController {

    private final SysConfigService sysConfigService;
    private final ConfigCacheService configCacheService;

    /**
     * 根据配置键获取配置值
     */
    @GetMapping("/value/{configKey}")
    @Operation(summary = "获取配置值", description = "根据配置键获取配置值")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.CONFIG_READ + "')")
    public PubResult<String> getConfigValue(
            @Parameter(description = "配置键") @PathVariable String configKey) {
        String value = sysConfigService.getConfigValue(configKey);
        return PubResult.success(value);
    }

    /**
     * 根据配置键获取配置对象
     */
    @GetMapping("/{configKey}")
    @Operation(summary = "获取配置", description = "根据配置键获取配置对象")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.CONFIG_READ + "')")
    public PubResult<SysConfig> getConfig(
            @Parameter(description = "配置键") @PathVariable String configKey) {
        SysConfig config = sysConfigService.getConfig(configKey);
        if (config == null) {
            return PubResult.error("配置不存在");
        }
        return PubResult.success(config);
    }

    /**
     * 根据配置ID获取配置对象
     */
    @GetMapping("/id/{configId}")
    @Operation(summary = "根据ID获取配置", description = "根据配置ID获取配置对象")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.CONFIG_READ + "')")
    public PubResult<SysConfig> getConfigById(
            @Parameter(description = "配置ID") @PathVariable Long configId) {
        SysConfig config = sysConfigService.getConfigById(configId);
        if (config == null) {
            return PubResult.error("配置不存在");
        }
        return PubResult.success(config);
    }

    /**
     * 根据配置类型获取配置列表
     */
    @GetMapping("/type/{configType}")
    @Operation(summary = "按类型获取配置", description = "根据配置类型获取配置列表")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.CONFIG_READ + "')")
    public PubResult<List<SysConfig>> getConfigsByType(
            @Parameter(description = "配置类型") @PathVariable String configType) {
        List<SysConfig> configs = sysConfigService.getConfigsByType(configType);
        return PubResult.success(configs);
    }

    /**
     * 获取所有启用的配置
     */
    @GetMapping("/enabled")
    @Operation(summary = "获取启用的配置", description = "获取所有启用的配置")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.CONFIG_READ + "')")
    public PubResult<List<SysConfig>> getAllEnabledConfigs() {
        List<SysConfig> configs = sysConfigService.getAllEnabledConfigs();
        return PubResult.success(configs);
    }

    /**
     * 获取所有配置
     */
    @GetMapping("/all")
    @Operation(summary = "获取所有配置", description = "获取所有配置")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.CONFIG_READ + "')")
    public PubResult<List<SysConfig>> getAllConfigs() {
        List<SysConfig> configs = sysConfigService.getAllConfigs();
        return PubResult.success(configs);
    }

    /**
     * 保存配置
     */
    @PostMapping
    @Operation(summary = "保存配置", description = "保存新配置")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.CONFIG_CREATE + "')")
    public PubResult<SysConfig> saveConfig(@RequestBody SysConfig config) {
        try {
            SysConfig savedConfig = sysConfigService.saveConfig(config);
            // 更新Redis缓存
            configCacheService.cacheConfig(savedConfig.getConfigKey(), savedConfig.getConfigValue());
            return PubResult.success(savedConfig);
        } catch (IllegalArgumentException e) {
            return PubResult.error(e.getMessage());
        } catch (Exception e) {
            log.error("保存配置失败", e);
            return PubResult.error("保存配置失败: " + e.getMessage());
        }
    }

    /**
     * 更新配置
     */
    @PutMapping
    @Operation(summary = "更新配置", description = "更新配置")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.CONFIG_UPDATE + "')")
    public PubResult<SysConfig> updateConfig(@RequestBody SysConfig config) {
        try {
            SysConfig updatedConfig = sysConfigService.updateConfig(config);
            // 更新Redis缓存
            configCacheService.cacheConfig(updatedConfig.getConfigKey(), updatedConfig.getConfigValue());
            return PubResult.success(updatedConfig);
        } catch (IllegalArgumentException e) {
            return PubResult.error(e.getMessage());
        } catch (Exception e) {
            log.error("更新配置失败", e);
            return PubResult.error("更新配置失败: " + e.getMessage());
        }
    }

    /**
     * 根据配置键更新配置值
     */
    @PutMapping("/value/{configKey}")
    @Operation(summary = "更新配置值", description = "根据配置键更新配置值")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.CONFIG_UPDATE + "')")
    public PubResult<Boolean> updateConfigValue(
            @Parameter(description = "配置键") @PathVariable String configKey,
            @Parameter(description = "配置值") @RequestParam String configValue,
            @Parameter(description = "更新人") @RequestParam(required = false) String updatedBy) {
        try {
            boolean success = sysConfigService.updateConfigValue(configKey, configValue, updatedBy);
            if (success) {
                // 更新Redis缓存
                configCacheService.cacheConfig(configKey, configValue);
            }
            return PubResult.success(success);
        } catch (IllegalArgumentException e) {
            return PubResult.error(e.getMessage());
        } catch (Exception e) {
            log.error("更新配置值失败", e);
            return PubResult.error("更新配置值失败: " + e.getMessage());
        }
    }

    /**
     * 删除配置
     */
    @DeleteMapping("/{configId}")
    @Operation(summary = "删除配置", description = "根据配置ID删除配置")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.CONFIG_DELETE + "')")
    public PubResult<Boolean> deleteConfig(
            @Parameter(description = "配置ID") @PathVariable Long configId) {
        try {
            SysConfig config = sysConfigService.getConfigById(configId);
            boolean success = sysConfigService.deleteConfig(configId);
            if (success && config != null) {
                // 删除Redis缓存
                configCacheService.removeCachedConfig(config.getConfigKey());
            }
            return PubResult.success(success);
        } catch (Exception e) {
            log.error("删除配置失败", e);
            return PubResult.error("删除配置失败: " + e.getMessage());
        }
    }

    /**
     * 根据配置键删除配置
     */
    @DeleteMapping("/key/{configKey}")
    @Operation(summary = "根据键删除配置", description = "根据配置键删除配置")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.CONFIG_DELETE + "')")
    public PubResult<Boolean> deleteConfigByKey(
            @Parameter(description = "配置键") @PathVariable String configKey) {
        try {
            boolean success = sysConfigService.deleteConfigByKey(configKey);
            if (success) {
                // 删除Redis缓存
                configCacheService.removeCachedConfig(configKey);
            }
            return PubResult.success(success);
        } catch (Exception e) {
            log.error("删除配置失败", e);
            return PubResult.error("删除配置失败: " + e.getMessage());
        }
    }

    /**
     * 启用或禁用配置
     */
    @PutMapping("/toggle/{configId}")
    @Operation(summary = "切换配置状态", description = "启用或禁用配置")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.CONFIG_UPDATE + "')")
    public PubResult<Boolean> toggleConfig(
            @Parameter(description = "配置ID") @PathVariable Long configId,
            @Parameter(description = "是否启用") @RequestParam Boolean enabled,
            @Parameter(description = "更新人") @RequestParam(required = false) String updatedBy) {
        try {
            boolean success = sysConfigService.toggleConfig(configId, enabled, updatedBy);
            if (success) {
                SysConfig config = sysConfigService.getConfigById(configId);
                if (config != null) {
                    if (enabled) {
                        // 启用时添加到缓存
                        configCacheService.cacheConfig(config.getConfigKey(), config.getConfigValue());
                    } else {
                        // 禁用时从缓存删除
                        configCacheService.removeCachedConfig(config.getConfigKey());
                    }
                }
            }
            return PubResult.success(success);
        } catch (Exception e) {
            log.error("切换配置状态失败", e);
            return PubResult.error("切换配置状态失败: " + e.getMessage());
        }
    }

    /**
     * 检查配置键是否存在
     */
    @GetMapping("/exists/{configKey}")
    @Operation(summary = "检查配置键是否存在", description = "检查配置键是否存在")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.CONFIG_READ + "')")
    public PubResult<Boolean> existsConfigKey(
            @Parameter(description = "配置键") @PathVariable String configKey) {
        boolean exists = sysConfigService.existsConfigKey(configKey);
        return PubResult.success(exists);
    }

    /**
     * 根据配置类型获取配置键值对Map
     */
    @GetMapping("/map/{configType}")
    @Operation(summary = "按类型获取配置Map", description = "根据配置类型获取配置键值对Map")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.CONFIG_READ + "')")
    public PubResult<Map<String, String>> getConfigMapByType(
            @Parameter(description = "配置类型") @PathVariable String configType) {
        Map<String, String> configMap = sysConfigService.getConfigMapByType(configType);
        return PubResult.success(configMap);
    }

    /**
     * 获取所有启用的配置键值对Map
     */
    @GetMapping("/map/enabled")
    @Operation(summary = "获取启用的配置Map", description = "获取所有启用的配置键值对Map")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.CONFIG_READ + "')")
    public PubResult<Map<String, String>> getAllEnabledConfigMap() {
        Map<String, String> configMap = sysConfigService.getAllEnabledConfigMap();
        return PubResult.success(configMap);
    }

    /**
     * 刷新配置缓存
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新配置缓存", description = "从数据库重新加载配置到Redis缓存")
    @PreAuthorize("hasAuthority('" + AuthorityConstants.CONFIG_REFRESH + "')")
    public PubResult<String> refreshConfigCache() {
        try {
            // 清空现有缓存
            configCacheService.clearAllConfigs();

            // 重新加载配置
            Map<String, String> configMap = sysConfigService.getAllEnabledConfigMap();
            configCacheService.batchCacheConfigs(configMap);

            return PubResult.success("配置缓存刷新成功，共加载" + configMap.size() + "个配置");
        } catch (Exception e) {
            log.error("刷新配置缓存失败", e);
            return PubResult.error("刷新配置缓存失败: " + e.getMessage());
        }
    }
}

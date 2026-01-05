package com.star.swiftConfig.service.impl;

import com.star.swiftConfig.domain.SysConfig;
import com.star.swiftConfig.mapper.postgresql.SysConfigMapper;
import com.star.swiftConfig.service.SysConfigService;
import com.star.swiftEncrypt.service.CryptoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 系统配置服务实现类
 * 提供配置的存储和读取功能
 * 支持通过标记控制是否加密：
 * - DEC(value): 存储时加密value
 * - ENC(value): 读取时解密value
 * - 无标记: 明文存储和读取
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl implements SysConfigService {

    private final SysConfigMapper sysConfigMapper;
    private final CryptoService cryptoService;

    // DEC()标记的正则表达式
    private static final Pattern DEC_PATTERN = Pattern.compile("^DEC\\((.*)\\)$");
    // ENC()标记的正则表达式
    private static final Pattern ENC_PATTERN = Pattern.compile("^ENC\\((.*)\\)$");

    @Override
    public String getConfigValue(String configKey) {
        SysConfig config = sysConfigMapper.findByConfigKey(configKey);
        if (config == null) {
            return null;
        }
        return decryptValue(config.getConfigValue());
    }

    @Override
    public SysConfig getConfig(String configKey) {
        SysConfig config = sysConfigMapper.findByConfigKey(configKey);
        if (config != null) {
            config.setConfigValue(decryptValue(config.getConfigValue()));
        }
        return config;
    }

    @Override
    public SysConfig getConfigById(Long configId) {
        SysConfig config = sysConfigMapper.findById(configId);
        if (config != null) {
            config.setConfigValue(decryptValue(config.getConfigValue()));
        }
        return config;
    }

    @Override
    public List<SysConfig> getConfigsByType(String configType) {
        List<SysConfig> configs = sysConfigMapper.findByConfigType(configType);
        configs.forEach(config -> config.setConfigValue(decryptValue(config.getConfigValue())));
        return configs;
    }

    @Override
    public List<SysConfig> getAllEnabledConfigs() {
        List<SysConfig> configs = sysConfigMapper.findAllEnabled();
        configs.forEach(config -> config.setConfigValue(decryptValue(config.getConfigValue())));
        return configs;
    }

    @Override
    public List<SysConfig> getAllConfigs() {
        List<SysConfig> configs = sysConfigMapper.findAll();
        configs.forEach(config -> config.setConfigValue(decryptValue(config.getConfigValue())));
        return configs;
    }

    @Override
    @Transactional
    public SysConfig saveConfig(SysConfig config) {
        // 检查配置键是否已存在
        if (sysConfigMapper.existsByConfigKey(config.getConfigKey())) {
            throw new IllegalArgumentException("配置键已存在: " + config.getConfigKey());
        }

        // 处理加密标记
        String processedValue = processValueForStorage(config.getConfigValue());
        config.setConfigValue(processedValue);

        // 设置时间戳
        LocalDateTime now = LocalDateTime.now();
        config.setCreatedAt(now);
        config.setUpdatedAt(now);

        // 保存配置
        sysConfigMapper.insert(config);

        log.info("保存配置成功: configKey={}, configType={}", config.getConfigKey(), config.getConfigType());
        return config;
    }

    @Override
    @Transactional
    public SysConfig updateConfig(SysConfig config) {
        // 检查配置是否存在
        SysConfig existingConfig = sysConfigMapper.findById(config.getConfigId());
        if (existingConfig == null) {
            throw new IllegalArgumentException("配置不存在: " + config.getConfigId());
        }

        // 处理加密标记
        String processedValue = processValueForStorage(config.getConfigValue());
        config.setConfigValue(processedValue);

        // 设置更新时间
        config.setUpdatedAt(LocalDateTime.now());

        // 更新配置
        sysConfigMapper.update(config);

        log.info("更新配置成功: configKey={}, configType={}", config.getConfigKey(), config.getConfigType());
        return config;
    }

    @Override
    @Transactional
    public boolean updateConfigValue(String configKey, String configValue, String updatedBy) {
        // 检查配置是否存在
        SysConfig existingConfig = sysConfigMapper.findByConfigKey(configKey);
        if (existingConfig == null) {
            throw new IllegalArgumentException("配置不存在: " + configKey);
        }

        // 处理加密标记
        String processedValue = processValueForStorage(configValue);

        // 更新配置值
        int result = sysConfigMapper.updateValueByConfigKey(configKey, processedValue, updatedBy);

        log.info("更新配置值成功: configKey={}", configKey);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean deleteConfig(Long configId) {
        int result = sysConfigMapper.deleteById(configId);
        log.info("删除配置成功: configId={}", configId);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean deleteConfigByKey(String configKey) {
        int result = sysConfigMapper.deleteByConfigKey(configKey);
        log.info("删除配置成功: configKey={}", configKey);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean toggleConfig(Long configId, Boolean enabled, String updatedBy) {
        int result = sysConfigMapper.updateEnabledStatus(configId, enabled, updatedBy);
        log.info("切换配置状态成功: configId={}, enabled={}", configId, enabled);
        return result > 0;
    }

    @Override
    public boolean existsConfigKey(String configKey) {
        return sysConfigMapper.existsByConfigKey(configKey);
    }

    @Override
    public Map<String, String> getConfigMapByType(String configType) {
        List<SysConfig> configs = sysConfigMapper.findByConfigType(configType);
        Map<String, String> configMap = new HashMap<>();
        for (SysConfig config : configs) {
            configMap.put(config.getConfigKey(), decryptValue(config.getConfigValue()));
        }
        return configMap;
    }

    @Override
    public Map<String, String> getAllEnabledConfigMap() {
        List<SysConfig> configs = sysConfigMapper.findAllEnabled();
        Map<String, String> configMap = new HashMap<>();
        for (SysConfig config : configs) {
            configMap.put(config.getConfigKey(), decryptValue(config.getConfigValue()));
        }
        return configMap;
    }

    /**
     * 处理存储前的值
     * 如果值以DEC()包裹，则加密后存储
     * 否则直接存储
     *
     * @param value 原始值
     * @return 处理后的值
     */
    private String processValueForStorage(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        Matcher matcher = DEC_PATTERN.matcher(value);
        if (matcher.matches()) {
            // 提取DEC()中的内容并加密
            String content = matcher.group(1);
            try {
                String encrypted = cryptoService.encryptWithAES(content);
                log.debug("加密配置值成功");
                return encrypted;
            } catch (Exception e) {
                log.error("加密配置值失败", e);
                throw new RuntimeException("加密配置值失败", e);
            }
        }

        // 没有DEC()标记，直接存储
        return value;
    }

    /**
     * 解密值
     * 如果值以ENC()包裹，则解密
     * 否则直接返回
     *
     * @param value 存储的值
     * @return 解密后的值或原值
     */
    private String decryptValue(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        Matcher matcher = ENC_PATTERN.matcher(value);
        if (matcher.matches()) {
            // 提取ENC()中的内容并解密
            String content = matcher.group(1);
            try {
                return cryptoService.decryptWithAES(content);
            } catch (Exception e) {
                log.error("解密配置值失败", e);
                throw new RuntimeException("解密配置值失败", e);
            }
        }

        // 没有ENC()标记，直接返回
        return value;
    }
}

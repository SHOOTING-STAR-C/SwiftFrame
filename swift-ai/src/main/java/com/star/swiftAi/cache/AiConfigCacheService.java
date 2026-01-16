package com.star.swiftAi.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.star.swiftAi.dto.ModelDTO;
import com.star.swiftAi.dto.ProviderDTO;
import com.star.swiftAi.dto.SystemPromptDTO;
import com.star.swiftredis.service.ConfigCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * AI配置缓存服务
 * 用于缓存AI模块的配置数据（模型、供应商、系统提示词）
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiConfigCacheService {

    private final ConfigCacheService configCacheService;
    private final ObjectMapper objectMapper;
    
    // 缓存过期时间（30分钟）
    private static final long CACHE_EXPIRE_MINUTES = 30;
    
    // 缓存Key前缀
    private static final String AI_MODEL_PREFIX = "ai:model:";
    private static final String AI_PROVIDER_PREFIX = "ai:provider:";
    private static final String AI_SYSTEM_PROMPT_PREFIX = "ai:system_prompt:";
    private static final String AI_ENABLED_MODELS = "ai:enabled_models";
    private static final String AI_ENABLED_PROVIDERS = "ai:enabled_providers";
    private static final String AI_ENABLED_PROMPTS = "ai:enabled_prompts";

    // ==================== 模型缓存 ====================

    /**
     * 缓存模型
     */
    public void cacheModel(Long modelId, ModelDTO model) {
        String key = AI_MODEL_PREFIX + modelId;
        try {
            String json = objectMapper.writeValueAsString(model);
            configCacheService.cacheConfig(key, json);
            log.debug("缓存模型: modelId={}", modelId);
        } catch (Exception e) {
            log.error("缓存模型失败: modelId={}, error={}", modelId, e.getMessage(), e);
        }
    }

    /**
     * 获取缓存的模型
     */
    public ModelDTO getCachedModel(Long modelId) {
        String key = AI_MODEL_PREFIX + modelId;
        try {
            String json = configCacheService.getCachedConfig(key);
            if (json != null) {
                log.debug("命中模型缓存: modelId={}", modelId);
                return objectMapper.readValue(json, ModelDTO.class);
            }
        } catch (Exception e) {
            log.error("获取模型缓存失败: modelId={}, error={}", modelId, e.getMessage(), e);
        }
        return null;
    }

    /**
     * 删除模型缓存
     */
    public void removeModelCache(Long modelId) {
        String key = AI_MODEL_PREFIX + modelId;
        configCacheService.removeCachedConfig(key);
        log.debug("删除模型缓存: modelId={}", modelId);
    }

    /**
     * 缓存启用的模型列表
     */
    public void cacheEnabledModels(List<ModelDTO> models) {
        try {
            String json = objectMapper.writeValueAsString(models);
            configCacheService.cacheConfig(AI_ENABLED_MODELS, json);
            log.debug("缓存启用的模型列表: count={}", models.size());
        } catch (Exception e) {
            log.error("缓存启用模型列表失败: error={}", e.getMessage(), e);
        }
    }

    /**
     * 获取缓存的启用模型列表
     */
    public List<ModelDTO> getCachedEnabledModels() {
        try {
            String json = configCacheService.getCachedConfig(AI_ENABLED_MODELS);
            if (json != null) {
                log.debug("命中启用模型列表缓存");
                return objectMapper.readValue(json, new TypeReference<List<ModelDTO>>() {});
            }
        } catch (Exception e) {
            log.error("获取启用模型列表缓存失败: error={}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * 删除启用模型列表缓存
     */
    public void removeEnabledModelsCache() {
        configCacheService.removeCachedConfig(AI_ENABLED_MODELS);
        log.debug("删除启用模型列表缓存");
    }

    // ==================== 供应商缓存 ====================

    /**
     * 缓存供应商
     */
    public void cacheProvider(Long providerId, ProviderDTO provider) {
        String key = AI_PROVIDER_PREFIX + providerId;
        try {
            String json = objectMapper.writeValueAsString(provider);
            configCacheService.cacheConfig(key, json);
            log.debug("缓存供应商: providerId={}", providerId);
        } catch (Exception e) {
            log.error("缓存供应商失败: providerId={}, error={}", providerId, e.getMessage(), e);
        }
    }

    /**
     * 获取缓存的供应商
     */
    public ProviderDTO getCachedProvider(Long providerId) {
        String key = AI_PROVIDER_PREFIX + providerId;
        try {
            String json = configCacheService.getCachedConfig(key);
            if (json != null) {
                log.debug("命中供应商缓存: providerId={}", providerId);
                return objectMapper.readValue(json, ProviderDTO.class);
            }
        } catch (Exception e) {
            log.error("获取供应商缓存失败: providerId={}, error={}", providerId, e.getMessage(), e);
        }
        return null;
    }

    /**
     * 删除供应商缓存
     */
    public void removeProviderCache(Long providerId) {
        String key = AI_PROVIDER_PREFIX + providerId;
        configCacheService.removeCachedConfig(key);
        log.debug("删除供应商缓存: providerId={}", providerId);
    }

    /**
     * 缓存启用的供应商列表
     */
    public void cacheEnabledProviders(List<ProviderDTO> providers) {
        try {
            String json = objectMapper.writeValueAsString(providers);
            configCacheService.cacheConfig(AI_ENABLED_PROVIDERS, json);
            log.debug("缓存启用的供应商列表: count={}", providers.size());
        } catch (Exception e) {
            log.error("缓存启用供应商列表失败: error={}", e.getMessage(), e);
        }
    }

    /**
     * 获取缓存的启用供应商列表
     */
    public List<ProviderDTO> getCachedEnabledProviders() {
        try {
            String json = configCacheService.getCachedConfig(AI_ENABLED_PROVIDERS);
            if (json != null) {
                log.debug("命中启用供应商列表缓存");
                return objectMapper.readValue(json, new TypeReference<List<ProviderDTO>>() {});
            }
        } catch (Exception e) {
            log.error("获取启用供应商列表缓存失败: error={}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * 删除启用供应商列表缓存
     */
    public void removeEnabledProvidersCache() {
        configCacheService.removeCachedConfig(AI_ENABLED_PROVIDERS);
        log.debug("删除启用供应商列表缓存");
    }

    // ==================== 系统提示词缓存 ====================

    /**
     * 缓存系统提示词
     */
    public void cacheSystemPrompt(Long promptId, SystemPromptDTO prompt) {
        String key = AI_SYSTEM_PROMPT_PREFIX + promptId;
        try {
            String json = objectMapper.writeValueAsString(prompt);
            configCacheService.cacheConfig(key, json);
            log.debug("缓存系统提示词: promptId={}", promptId);
        } catch (Exception e) {
            log.error("缓存系统提示词失败: promptId={}, error={}", promptId, e.getMessage(), e);
        }
    }

    /**
     * 获取缓存的系统提示词
     */
    public SystemPromptDTO getCachedSystemPrompt(Long promptId) {
        String key = AI_SYSTEM_PROMPT_PREFIX + promptId;
        try {
            String json = configCacheService.getCachedConfig(key);
            if (json != null) {
                log.debug("命中系统提示词缓存: promptId={}", promptId);
                return objectMapper.readValue(json, SystemPromptDTO.class);
            }
        } catch (Exception e) {
            log.error("获取系统提示词缓存失败: promptId={}, error={}", promptId, e.getMessage(), e);
        }
        return null;
    }

    /**
     * 删除系统提示词缓存
     */
    public void removeSystemPromptCache(Long promptId) {
        String key = AI_SYSTEM_PROMPT_PREFIX + promptId;
        configCacheService.removeCachedConfig(key);
        log.debug("删除系统提示词缓存: promptId={}", promptId);
    }

    /**
     * 缓存启用的提示词列表
     */
    public void cacheEnabledPrompts(List<SystemPromptDTO> prompts) {
        try {
            String json = objectMapper.writeValueAsString(prompts);
            configCacheService.cacheConfig(AI_ENABLED_PROMPTS, json);
            log.debug("缓存启用的提示词列表: count={}", prompts.size());
        } catch (Exception e) {
            log.error("缓存启用提示词列表失败: error={}", e.getMessage(), e);
        }
    }

    /**
     * 获取缓存的启用提示词列表
     */
    public List<SystemPromptDTO> getCachedEnabledPrompts() {
        try {
            String json = configCacheService.getCachedConfig(AI_ENABLED_PROMPTS);
            if (json != null) {
                log.debug("命中启用提示词列表缓存");
                return objectMapper.readValue(json, new TypeReference<List<SystemPromptDTO>>() {});
            }
        } catch (Exception e) {
            log.error("获取启用提示词列表缓存失败: error={}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * 删除启用提示词列表缓存
     */
    public void removeEnabledPromptsCache() {
        configCacheService.removeCachedConfig(AI_ENABLED_PROMPTS);
        log.debug("删除启用提示词列表缓存");
    }

    // ==================== 通用方法 ====================

    /**
     * 清空所有AI配置缓存
     */
    public void clearAllAiCache() {
        configCacheService.clearAllConfigs();
        log.info("清空所有AI配置缓存");
    }
}

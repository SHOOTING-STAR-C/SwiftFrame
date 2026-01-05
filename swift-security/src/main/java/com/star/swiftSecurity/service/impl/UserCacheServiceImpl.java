package com.star.swiftSecurity.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.star.swiftCommon.properties.CommonProperties;
import com.star.swiftSecurity.dto.UserCacheDTO;
import com.star.swiftSecurity.entity.SwiftUserDetails;
import com.star.swiftSecurity.properties.SecurityCacheProperties;
import com.star.swiftSecurity.service.UserCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户缓存服务实现
 * 使用Redis缓存用户信息，减少数据库查询
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserCacheServiceImpl implements UserCacheService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final CommonProperties commonProperties;
    private final SecurityCacheProperties securityCacheProperties;
    private final ObjectMapper objectMapper;
    
    /**
     * Redis Key前缀
     */
    private static final String CACHE_KEY_PREFIX = "user:info:";

    /**
     * ID到用户名的映射前缀
     */
    private static final String ID_MAP_PREFIX = "user:id_map:";
    
    @Override
    public void cacheUser(String username, SwiftUserDetails user) {
        // 检查是否启用缓存
        if (!securityCacheProperties.isEnabled()) {
            log.debug("用户缓存未启用，跳过缓存: {}", username);
            return;
        }
        
        try {
            // 创建缓存DTO，包含用户信息、角色和权限
            UserCacheDTO cacheDTO = new UserCacheDTO();
            cacheDTO.setUserId(user.getUserId());
            cacheDTO.setUsername(user.getUsername());
            cacheDTO.setFullName(user.getFullName());
            cacheDTO.setEmail(user.getEmail());
            cacheDTO.setPhone(user.getPhone());
            cacheDTO.setEnabled(user.isEnabled());
            cacheDTO.setAccountNonExpired(user.isAccountNonExpired());
            cacheDTO.setAccountNonLocked(user.isAccountNonLocked());
            cacheDTO.setCredentialsNonExpired(user.isCredentialsNonExpired());
            cacheDTO.setFailedLoginAttempts(user.getFailedLoginAttempts());
            cacheDTO.setLockUntil(user.getLockUntil());
            cacheDTO.setPasswordChangedAt(user.getPasswordChangedAt());
            cacheDTO.setLastLoginAt(user.getLastLoginAt());
            cacheDTO.setLastLoginIp(user.getLastLoginIp());
            cacheDTO.setCreatedAt(user.getCreatedAt());
            
            // 提取角色名称
            Set<String> roleNames = user.getUserRoles().stream()
                    .map(userRole -> userRole.getRole().getName())
                    .collect(Collectors.toSet());
            cacheDTO.setRoleNames(roleNames);
            
            // 提取权限名称
            Set<String> authorityNames = user.getUserRoles().stream()
                    .flatMap(userRole -> userRole.getRole().getRoleAuthorities().stream())
                    .map(roleAuth -> roleAuth.getAuthority().getName())
                    .collect(Collectors.toSet());
            cacheDTO.setAuthorityNames(authorityNames);
            
            String key = buildCacheKey(username);
            String json = objectMapper.writeValueAsString(cacheDTO);
            
            // 缓存用户信息
            log.info("正在缓存用户信息，Key: {}", key);
            redisTemplate.opsForValue().set(
                    key,
                    json,
                    securityCacheProperties.getUserCacheExpirationMinutes(),
                    TimeUnit.MINUTES
            );

            // 缓存ID到用户名的映射，方便按ID查找
            String idMapKey = buildIdMapKey(user.getUserId());
            redisTemplate.opsForValue().set(
                    idMapKey,
                    username,
                    securityCacheProperties.getUserCacheExpirationMinutes(),
                    TimeUnit.MINUTES
            );

            log.debug("用户信息已缓存（含角色和权限）: {}, ID: {}, 角色: {}, 权限: {}", 
                    username, user.getUserId(), roleNames.size(), authorityNames.size());
        } catch (Exception e) {
            log.error("缓存用户信息失败: {}", username, e);
        }
    }
    
    @Override
    public UserCacheDTO getCachedUser(String username) {
        // 检查是否启用缓存
        if (!securityCacheProperties.isEnabled()) {
            return null;
        }
        
        try {
            String key = buildCacheKey(username);
            log.debug("尝试从缓存获取用户信息，Key: {}", key);
            Object cachedObj = redisTemplate.opsForValue().get(key);
            if (cachedObj != null) {
                UserCacheDTO cacheDTO;
                if (cachedObj instanceof String) {
                    cacheDTO = objectMapper.readValue((String) cachedObj, UserCacheDTO.class);
                } else {
                    cacheDTO = objectMapper.convertValue(cachedObj, UserCacheDTO.class);
                }
                log.debug("成功从缓存获取用户信息: {}, 角色: {}, 权限: {}",
                        username, 
                        cacheDTO.getRoleNames() != null ? cacheDTO.getRoleNames().size() : 0,
                        cacheDTO.getAuthorityNames() != null ? cacheDTO.getAuthorityNames().size() : 0);
                return cacheDTO;
            } else {
                log.warn("缓存未命中，Key: {}", key);
            }
        } catch (Exception e) {
            log.error("从缓存获取用户信息失败: {}", username, e);
            // 如果反序列化彻底失败，尝试清除异常缓存
            redisTemplate.delete(buildCacheKey(username));
        }
        return null;
    }
    
    @Override
    public void evictUser(String username) {
        try {
            // 先尝试获取用户信息以拿到ID
            String key = buildCacheKey(username);
            Object cachedObj = redisTemplate.opsForValue().get(key);
            if (cachedObj != null) {
                UserCacheDTO cacheDTO;
                if (cachedObj instanceof String) {
                    cacheDTO = objectMapper.readValue((String) cachedObj, UserCacheDTO.class);
                } else {
                    cacheDTO = objectMapper.convertValue(cachedObj, UserCacheDTO.class);
                }
                if (cacheDTO.getUserId() != null) {
                    redisTemplate.delete(buildIdMapKey(cacheDTO.getUserId()));
                }
            }
            redisTemplate.delete(key);
            log.debug("用户缓存已清除: {}", username);
        } catch (Exception e) {
            log.error("清除用户缓存失败: {}", username, e);
            // 即使获取详情失败，也要尝试直接删除 Key
            redisTemplate.delete(buildCacheKey(username));
        }
    }

    @Override
    public UserCacheDTO getCachedUserById(Long userId) {
        if (!securityCacheProperties.isEnabled() || userId == null) {
            return null;
        }
        try {
            String idMapKey = buildIdMapKey(userId);
            Object usernameObj = redisTemplate.opsForValue().get(idMapKey);
            if (usernameObj != null) {
                return getCachedUser(usernameObj.toString());
            }
        } catch (Exception e) {
            log.error("从ID映射获取用户信息失败: {}", userId, e);
        }
        return null;
    }

    @Override
    public void evictUserById(Long userId) {
        if (userId == null) return;
        try {
            String idMapKey = buildIdMapKey(userId);
            Object usernameObj = redisTemplate.opsForValue().get(idMapKey);
            if (usernameObj != null) {
                evictUser(usernameObj.toString());
            }
            redisTemplate.delete(idMapKey);
        } catch (Exception e) {
            log.error("通过ID清除用户缓存失败: {}", userId, e);
        }
    }
    
    @Override
    public boolean isUserCached(String username) {
        try {
            String key = buildCacheKey(username);
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("检查用户缓存失败: {}", username, e);
            return false;
        }
    }
    
    /**
     * 构建缓存Key
     *
     * @param username 用户名
     * @return 缓存Key
     */
    private String buildCacheKey(String username) {
        return commonProperties.getName() + ":" + CACHE_KEY_PREFIX + username;
    }

    /**
     * 构建ID映射Key
     *
     * @param userId 用户ID
     * @return ID映射Key
     */
    private String buildIdMapKey(Long userId) {
        return commonProperties.getName() + ":" + ID_MAP_PREFIX + userId;
    }
}

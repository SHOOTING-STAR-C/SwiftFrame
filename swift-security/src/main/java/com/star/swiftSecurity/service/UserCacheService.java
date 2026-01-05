package com.star.swiftSecurity.service;

import com.star.swiftSecurity.dto.UserCacheDTO;
import com.star.swiftSecurity.entity.SwiftUserDetails;

/**
 * 用户缓存服务接口
 * 用于缓存用户信息，减少数据库查询
 *
 * @author SHOOTING_STAR_C
 */
public interface UserCacheService {
    
    /**
     * 缓存用户信息
     *
     * @param username 用户名
     * @param user     用户信息
     */
    void cacheUser(String username, SwiftUserDetails user);
    
    /**
     * 从缓存获取用户信息
     *
     * @param username 用户名
     * @return 用户缓存DTO，如果缓存不存在返回null
     */
    UserCacheDTO getCachedUser(String username);
    
    /**
     * 清除用户缓存
     *
     * @param username 用户名
     */
    void evictUser(String username);

    /**
     * 根据用户ID获取缓存的用户信息
     *
     * @param userId 用户ID
     * @return 缓存的用户信息，未命中返回null
     */
    UserCacheDTO getCachedUserById(Long userId);

    /**
     * 根据用户ID清除缓存
     *
     * @param userId 用户ID
     */
    void evictUserById(Long userId);

    /**
     * 检查用户是否已缓存
     *
     * @param username 用户名
     * @return true表示已缓存，false表示未缓存
     */
    boolean isUserCached(String username);
}

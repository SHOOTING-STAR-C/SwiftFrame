package com.star.swiftSecurity.service.impl;

import com.star.swiftCommon.domain.PageResult;
import com.star.swiftSecurity.dto.UserCacheDTO;
import com.star.swiftSecurity.entity.SwiftRole;
import com.star.swiftSecurity.entity.SwiftUserDetails;
import com.star.swiftSecurity.entity.SwiftUserRole;
import com.star.swiftSecurity.entity.SwiftUserRoleId;
import com.star.swiftSecurity.exception.BusinessException;
import com.star.swiftSecurity.exception.DuplicateEntityException;
import com.star.swiftSecurity.mapper.mysql.SwiftRoleMapper;
import com.star.swiftSecurity.mapper.mysql.SwiftUserMapper;
import com.star.swiftSecurity.mapper.mysql.SwiftUserRoleMapper;
import com.star.swiftSecurity.service.AccountStateService;
import com.star.swiftSecurity.service.SwiftUserService;
import com.star.swiftSecurity.service.UserCacheService;
import com.star.swiftCommon.utils.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SwiftUserServiceImpl implements SwiftUserService {
    private final SwiftUserMapper userMapper;
    private final SwiftRoleMapper roleMapper;
    private final SwiftUserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final AccountStateService accountStateService;
    private final UserCacheService userCacheService;

    /**
     * 创建用户
     *
     * @param user user
     * @return SwiftUserDetails
     */
    @Override
    public SwiftUserDetails createUser(SwiftUserDetails user) {
        if (userMapper.existsByUsername(user.getUsername())) {
            throw new DuplicateEntityException("用户名已存在");
        }

        if (userMapper.existsByEmail(user.getEmail())) {
            throw new DuplicateEntityException("邮箱已存在");
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPasswordChangedAt(LocalDateTime.now());
        user.setCreatedAt(LocalDateTime.now());
        if (user.getUserId() == null) {
            user.setUserId(SnowflakeIdGenerator.generateId());
        }
        userMapper.insert(user);
        
        // 自动为新用户分配普通用户角色（ROLE_USER）
        try {
            assignRoleToUser(user.getUserId(), 5L, "SYSTEM");
        } catch (Exception e) {
            // 如果分配角色失败，记录警告但不影响用户创建
            log.warn("为新用户分配默认角色失败: {}", user.getUsername(), e);
        }
        
        return user;
    }

    /**
     * 更新用户信息
     *
     * @param user user
     * @return SwiftUserDetails
     */
    @Override
    public SwiftUserDetails updateUser(SwiftUserDetails user) {
        // 更新前需确保获取到最新数据，这里可以直接查库或者走缓存
        // 但由于后续要更新并清除缓存，直接查库更稳妥
        SwiftUserDetails existing = userMapper.findById(user.getUserId());
        if (existing == null) {
            throw new BusinessException("用户不存在");
        }

        // 更新允许修改的字段
        existing.setEmail(user.getEmail());
        existing.setFullName(user.getFullName());
        existing.setPhone(user.getPhone());

        userMapper.update(existing);
        
        // 清除用户缓存
        userCacheService.evictUser(existing.getUsername());
        userCacheService.evictUserById(existing.getUserId());
        
        return existing;
    }

    /**
     * 删除用户
     *
     * @param userId userId
     */
    @Override
    public void deleteUser(Long userId) {
        SwiftUserDetails user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        userMapper.deleteById(userId);
        
        // 清除用户缓存
        userCacheService.evictUser(user.getUsername());
        userCacheService.evictUserById(userId);
    }

    /**
     * 获取用户信息
     *
     * @param userId userId
     * @return SwiftUserDetails
     */
    @Override
    public SwiftUserDetails getUserById(Long userId) {
        // 优先从缓存获取
        UserCacheDTO cachedUser = userCacheService.getCachedUserById(userId);
        if (cachedUser != null) {
            return restoreUserFromCache(cachedUser);
        }

        // 缓存未命中，使用优化查询一次性获取所有数据（避免N+1查询）
        SwiftUserDetails user = userMapper.findByIdWithAuthorities(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 写入缓存（此时user已包含完整的角色权限信息，不会触发额外查询）
        userCacheService.cacheUser(user.getUsername(), user);
        
        return user;
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param username username
     * @return SwiftUserDetails
     */
    @Override
    public SwiftUserDetails loadUserByUsername(String username) {
        SwiftUserDetails user = userMapper.findByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId userId
     * @return SwiftUserDetails
     */
    @Override
    public SwiftUserDetails loadUserByUserId(Long userId) {
        return getUserById(userId);
    }
    
    /**
     * 从缓存DTO恢复用户信息
     *
     * @param cacheDTO 缓存DTO
     * @return SwiftUserDetails
     */
    private SwiftUserDetails restoreUserFromCache(UserCacheDTO cacheDTO) {
        SwiftUserDetails user = new SwiftUserDetails();
        user.setUserId(cacheDTO.getUserId());
        user.setUsername(cacheDTO.getUsername());
        user.setFullName(cacheDTO.getFullName());
        user.setEmail(cacheDTO.getEmail());
        user.setPhone(cacheDTO.getPhone());
        user.setEnabled(cacheDTO.isEnabled());
        user.setAccountNonExpired(cacheDTO.isAccountNonExpired());
        user.setAccountNonLocked(cacheDTO.isAccountNonLocked());
        user.setCredentialsNonExpired(cacheDTO.isCredentialsNonExpired());
        user.setFailedLoginAttempts(cacheDTO.getFailedLoginAttempts());
        user.setLockUntil(cacheDTO.getLockUntil());
        user.setPasswordChangedAt(cacheDTO.getPasswordChangedAt());
        user.setLastLoginAt(cacheDTO.getLastLoginAt());
        user.setLastLoginIp(cacheDTO.getLastLoginIp());
        user.setCreatedAt(cacheDTO.getCreatedAt());
        
        // 设置空的角色集合，因为权限信息已经通过缓存获取
        user.setUserRoles(new java.util.HashSet<>());
        
        // 设置缓存的权限名称
        if (cacheDTO.getAuthorityNames() != null) {
            user.setCachedAuthorityNames(cacheDTO.getAuthorityNames());
        }
        
        user.setPassword(null);
     
        return user;
    }

    /**
     * 获取所有用户
     *
     * @return List<SwiftUserDetails>
     */
    public List<SwiftUserDetails> getAllUsers() {
        return userMapper.findAll();
    }

    /**
     * 分页获取用户
     *
     * @param page 当前页码
     * @param size 每页大小
     * @return PageResult<SwiftUserDetails>
     */
    @Override
    public PageResult<SwiftUserDetails> getUserPage(Integer page, Integer size) {
        // 计算偏移量
        Integer offset = (page - 1) * size;
        
        // 查询分页数据
        List<SwiftUserDetails> records = userMapper.findPage(offset, size);
        
        // 查询总记录数
        Integer total = userMapper.countAll();
        
        return PageResult.success(records, total, page, size);
    }

    /**
     * 更改密码
     *
     * @param userId      userId
     * @param newPassword newPassword
     * @return SwiftUserDetails
     */
    @Override
    public SwiftUserDetails changePassword(Long userId, String newPassword) {
        SwiftUserDetails user = getUserById(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordChangedAt(LocalDateTime.now());
        user.setCredentialsNonExpired(true);
        userMapper.update(user);
        
        // 清除用户缓存
        userCacheService.evictUser(user.getUsername());
        userCacheService.evictUserById(userId);
        
        return user;
    }

    /**
     * 授予角色
     *
     * @param userId     userId
     * @param roleId     roleId
     * @param assignedBy assignedBy
     */
    @Override
    public void assignRoleToUser(Long userId, Long roleId, String assignedBy) {
        SwiftUserDetails user = getUserById(userId);
        SwiftRole role = roleMapper.findById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        if (userRoleMapper.existsByUserAndRole(user.getUserId(), roleId)) {
            throw new DuplicateEntityException("用户已拥有该角色");
        }

        SwiftUserRole userRole = new SwiftUserRole();
        SwiftUserRoleId id = new SwiftUserRoleId();
        id.setUserId(user.getUserId());
        id.setRoleId(role.getRoleId());
        userRole.setUserId(id);
        userRole.setUser(user);
        userRole.setRole(role);
        userRole.setAssignedBy(assignedBy);
        userRole.setAssignedAt(LocalDateTime.now());
        userRoleMapper.insert(userRole);
        
        // 清除用户缓存
        userCacheService.evictUser(user.getUsername());
        userCacheService.evictUserById(userId);
    }

    /**
     * 收回角色
     *
     * @param userId userId
     * @param roleId roleId
     */
    @Override
    public void removeRoleFromUser(Long userId, Long roleId) {
        SwiftUserDetails user = getUserById(userId);
        userRoleMapper.deleteByUserAndRole(userId, roleId);
        
        // 清除用户缓存
        userCacheService.evictUser(user.getUsername());
        userCacheService.evictUserById(userId);
    }

    /**
     * 获取用户拥有的角色
     *
     * @param userId userId
     * @return Set<SwiftRole>
     */
    @Override
    public Set<SwiftRole> getUserRoles(Long userId) {
        return userRoleMapper.findByUser(userId).stream()
                .map(SwiftUserRole::getRole)
                .collect(Collectors.toSet());
    }

    /**
     * 启用用户
     *
     * @param userId userId
     */
    @Override
    public void enableUser(Long userId) {
        SwiftUserDetails user = getUserById(userId);
        user.setEnabled(true);
        userMapper.update(user);
        userCacheService.evictUser(user.getUsername());
        userCacheService.evictUserById(userId);
    }

    /**
     * 禁用账户
     *
     * @param userId userId
     */
    @Override
    public void disableUser(Long userId) {
        SwiftUserDetails user = getUserById(userId);
        user.setEnabled(false);
        userMapper.update(user);
        userCacheService.evictUser(user.getUsername());
        userCacheService.evictUserById(userId);
    }

    /**
     * 解锁账户
     *
     * @param userId userId
     */
    @Override
    public void unlockUser(Long userId) {
        SwiftUserDetails user = getUserById(userId);
        accountStateService.unlockUserAccount(user);
        userMapper.update(user);
        userCacheService.evictUser(user.getUsername());
        userCacheService.evictUserById(userId);
    }

    /**
     * 锁定用户
     *
     * @param userId userId
     */
    @Override
    public void lockUser(Long userId) {
        SwiftUserDetails user = getUserById(userId);
        accountStateService.lockUserAccount(user);
        userMapper.update(user);
        userCacheService.evictUser(user.getUsername());
        userCacheService.evictUserById(userId);
    }

    /**
     * 记录登录成功
     *
     * @param userId    userId
     * @param ipAddress ipAddress
     */
    @Override
    public void recordLoginSuccess(Long userId, String ipAddress) {
        SwiftUserDetails user = getUserById(userId);
        user.setLastLoginAt(LocalDateTime.now());
        user.setLastLoginIp(ipAddress);
        user.setFailedLoginAttempts(0); // 重置失败计数
        userMapper.update(user);
        userCacheService.evictUser(user.getUsername());
        userCacheService.evictUserById(userId);
    }

    /**
     * 记录登录失败
     *
     * @param userId userId
     */
    @Override
    public void recordLoginFailure(Long userId) {
        SwiftUserDetails user = getUserById(userId);
        accountStateService.handleLoginFailure(user);
        userMapper.update(user);
        userCacheService.evictUser(user.getUsername());
        userCacheService.evictUserById(userId);
    }

    /**
     * 校验密码是否失效
     *
     * @param userId userId
     * @return boolean
     */
    @Override
    public boolean isPasswordExpired(Long userId) {
        // 校验密码是否失效可以使用基础信息
        SwiftUserDetails user = userMapper.findBaseById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return accountStateService.isPasswordExpired(user);
    }

    /**
     * 根据邮箱查找用户
     *
     * @param email email
     * @return SwiftUserDetails
     */
    @Override
    public SwiftUserDetails getUserByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    /**
     * 根据手机号查找用户
     *
     * @param phone phone
     * @return SwiftUserDetails
     */
    @Override
    public SwiftUserDetails getUserByPhone(String phone) {
        return userMapper.findByPhone(phone);
    }
}

package com.star.swiftSecurity.service.impl;

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
import com.star.swiftCommon.utils.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
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
@Service
@RequiredArgsConstructor
@Transactional
public class SwiftUserServiceImpl implements SwiftUserService {
    private final SwiftUserMapper userMapper;
    private final SwiftRoleMapper roleMapper;
    private final SwiftUserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final AccountStateService accountStateService;

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
        SwiftUserDetails existing = getUserById(user.getUserId());

        // 更新允许修改的字段
        existing.setEmail(user.getEmail());
        existing.setFullName(user.getFullName());
        existing.setPhone(user.getPhone());

        userMapper.update(existing);
        return existing;
    }

    /**
     * 删除用户
     *
     * @param userId userId
     */
    @Override
    public void deleteUser(Long userId) {
        userMapper.deleteById(userId);
    }

    /**
     * 获取用户信息
     *
     * @param userId userId
     * @return SwiftUserDetails
     */
    @Override
    public SwiftUserDetails getUserById(Long userId) {
        SwiftUserDetails user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
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
     * 获取所有用户
     *
     * @return List<SwiftUserDetails>
     */
    public List<SwiftUserDetails> getAllUsers() {
        return userMapper.findAll();
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
    }

    /**
     * 收回角色
     *
     * @param userId userId
     * @param roleId roleId
     */
    @Override
    public void removeRoleFromUser(Long userId, Long roleId) {
        userRoleMapper.deleteByUserAndRole(userId, roleId);
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
    }

    /**
     * 校验密码是否失效
     *
     * @param userId userId
     * @return boolean
     */
    @Override
    public boolean isPasswordExpired(Long userId) {
        SwiftUserDetails user = getUserById(userId);
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

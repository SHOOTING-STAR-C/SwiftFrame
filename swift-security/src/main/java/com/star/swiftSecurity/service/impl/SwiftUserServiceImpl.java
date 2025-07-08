package com.star.swiftSecurity.service.impl;

import com.star.swiftSecurity.entity.SwiftRole;
import com.star.swiftSecurity.entity.SwiftUserDetails;
import com.star.swiftSecurity.entity.SwiftUserRole;
import com.star.swiftSecurity.exception.DuplicateEntityException;
import com.star.swiftSecurity.repository.SwiftRoleRepository;
import com.star.swiftSecurity.repository.SwiftUserRepository;
import com.star.swiftSecurity.repository.SwiftUserRoleRepository;
import com.star.swiftSecurity.service.AccountStateService;
import com.star.swiftSecurity.service.SwiftUserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
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
    private final SwiftUserRepository userRepository;
    private final SwiftRoleRepository roleRepository;
    private final SwiftUserRoleRepository userRoleRepository;
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
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new DuplicateEntityException("用户名已存在");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateEntityException("邮箱已存在");
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPasswordChangedAt(LocalDateTime.now());
        return userRepository.save(user);
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

        return userRepository.save(existing);
    }

    /**
     * 删除用户
     *
     * @param userId userId
     */
    @Override
    public void deleteUser(UUID userId) {
        SwiftUserDetails user = getUserById(userId);
        userRepository.delete(user);
    }

    /**
     * 获取用户信息
     *
     * @param userId userId
     * @return SwiftUserDetails
     */
    @Override
    public SwiftUserDetails getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("用户不存在"));
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param username username
     * @return SwiftUserDetails
     */
    @Override
    public SwiftUserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("用户不存在"));
    }

    /**
     * 获取所有用户
     *
     * @param pageable pageable
     * @return Page<SwiftUserDetails>
     */
    @Override
    public Page<SwiftUserDetails> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /**
     * 更改密码
     *
     * @param userId      userId
     * @param newPassword newPassword
     * @return SwiftUserDetails
     */
    @Override
    public SwiftUserDetails changePassword(UUID userId, String newPassword) {
        SwiftUserDetails user = getUserById(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordChangedAt(LocalDateTime.now());
        user.setCredentialsNonExpired(true);
        return userRepository.save(user);
    }

    /**
     * 授予角色
     *
     * @param userId     userId
     * @param roleId     roleId
     * @param assignedBy assignedBy
     */
    @Override
    public void assignRoleToUser(UUID userId, Long roleId, String assignedBy) {
        SwiftUserDetails user = getUserById(userId);
        SwiftRole role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("角色不存在"));

        if (userRoleRepository.existsByUserAndRole(user, role)) {
            throw new DuplicateEntityException("用户已拥有该角色");
        }

        SwiftUserRole userRole = new SwiftUserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRole.setAssignedBy(assignedBy);
        userRoleRepository.save(userRole);
    }

    /**
     * 收回角色
     *
     * @param userId userId
     * @param roleId roleId
     */
    @Override
    public void removeRoleFromUser(UUID userId, Long roleId) {
        SwiftUserDetails user = getUserById(userId);
        SwiftRole role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("角色不存在"));

        userRoleRepository.deleteByUserAndRole(user, role);
    }

    /**
     * 获取用户拥有的角色
     *
     * @param userId userId
     * @return Set<SwiftRole>
     */
    @Override
    public Set<SwiftRole> getUserRoles(UUID userId) {
        SwiftUserDetails user = getUserById(userId);
        return userRoleRepository.findByUser(user).stream()
                .map(SwiftUserRole::getRole)
                .collect(Collectors.toSet());
    }

    /**
     * 启用用户
     *
     * @param userId userId
     */
    @Override
    public void enableUser(UUID userId) {
        SwiftUserDetails user = getUserById(userId);
        user.setEnabled(true);
        userRepository.save(user);
    }

    /**
     * 禁用账户
     *
     * @param userId userId
     */
    @Override
    public void disableUser(UUID userId) {
        SwiftUserDetails user = getUserById(userId);
        user.setEnabled(false);
        userRepository.save(user);
    }

    /**
     * 解锁账户
     *
     * @param userId userId
     */
    @Override
    public void unlockUser(UUID userId) {
        SwiftUserDetails user = getUserById(userId);
        accountStateService.unlockUserAccount(user);
        userRepository.save(user);
    }

    /**
     * 锁定用户
     *
     * @param userId userId
     */
    @Override
    public void lockUser(UUID userId) {
        SwiftUserDetails user = getUserById(userId);
        accountStateService.lockUserAccount(user);
        userRepository.save(user);
    }

    /**
     * 记录登录成功
     *
     * @param userId    userId
     * @param ipAddress ipAddress
     */
    @Override
    public void recordLoginSuccess(UUID userId, String ipAddress) {
        SwiftUserDetails user = getUserById(userId);
        user.setLastLoginAt(LocalDateTime.now());
        user.setLastLoginIp(ipAddress);
        user.setFailedLoginAttempts(0); // 重置失败计数
        userRepository.save(user);
    }

    /**
     * 记录登录失败
     *
     * @param userId userId
     */
    @Override
    public void recordLoginFailure(UUID userId) {
        SwiftUserDetails user = getUserById(userId);
        accountStateService.handleLoginFailure(user);
        userRepository.save(user);
    }

    /**
     * 校验密码是否失效
     *
     * @param userId userId
     * @return boolean
     */
    @Override
    public boolean isPasswordExpired(UUID userId) {
        SwiftUserDetails user = getUserById(userId);
        return accountStateService.isPasswordExpired(user);
    }
}

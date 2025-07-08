package com.star.swiftSecurity.service;

import com.star.swiftSecurity.entity.SwiftRole;
import com.star.swiftSecurity.entity.SwiftUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Set;
import java.util.UUID;

/**
 * 用户服务
 *
 * @author SHOOTING_STAR_C
 */
public interface SwiftUserService extends UserDetailsService {
    /**
     * 创建用户
     *
     * @param user user
     * @return SwiftUserDetails
     */
    SwiftUserDetails createUser(SwiftUserDetails user);

    /**
     * 更新用户信息
     *
     * @param user user
     * @return SwiftUserDetails
     */
    SwiftUserDetails updateUser(SwiftUserDetails user);

    /**
     * 删除用户
     *
     * @param userId userId
     */
    void deleteUser(UUID userId);

    /**
     * 根据用户名ID获取用户信息
     *
     * @param userId userId
     * @return SwiftUserDetails
     */
    SwiftUserDetails getUserById(UUID userId);

    /**
     * 根据用户名获取用户信息
     *
     * @param username username
     * @return SwiftUserDetails
     */
    SwiftUserDetails loadUserByUsername(String username);

    /**
     * 获取所有用户
     *
     * @param pageable pageable
     * @return Page<SwiftUserDetails>
     */
    Page<SwiftUserDetails> getAllUsers(Pageable pageable);

    /**
     * 更改密码
     *
     * @param userId      userId
     * @param newPassword newPassword
     * @return SwiftUserDetails
     */
    SwiftUserDetails changePassword(UUID userId, String newPassword);

    /**
     * @param userId     userId
     * @param roleId     roleId
     * @param assignedBy assignedBy
     */
    void assignRoleToUser(UUID userId, Long roleId, String assignedBy);

    /**
     * 收回角色
     *
     * @param userId userId
     * @param roleId roleId
     */
    void removeRoleFromUser(UUID userId, Long roleId);

    /**
     * 获取用户拥有的角色
     *
     * @param userId userId
     * @return Set<SwiftRole>
     */
    Set<SwiftRole> getUserRoles(UUID userId);

    /**
     * 启用用户
     *
     * @param userId userId
     */
    void enableUser(UUID userId);

    /**
     * 禁用账户
     *
     * @param userId userId
     */
    void disableUser(UUID userId);

    /**
     * 解锁账户
     *
     * @param userId userId
     */
    void unlockUser(UUID userId);

    /**
     * 锁定用户
     *
     * @param userId userId
     */
    void lockUser(UUID userId);

    /**
     * 记录登录成功
     *
     * @param userId    userId
     * @param ipAddress ipAddress
     */
    void recordLoginSuccess(UUID userId, String ipAddress);

    /**
     * 记录登录失败
     *
     * @param userId userId
     */
    void recordLoginFailure(UUID userId);

    /**
     * 密码是否过期
     *
     * @param userId userId
     * @return boolean
     */
    boolean isPasswordExpired(UUID userId);

}

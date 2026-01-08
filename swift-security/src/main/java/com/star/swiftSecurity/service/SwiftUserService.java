package com.star.swiftSecurity.service;

import com.star.swiftCommon.domain.PageResult;
import com.star.swiftSecurity.entity.SwiftRole;
import com.star.swiftSecurity.entity.SwiftUserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Set;

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
    void deleteUser(Long userId);

    /**
     * 根据用户名ID获取用户信息
     *
     * @param userId userId
     * @return SwiftUserDetails
     */
    SwiftUserDetails getUserById(Long userId);

    /**
     * 根据用户名获取用户信息
     *
     * @param username username
     * @return SwiftUserDetails
     */
    SwiftUserDetails loadUserByUsername(String username);

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId userId
     * @return SwiftUserDetails
     */
    SwiftUserDetails loadUserByUserId(Long userId);

    /**
     * 获取所有用户
     *
     * @return List<SwiftUserDetails>
     */
    List<SwiftUserDetails> getAllUsers();

    /**
     * 分页获取用户
     *
     * @param page 当前页码
     * @param size 每页大小
     * @return PageResult<SwiftUserDetails>
     */
    PageResult<SwiftUserDetails> getUserPage(Integer page, Integer size);

    /**
     * 更改密码
     *
     * @param userId      userId
     * @param newPassword newPassword
     * @return SwiftUserDetails
     */
    SwiftUserDetails changePassword(Long userId, String newPassword);

    /**
     * @param userId     userId
     * @param roleId     roleId
     * @param assignedBy assignedBy
     */
    void assignRoleToUser(Long userId, Long roleId, String assignedBy);

    /**
     * 收回角色
     *
     * @param userId userId
     * @param roleId roleId
     */
    void removeRoleFromUser(Long userId, Long roleId);

    /**
     * 获取用户拥有的角色
     *
     * @param userId userId
     * @return Set<SwiftRole>
     */
    Set<SwiftRole> getUserRoles(Long userId);

    /**
     * 启用用户
     *
     * @param userId userId
     */
    void enableUser(Long userId);

    /**
     * 禁用账户
     *
     * @param userId userId
     */
    void disableUser(Long userId);

    /**
     * 解锁账户
     *
     * @param userId userId
     */
    void unlockUser(Long userId);

    /**
     * 锁定用户
     *
     * @param userId userId
     */
    void lockUser(Long userId);

    /**
     * 记录登录成功
     *
     * @param userId    userId
     * @param ipAddress ipAddress
     */
    void recordLoginSuccess(Long userId, String ipAddress);

    /**
     * 记录登录失败
     *
     * @param userId userId
     */
    void recordLoginFailure(Long userId);

    /**
     * 密码是否过期
     *
     * @param userId userId
     * @return boolean
     */
    boolean isPasswordExpired(Long userId);

    /**
     * 根据邮箱查找用户
     *
     * @param email email
     * @return SwiftUserDetails
     */
    SwiftUserDetails getUserByEmail(String email);

    /**
     * 根据手机号查找用户
     *
     * @param phone phone
     * @return SwiftUserDetails
     */
    SwiftUserDetails getUserByPhone(String phone);

}

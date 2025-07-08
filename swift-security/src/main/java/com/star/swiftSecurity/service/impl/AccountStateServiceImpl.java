package com.star.swiftSecurity.service.impl;

import com.star.swiftSecurity.entity.SwiftUserDetails;
import com.star.swiftSecurity.properties.AccountSecurityProperties;
import com.star.swiftSecurity.service.AccountStateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 账户状态管理
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountStateServiceImpl implements AccountStateService {
    private final AccountSecurityProperties securityProperties;

    /**
     * 检查失败登录次数
     *
     * @param user user
     */
    @Override
    public void handleLoginFailure(SwiftUserDetails user) {
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);

        if (user.getFailedLoginAttempts() >= securityProperties.getMaxLoginAttempts()) {
            lockUserAccount(user);
        }
    }

    /**
     * 锁定用户
     *
     * @param user user
     */
    public void lockUserAccount(SwiftUserDetails user) {
        lockUserAccount(user, securityProperties.getLockDurationMinutes());
    }

    /**
     * 锁定用户
     *
     * @param user    user
     * @param minutes 分钟
     */
    public void lockUserAccount(SwiftUserDetails user, int minutes) {
        user.setAccountNonLocked(false);
        user.setLockUntil(LocalDateTime.now().plusMinutes(minutes));
    }

    /**
     * 解锁用户
     *
     * @param user user
     */
    public void unlockUserAccount(SwiftUserDetails user) {
        user.setAccountNonLocked(true);
        user.setFailedLoginAttempts(0);
        user.setLockUntil(null);
    }

    /**
     * 密码是否过期
     *
     * @param user user
     * @return boolean
     */
    public boolean isPasswordExpired(SwiftUserDetails user) {
        if (user.getPasswordChangedAt() == null) return false;

        return user.getPasswordChangedAt()
                .plusDays(securityProperties.getPasswordExpiryDays())
                .isBefore(LocalDateTime.now());
    }

}

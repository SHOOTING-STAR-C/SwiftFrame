package com.star.swiftSecurity.service;

import com.star.swiftSecurity.entity.SwiftUserDetails;

public interface AccountStateService {
    /**
     * 检查失败登录次数
     *
     * @param user user
     */
    void handleLoginFailure(SwiftUserDetails user);

    /**
     * 锁定用户
     *
     * @param user user
     */
    void lockUserAccount(SwiftUserDetails user);

    /**
     * 锁定用户
     *
     * @param user    user
     * @param minutes 分钟
     */
    void lockUserAccount(SwiftUserDetails user, int minutes);

    /**
     * 解锁用户
     *
     * @param user user
     */
    void unlockUserAccount(SwiftUserDetails user);

    /**
     * 密码是否过期
     *
     * @param user user
     * @return boolean
     */
    boolean isPasswordExpired(SwiftUserDetails user);
}

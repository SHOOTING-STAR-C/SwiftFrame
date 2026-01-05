package com.star.swiftSecurity.init;

import com.star.swiftSecurity.constant.RoleConstants;
import com.star.swiftSecurity.entity.SwiftRole;
import com.star.swiftSecurity.entity.SwiftUserDetails;
import com.star.swiftSecurity.service.SwiftRoleService;
import com.star.swiftSecurity.service.SwiftUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * 安全数据初始化器
 * 在应用启动时检查并初始化超级管理员账号
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityDataInitializer implements ApplicationRunner {

    private final SwiftUserService userService;
    private final SwiftRoleService roleService;

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_FULL_NAME = "系统管理员";
    private static final String ADMIN_EMAIL = "admin@swift.com";
    private static final int PASSWORD_LENGTH = 16;

    /**
     * 应用启动后执行
     */
    @Override
    public void run(ApplicationArguments args) {
        try {
            // 检查admin用户是否已存在
            SwiftUserDetails existingAdmin = null;
            try {
                existingAdmin = userService.loadUserByUsername(ADMIN_USERNAME);
            } catch (Exception e) {
                // 用户不存在，继续创建
            }

            if (existingAdmin == null) {
                log.info("========================================");
                log.info("检测到首次启动，正在初始化超级管理员账号...");
                log.info("========================================");

                // 生成随机密码
                String randomPassword = generateRandomPassword(PASSWORD_LENGTH);

                // 创建超级管理员用户
                SwiftUserDetails admin = new SwiftUserDetails();
                admin.setUsername(ADMIN_USERNAME);
                admin.setFullName(ADMIN_FULL_NAME);
                admin.setEmail(ADMIN_EMAIL);
                admin.setPassword(randomPassword);
                admin.setEnabled(true);
                admin.setAccountNonExpired(true);
                admin.setAccountNonLocked(true);
                admin.setCredentialsNonExpired(true);
                admin.setFailedLoginAttempts(0);

                SwiftUserDetails createdAdmin = userService.createUser(admin);

                // 分配超级管理员角色
                SwiftRole superAdminRole = roleService.findByName(RoleConstants.ROLE_SUPER_ADMIN);
                if (superAdminRole != null) {
                    userService.assignRoleToUser(
                            createdAdmin.getUserId(),
                            superAdminRole.getRoleId(),
                            "SYSTEM"
                    );
                }

                // 输出登录信息
                log.info("========================================");
                log.info("超级管理员账号创建成功！");
                log.info("用户名: {}", ADMIN_USERNAME);
                log.info("密码: {}", randomPassword);
                log.info("========================================");
                log.warn("请妥善保管上述密码，首次登录后请立即修改！");
                log.info("========================================");
            } else {
                log.debug("超级管理员账号已存在，跳过初始化");
            }
        } catch (Exception e) {
            log.error("初始化超级管理员账号失败", e);
            throw new RuntimeException("初始化超级管理员账号失败", e);
        }
    }

    /**
     * 生成随机密码
     * 密码包含大小写字母、数字和特殊字符
     *
     * @param length 密码长度
     * @return 随机密码
     */
    private String generateRandomPassword(int length) {
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String digits = "0123456789";
        String specialChars = "!@#$%^&*()_+-=[]{}|;:,.<>?";

        String allChars = lowerCase + upperCase + digits + specialChars;
        SecureRandom random = new SecureRandom();

        StringBuilder password = new StringBuilder();

        // 确保至少包含一个大写字母、一个小写字母、一个数字和一个特殊字符
        password.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
        password.append(upperCase.charAt(random.nextInt(upperCase.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));

        // 填充剩余长度
        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // 打乱字符顺序
        return shuffleString(password.toString(), random);
    }

    /**
     * 打乱字符串顺序
     *
     * @param input 输入字符串
     * @param random 随机数生成器
     * @return 打乱后的字符串
     */
    private String shuffleString(String input, SecureRandom random) {
        char[] characters = input.toCharArray();
        for (int i = characters.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = characters[i];
            characters[i] = characters[j];
            characters[j] = temp;
        }
        return new String(characters);
    }
}

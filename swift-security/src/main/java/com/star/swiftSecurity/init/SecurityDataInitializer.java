package com.star.swiftSecurity.init;

import com.star.swiftSecurity.constant.AuthorityConstants;
import com.star.swiftSecurity.constant.RoleConstants;
import com.star.swiftSecurity.entity.SwiftAuthority;
import com.star.swiftSecurity.entity.SwiftRole;
import com.star.swiftSecurity.mapper.SwiftAuthorityMapper;
import com.star.swiftSecurity.mapper.SwiftRoleAuthorityMapper;
import com.star.swiftSecurity.mapper.SwiftRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 安全数据初始化器
 * 在应用启动时自动初始化角色和权限数据
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityDataInitializer implements CommandLineRunner {

    private final SwiftRoleMapper roleMapper;
    private final SwiftAuthorityMapper authorityMapper;
    private final SwiftRoleAuthorityMapper roleAuthorityMapper;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("开始初始化安全数据...");
        
        // 初始化权限
        initAuthorities();
        
        // 初始化角色
        initRoles();
        
        // 为角色分配权限
        assignAuthoritiesToRoles();
        
        log.info("安全数据初始化完成");
    }

    /**
     * 初始化所有权限
     */
    private void initAuthorities() {
        log.info("初始化权限数据...");
        
        // 用户管理权限
        createAuthorityIfNotExists(AuthorityConstants.USER_READ, AuthorityConstants.USER_READ_DESC);
        createAuthorityIfNotExists(AuthorityConstants.USER_CREATE, AuthorityConstants.USER_CREATE_DESC);
        createAuthorityIfNotExists(AuthorityConstants.USER_UPDATE, AuthorityConstants.USER_UPDATE_DESC);
        createAuthorityIfNotExists(AuthorityConstants.USER_DELETE, AuthorityConstants.USER_DELETE_DESC);
        createAuthorityIfNotExists(AuthorityConstants.USER_RESET_PASSWORD, AuthorityConstants.USER_RESET_PASSWORD_DESC);
        createAuthorityIfNotExists(AuthorityConstants.USER_LOCK, AuthorityConstants.USER_LOCK_DESC);
        
        // 角色管理权限
        createAuthorityIfNotExists(AuthorityConstants.ROLE_READ, AuthorityConstants.ROLE_READ_DESC);
        createAuthorityIfNotExists(AuthorityConstants.ROLE_CREATE, AuthorityConstants.ROLE_CREATE_DESC);
        createAuthorityIfNotExists(AuthorityConstants.ROLE_UPDATE, AuthorityConstants.ROLE_UPDATE_DESC);
        createAuthorityIfNotExists(AuthorityConstants.ROLE_DELETE, AuthorityConstants.ROLE_DELETE_DESC);
        createAuthorityIfNotExists(AuthorityConstants.ROLE_GRANT_AUTHORITY, AuthorityConstants.ROLE_GRANT_AUTHORITY_DESC);
        createAuthorityIfNotExists(AuthorityConstants.ROLE_REVOKE_AUTHORITY, AuthorityConstants.ROLE_REVOKE_AUTHORITY_DESC);
        
        // 权限管理权限
        createAuthorityIfNotExists(AuthorityConstants.AUTHORITY_READ, AuthorityConstants.AUTHORITY_READ_DESC);
        createAuthorityIfNotExists(AuthorityConstants.AUTHORITY_CREATE, AuthorityConstants.AUTHORITY_CREATE_DESC);
        createAuthorityIfNotExists(AuthorityConstants.AUTHORITY_UPDATE, AuthorityConstants.AUTHORITY_UPDATE_DESC);
        createAuthorityIfNotExists(AuthorityConstants.AUTHORITY_DELETE, AuthorityConstants.AUTHORITY_DELETE_DESC);
        
        // 业务数据权限
        createAuthorityIfNotExists(AuthorityConstants.DATA_READ, AuthorityConstants.DATA_READ_DESC);
        createAuthorityIfNotExists(AuthorityConstants.DATA_CREATE, AuthorityConstants.DATA_CREATE_DESC);
        createAuthorityIfNotExists(AuthorityConstants.DATA_UPDATE, AuthorityConstants.DATA_UPDATE_DESC);
        createAuthorityIfNotExists(AuthorityConstants.DATA_DELETE, AuthorityConstants.DATA_DELETE_DESC);
        createAuthorityIfNotExists(AuthorityConstants.DATA_EXPORT, AuthorityConstants.DATA_EXPORT_DESC);
        createAuthorityIfNotExists(AuthorityConstants.DATA_IMPORT, AuthorityConstants.DATA_IMPORT_DESC);
        
        // 系统管理权限
        createAuthorityIfNotExists(AuthorityConstants.SYSTEM_READ, AuthorityConstants.SYSTEM_READ_DESC);
        createAuthorityIfNotExists(AuthorityConstants.SYSTEM_UPDATE, AuthorityConstants.SYSTEM_UPDATE_DESC);
        createAuthorityIfNotExists(AuthorityConstants.LOG_READ, AuthorityConstants.LOG_READ_DESC);
        createAuthorityIfNotExists(AuthorityConstants.LOG_DELETE, AuthorityConstants.LOG_DELETE_DESC);
        
        log.info("权限数据初始化完成");
    }

    /**
     * 初始化角色
     */
    private void initRoles() {
        log.info("初始化角色数据...");
        
        // 创建超级管理员角色
        createRoleIfNotExists(RoleConstants.ROLE_SUPER_ADMIN, RoleConstants.ROLE_SUPER_ADMIN_DESC);
        
        // 创建普通用户角色
        createRoleIfNotExists(RoleConstants.ROLE_USER, RoleConstants.ROLE_USER_DESC);
        
        log.info("角色数据初始化完成");
    }

    /**
     * 为角色分配权限
     */
    private void assignAuthoritiesToRoles() {
        log.info("为角色分配权限...");
        
        // 获取角色
        SwiftRole superAdminRole = roleMapper.findByName(RoleConstants.ROLE_SUPER_ADMIN);
        SwiftRole userRole = roleMapper.findByName(RoleConstants.ROLE_USER);
        
        if (superAdminRole != null && userRole != null) {
            // 超级管理员拥有所有权限
            grantAllAuthoritiesToRole(superAdminRole);
            
            // 普通用户拥有基本业务数据权限
            grantBasicAuthoritiesToRole(userRole);
            
            log.info("角色权限分配完成");
        } else {
            log.warn("角色不存在，无法分配权限");
        }
    }

    /**
     * 创建权限（如果不存在）
     */
    private void createAuthorityIfNotExists(String name, String description) {
        SwiftAuthority authority = authorityMapper.findByName(name);
        if (authority == null) {
            authority = new SwiftAuthority();
            authority.setAuthorityId(UUID.randomUUID());
            authority.setName(name);
            authority.setDescription(description);
            authorityMapper.insert(authority);
            log.debug("创建权限: {}", name);
        }
    }

    /**
     * 创建角色（如果不存在）
     */
    private void createRoleIfNotExists(String name, String description) {
        SwiftRole role = roleMapper.findByName(name);
        if (role == null) {
            role = new SwiftRole();
            role.setRoleId(UUID.randomUUID());
            role.setName(name);
            role.setDescription(description);
            roleMapper.insert(role);
            log.debug("创建角色: {}", name);
        }
    }

    /**
     * 为超级管理员角色分配所有权限
     */
    private void grantAllAuthoritiesToRole(SwiftRole role) {
        List<SwiftAuthority> allAuthorities = authorityMapper.findAll();
        for (SwiftAuthority authority : allAuthorities) {
            if (!roleAuthorityMapper.exists(role.getRoleId(), authority.getAuthorityId())) {
                roleAuthorityMapper.insert(role.getRoleId(), authority.getAuthorityId());
                log.debug("为角色 {} 分配权限: {}", role.getName(), authority.getName());
            }
        }
    }

    /**
     * 为普通用户角色分配基本权限
     */
    private void grantBasicAuthoritiesToRole(SwiftRole role) {
        // 普通用户只有基本的业务数据读取和创建权限
        String[] basicAuthorities = {
            AuthorityConstants.DATA_READ,
            AuthorityConstants.DATA_CREATE,
            AuthorityConstants.DATA_UPDATE
        };
        
        for (String authorityName : basicAuthorities) {
            SwiftAuthority authority = authorityMapper.findByName(authorityName);
            if (authority != null && !roleAuthorityMapper.exists(role.getRoleId(), authority.getAuthorityId())) {
                roleAuthorityMapper.insert(role.getRoleId(), authority.getAuthorityId());
                log.debug("为角色 {} 分配权限: {}", role.getName(), authority.getName());
            }
        }
    }
}

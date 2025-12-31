package com.star.swiftSecurity.constant;

/**
 * 权限常量
 *
 * @author SHOOTING_STAR_C
 */
public class AuthorityConstants {
    // ==================== 用户管理权限 ====================
    
    /**
     * 查看用户列表
     */
    public static final String USER_READ = "user:read";

    /**
     * 创建用户
     */
    public static final String USER_CREATE = "user:create";

    /**
     * 更新用户信息
     */
    public static final String USER_UPDATE = "user:update";

    /**
     * 删除用户
     */
    public static final String USER_DELETE = "user:delete";

    /**
     * 重置用户密码
     */
    public static final String USER_RESET_PASSWORD = "user:reset_password";

    /**
     * 锁定/解锁用户账户
     */
    public static final String USER_LOCK = "user:lock";

    // ==================== 角色管理权限 ====================
    
    /**
     * 查看角色列表
     */
    public static final String ROLE_READ = "role:read";

    /**
     * 创建角色
     */
    public static final String ROLE_CREATE = "role:create";

    /**
     * 更新角色信息
     */
    public static final String ROLE_UPDATE = "role:update";

    /**
     * 删除角色
     */
    public static final String ROLE_DELETE = "role:delete";

    /**
     * 分配权限给角色
     */
    public static final String ROLE_GRANT_AUTHORITY = "role:grant_authority";

    /**
     * 收回角色权限
     */
    public static final String ROLE_REVOKE_AUTHORITY = "role:revoke_authority";

    // ==================== 权限管理权限 ====================
    
    /**
     * 查看权限列表
     */
    public static final String AUTHORITY_READ = "authority:read";

    /**
     * 创建权限
     */
    public static final String AUTHORITY_CREATE = "authority:create";

    /**
     * 更新权限信息
     */
    public static final String AUTHORITY_UPDATE = "authority:update";

    /**
     * 删除权限
     */
    public static final String AUTHORITY_DELETE = "authority:delete";

    // ==================== 业务数据权限 ====================
    
    /**
     * 查看业务数据
     */
    public static final String DATA_READ = "data:read";

    /**
     * 创建业务数据
     */
    public static final String DATA_CREATE = "data:create";

    /**
     * 更新业务数据
     */
    public static final String DATA_UPDATE = "data:update";

    /**
     * 删除业务数据
     */
    public static final String DATA_DELETE = "data:delete";

    /**
     * 导出数据
     */
    public static final String DATA_EXPORT = "data:export";

    /**
     * 导入数据
     */
    public static final String DATA_IMPORT = "data:import";

    // ==================== 系统管理权限 ====================
    
    /**
     * 查看系统配置
     */
    public static final String SYSTEM_READ = "system:read";

    /**
     * 更新系统配置
     */
    public static final String SYSTEM_UPDATE = "system:update";

    /**
     * 查看系统日志
     */
    public static final String LOG_READ = "log:read";

    /**
     * 清理系统日志
     */
    public static final String LOG_DELETE = "log:delete";

    // ==================== 描述信息 ====================
    
    public static final String USER_READ_DESC = "查看用户列表";
    public static final String USER_CREATE_DESC = "创建用户";
    public static final String USER_UPDATE_DESC = "更新用户信息";
    public static final String USER_DELETE_DESC = "删除用户";
    public static final String USER_RESET_PASSWORD_DESC = "重置用户密码";
    public static final String USER_LOCK_DESC = "锁定/解锁用户账户";

    public static final String ROLE_READ_DESC = "查看角色列表";
    public static final String ROLE_CREATE_DESC = "创建角色";
    public static final String ROLE_UPDATE_DESC = "更新角色信息";
    public static final String ROLE_DELETE_DESC = "删除角色";
    public static final String ROLE_GRANT_AUTHORITY_DESC = "分配权限给角色";
    public static final String ROLE_REVOKE_AUTHORITY_DESC = "收回角色权限";

    public static final String AUTHORITY_READ_DESC = "查看权限列表";
    public static final String AUTHORITY_CREATE_DESC = "创建权限";
    public static final String AUTHORITY_UPDATE_DESC = "更新权限信息";
    public static final String AUTHORITY_DELETE_DESC = "删除权限";

    public static final String DATA_READ_DESC = "查看业务数据";
    public static final String DATA_CREATE_DESC = "创建业务数据";
    public static final String DATA_UPDATE_DESC = "更新业务数据";
    public static final String DATA_DELETE_DESC = "删除业务数据";
    public static final String DATA_EXPORT_DESC = "导出数据";
    public static final String DATA_IMPORT_DESC = "导入数据";

    public static final String SYSTEM_READ_DESC = "查看系统配置";
    public static final String SYSTEM_UPDATE_DESC = "更新系统配置";
    public static final String LOG_READ_DESC = "查看系统日志";
    public static final String LOG_DELETE_DESC = "清理系统日志";
}

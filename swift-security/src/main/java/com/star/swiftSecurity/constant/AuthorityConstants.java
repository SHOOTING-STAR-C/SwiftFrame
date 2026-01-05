package com.star.swiftSecurity.constant;

/**
 * 权限常量
 *
 * @author SHOOTING_STAR_C
 */
public class AuthorityConstants {
    // ==================== 用户管理权限 ====================
    
    /**
     * 查看用户列表和详情
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
     * 修改用户密码
     */
    public static final String USER_PASSWORD = "user:password";

    /**
     * 用户管理（启用/禁用/锁定/解锁/分配角色）
     */
    public static final String USER_MANAGE = "user:manage";

    // ==================== 角色管理权限 ====================
    
    /**
     * 查看角色列表和详情
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
     * 角色管理（分配/收回权限）
     */
    public static final String ROLE_MANAGE = "role:manage";

    // ==================== 权限管理权限 ====================
    
    /**
     * 查看权限列表和详情
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

    // ==================== AI供应商管理权限 ====================
    
    /**
     * 查看供应商列表和详情
     */
    public static final String AI_PROVIDER_READ = "ai:provider:read";

    /**
     * 创建供应商
     */
    public static final String AI_PROVIDER_CREATE = "ai:provider:create";

    /**
     * 更新供应商
     */
    public static final String AI_PROVIDER_UPDATE = "ai:provider:update";

    /**
     * 删除供应商
     */
    public static final String AI_PROVIDER_DELETE = "ai:provider:delete";

    /**
     * 测试供应商连接
     */
    public static final String AI_PROVIDER_TEST = "ai:provider:test";

    // ==================== AI模型管理权限 ====================
    
    /**
     * 查看模型列表和详情
     */
    public static final String AI_MODEL_READ = "ai:model:read";

    /**
     * 创建模型
     */
    public static final String AI_MODEL_CREATE = "ai:model:create";

    /**
     * 更新模型
     */
    public static final String AI_MODEL_UPDATE = "ai:model:update";

    /**
     * 删除模型
     */
    public static final String AI_MODEL_DELETE = "ai:model:delete";

    /**
     * 测试模型
     */
    public static final String AI_MODEL_TEST = "ai:model:test";

    // ==================== AI聊天权限 ====================
    
    /**
     * 发送聊天消息
     */
    public static final String AI_CHAT_SEND = "ai:chat:send";

    /**
     * 查看聊天历史
     */
    public static final String AI_CHAT_HISTORY = "ai:chat:history";

    // ==================== AI会话管理权限 ====================
    
    /**
     * 查看会话列表和详情
     */
    public static final String AI_SESSION_READ = "ai:session:read";

    /**
     * 创建会话
     */
    public static final String AI_SESSION_CREATE = "ai:session:create";

    /**
     * 更新会话
     */
    public static final String AI_SESSION_UPDATE = "ai:session:update";

    /**
     * 删除会话
     */
    public static final String AI_SESSION_DELETE = "ai:session:delete";

    // ==================== 系统配置管理权限 ====================
    
    /**
     * 查看配置
     */
    public static final String CONFIG_READ = "config:read";

    /**
     * 创建配置
     */
    public static final String CONFIG_CREATE = "config:create";

    /**
     * 更新配置
     */
    public static final String CONFIG_UPDATE = "config:update";

    /**
     * 删除配置
     */
    public static final String CONFIG_DELETE = "config:delete";

    /**
     * 刷新配置缓存
     */
    public static final String CONFIG_REFRESH = "config:refresh";

    // ==================== 系统监控权限 ====================
    
    /**
     * 查看系统监控数据
     */
    public static final String MONITOR_VIEW = "monitor:view";

    // ==================== 描述信息 ====================
    
    public static final String USER_READ_DESC = "查看用户列表和详情";
    public static final String USER_CREATE_DESC = "创建用户";
    public static final String USER_UPDATE_DESC = "更新用户信息";
    public static final String USER_DELETE_DESC = "删除用户";
    public static final String USER_PASSWORD_DESC = "修改用户密码";
    public static final String USER_MANAGE_DESC = "用户管理（启用/禁用/锁定/解锁/分配角色）";

    public static final String ROLE_READ_DESC = "查看角色列表和详情";
    public static final String ROLE_CREATE_DESC = "创建角色";
    public static final String ROLE_UPDATE_DESC = "更新角色信息";
    public static final String ROLE_DELETE_DESC = "删除角色";
    public static final String ROLE_MANAGE_DESC = "角色管理（分配/收回权限）";

    public static final String AUTHORITY_READ_DESC = "查看权限列表和详情";
    public static final String AUTHORITY_CREATE_DESC = "创建权限";
    public static final String AUTHORITY_UPDATE_DESC = "更新权限信息";
    public static final String AUTHORITY_DELETE_DESC = "删除权限";

    public static final String AI_PROVIDER_READ_DESC = "查看供应商列表和详情";
    public static final String AI_PROVIDER_CREATE_DESC = "创建供应商";
    public static final String AI_PROVIDER_UPDATE_DESC = "更新供应商";
    public static final String AI_PROVIDER_DELETE_DESC = "删除供应商";
    public static final String AI_PROVIDER_TEST_DESC = "测试供应商连接";

    public static final String AI_MODEL_READ_DESC = "查看模型列表和详情";
    public static final String AI_MODEL_CREATE_DESC = "创建模型";
    public static final String AI_MODEL_UPDATE_DESC = "更新模型";
    public static final String AI_MODEL_DELETE_DESC = "删除模型";
    public static final String AI_MODEL_TEST_DESC = "测试模型";

    public static final String AI_CHAT_SEND_DESC = "发送聊天消息";
    public static final String AI_CHAT_HISTORY_DESC = "查看聊天历史";

    public static final String AI_SESSION_READ_DESC = "查看会话列表和详情";
    public static final String AI_SESSION_CREATE_DESC = "创建会话";
    public static final String AI_SESSION_UPDATE_DESC = "更新会话";
    public static final String AI_SESSION_DELETE_DESC = "删除会话";

    public static final String CONFIG_READ_DESC = "查看配置";
    public static final String CONFIG_CREATE_DESC = "创建配置";
    public static final String CONFIG_UPDATE_DESC = "更新配置";
    public static final String CONFIG_DELETE_DESC = "删除配置";
    public static final String CONFIG_REFRESH_DESC = "刷新配置缓存";

    public static final String MONITOR_VIEW_DESC = "查看系统监控数据";
}

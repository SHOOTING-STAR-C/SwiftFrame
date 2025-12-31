-- ========================================
-- 安全数据初始化脚本
-- 包含角色和权限的基础数据
-- ========================================

-- ==================== 权限数据 ====================

-- 用户管理权限
INSERT IGNORE INTO swift_authorities (authority_id, name, description) VALUES 
(UUID_TO_BIN(UUID()), 'user:read', '查看用户列表'),
(UUID_TO_BIN(UUID()), 'user:create', '创建用户'),
(UUID_TO_BIN(UUID()), 'user:update', '更新用户信息'),
(UUID_TO_BIN(UUID()), 'user:delete', '删除用户'),
(UUID_TO_BIN(UUID()), 'user:reset_password', '重置用户密码'),
(UUID_TO_BIN(UUID()), 'user:lock', '锁定/解锁用户账户');

-- 角色管理权限
INSERT IGNORE INTO swift_authorities (authority_id, name, description) VALUES 
(UUID_TO_BIN(UUID()), 'role:read', '查看角色列表'),
(UUID_TO_BIN(UUID()), 'role:create', '创建角色'),
(UUID_TO_BIN(UUID()), 'role:update', '更新角色信息'),
(UUID_TO_BIN(UUID()), 'role:delete', '删除角色'),
(UUID_TO_BIN(UUID()), 'role:grant_authority', '分配权限给角色'),
(UUID_TO_BIN(UUID()), 'role:revoke_authority', '收回角色权限');

-- 权限管理权限
INSERT IGNORE INTO swift_authorities (authority_id, name, description) VALUES 
(UUID_TO_BIN(UUID()), 'authority:read', '查看权限列表'),
(UUID_TO_BIN(UUID()), 'authority:create', '创建权限'),
(UUID_TO_BIN(UUID()), 'authority:update', '更新权限信息'),
(UUID_TO_BIN(UUID()), 'authority:delete', '删除权限');

-- 业务数据权限
INSERT IGNORE INTO swift_authorities (authority_id, name, description) VALUES 
(UUID_TO_BIN(UUID()), 'data:read', '查看业务数据'),
(UUID_TO_BIN(UUID()), 'data:create', '创建业务数据'),
(UUID_TO_BIN(UUID()), 'data:update', '更新业务数据'),
(UUID_TO_BIN(UUID()), 'data:delete', '删除业务数据'),
(UUID_TO_BIN(UUID()), 'data:export', '导出数据'),
(UUID_TO_BIN(UUID()), 'data:import', '导入数据');

-- 系统管理权限
INSERT IGNORE INTO swift_authorities (authority_id, name, description) VALUES 
(UUID_TO_BIN(UUID()), 'system:read', '查看系统配置'),
(UUID_TO_BIN(UUID()), 'system:update', '更新系统配置'),
(UUID_TO_BIN(UUID()), 'log:read', '查看系统日志'),
(UUID_TO_BIN(UUID()), 'log:delete', '清理系统日志');

-- ==================== 角色数据 ====================

-- 超级管理员角色
SET @super_admin_role_id = UUID_TO_BIN(UUID());
INSERT IGNORE INTO swift_roles (role_id, name, description) VALUES 
(@super_admin_role_id, 'ROLE_SUPER_ADMIN', '超级管理员，拥有所有权限');

-- 普通用户角色
SET @user_role_id = UUID_TO_BIN(UUID());
INSERT IGNORE INTO swift_roles (role_id, name, description) VALUES 
(@user_role_id, 'ROLE_USER', '普通用户，拥有基本权限');

-- ==================== 角色权限关联 ====================

-- 为超级管理员分配所有权限
INSERT IGNORE INTO swift_role_authorities (role_id, authority_id)
SELECT @super_admin_role_id, authority_id FROM swift_authorities;

-- 为普通用户分配基本权限
INSERT IGNORE INTO swift_role_authorities (role_id, authority_id)
SELECT @user_role_id, authority_id FROM swift_authorities 
WHERE name IN ('data:read', 'data:create', 'data:update');

-- ==================== 默认管理员用户 ====================
-- 注意：默认用户现在通过应用程序的 SecurityDataInitializer 初始化
-- 这样可以确保密码使用正确的 BCryptPasswordEncoder 加密
-- 
-- 默认管理员账户：
--   用户名: admin
--   密码: admin123
--   邮箱: admin@swift.com
--   角色: ROLE_SUPER_ADMIN
--
-- 默认普通用户：
--   用户名: user
--   密码: user123
--   邮箱: user@swift.com
--   角色: ROLE_USER

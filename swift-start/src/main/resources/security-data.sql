-- ========================================
-- 安全数据初始化脚本
-- 包含角色和权限的基础数据
-- ========================================

-- ==================== 权限数据 ====================

-- 用户管理权限
INSERT INTO swift_authorities (authority_id, name, description) VALUES 
(UUID_TO_BIN(UUID()), 'user:read', '查看用户列表'),
(UUID_TO_BIN(UUID()), 'user:create', '创建用户'),
(UUID_TO_BIN(UUID()), 'user:update', '更新用户信息'),
(UUID_TO_BIN(UUID()), 'user:delete', '删除用户'),
(UUID_TO_BIN(UUID()), 'user:reset_password', '重置用户密码'),
(UUID_TO_BIN(UUID()), 'user:lock', '锁定/解锁用户账户');

-- 角色管理权限
INSERT INTO swift_authorities (authority_id, name, description) VALUES 
(UUID_TO_BIN(UUID()), 'role:read', '查看角色列表'),
(UUID_TO_BIN(UUID()), 'role:create', '创建角色'),
(UUID_TO_BIN(UUID()), 'role:update', '更新角色信息'),
(UUID_TO_BIN(UUID()), 'role:delete', '删除角色'),
(UUID_TO_BIN(UUID()), 'role:grant_authority', '分配权限给角色'),
(UUID_TO_BIN(UUID()), 'role:revoke_authority', '收回角色权限');

-- 权限管理权限
INSERT INTO swift_authorities (authority_id, name, description) VALUES 
(UUID_TO_BIN(UUID()), 'authority:read', '查看权限列表'),
(UUID_TO_BIN(UUID()), 'authority:create', '创建权限'),
(UUID_TO_BIN(UUID()), 'authority:update', '更新权限信息'),
(UUID_TO_BIN(UUID()), 'authority:delete', '删除权限');

-- 业务数据权限
INSERT INTO swift_authorities (authority_id, name, description) VALUES 
(UUID_TO_BIN(UUID()), 'data:read', '查看业务数据'),
(UUID_TO_BIN(UUID()), 'data:create', '创建业务数据'),
(UUID_TO_BIN(UUID()), 'data:update', '更新业务数据'),
(UUID_TO_BIN(UUID()), 'data:delete', '删除业务数据'),
(UUID_TO_BIN(UUID()), 'data:export', '导出数据'),
(UUID_TO_BIN(UUID()), 'data:import', '导入数据');

-- 系统管理权限
INSERT INTO swift_authorities (authority_id, name, description) VALUES 
(UUID_TO_BIN(UUID()), 'system:read', '查看系统配置'),
(UUID_TO_BIN(UUID()), 'system:update', '更新系统配置'),
(UUID_TO_BIN(UUID()), 'log:read', '查看系统日志'),
(UUID_TO_BIN(UUID()), 'log:delete', '清理系统日志');

-- ==================== 角色数据 ====================

-- 超级管理员角色
SET @super_admin_role_id = UUID_TO_BIN(UUID());
INSERT INTO swift_roles (role_id, name, description) VALUES 
(@super_admin_role_id, 'ROLE_SUPER_ADMIN', '超级管理员，拥有所有权限');

-- 普通用户角色
SET @user_role_id = UUID_TO_BIN(UUID());
INSERT INTO swift_roles (role_id, name, description) VALUES 
(@user_role_id, 'ROLE_USER', '普通用户，拥有基本权限');

-- ==================== 角色权限关联 ====================

-- 为超级管理员分配所有权限
INSERT INTO swift_role_authorities (role_id, authority_id)
SELECT @super_admin_role_id, authority_id FROM swift_authorities;

-- 为普通用户分配基本权限
INSERT INTO swift_role_authorities (role_id, authority_id)
SELECT @user_role_id, authority_id FROM swift_authorities 
WHERE name IN ('data:read', 'data:create', 'data:update');

-- ==================== 默认管理员用户 ====================

-- 创建默认超级管理员用户
-- 默认用户名: admin
-- 默认密码: admin123 (需要通过应用程序进行加密)
-- 注意：实际使用时应该修改默认密码
SET @admin_user_id = UUID_TO_BIN(UUID());
INSERT INTO swift_users (user_id, username, full_name, password, email, enabled) VALUES 
(@admin_user_id, 'admin', '系统管理员', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'admin@swift.com', TRUE);

-- 为管理员分配超级管理员角色
INSERT INTO swift_user_roles (user_id, role_id) VALUES 
(@admin_user_id, @super_admin_role_id);

-- 创建默认普通用户
-- 默认用户名: user
-- 默认密码: user123 (需要通过应用程序进行加密)
SET @normal_user_id = UUID_TO_BIN(UUID());
INSERT INTO swift_users (user_id, username, full_name, password, email, enabled) VALUES 
(@normal_user_id, 'user', '普通用户', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'user@swift.com', TRUE);

-- 为普通用户分配用户角色
INSERT INTO swift_user_roles (user_id, role_id) VALUES 
(@normal_user_id, @user_role_id);

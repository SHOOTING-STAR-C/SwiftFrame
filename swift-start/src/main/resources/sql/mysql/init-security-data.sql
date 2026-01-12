-- ============================================
-- 安全数据初始化脚本
-- 说明：初始化5个角色、38个权限及权限分配
-- ============================================

-- 1. 初始化权限数据
INSERT IGNORE INTO swift_authorities (authority_id, name, description, created_at) VALUES
-- 用户管理权限 (6个)
(1, 'user:read', '查看用户列表和详情', NOW()),
(2, 'user:create', '创建用户', NOW()),
(3, 'user:update', '更新用户信息', NOW()),
(4, 'user:delete', '删除用户', NOW()),
(5, 'user:password', '修改用户密码', NOW()),
(6, 'user:manage', '用户管理（启用/禁用/锁定/解锁/分配角色）', NOW()),

-- 角色管理权限 (5个)
(7, 'role:read', '查看角色列表和详情', NOW()),
(8, 'role:create', '创建角色', NOW()),
(9, 'role:update', '更新角色信息', NOW()),
(10, 'role:delete', '删除角色', NOW()),
(11, 'role:manage', '角色管理（分配/收回权限）', NOW()),

-- 权限管理权限 (4个)
(12, 'authority:read', '查看权限列表和详情', NOW()),
(13, 'authority:create', '创建权限', NOW()),
(14, 'authority:update', '更新权限信息', NOW()),
(15, 'authority:delete', '删除权限', NOW()),

-- AI供应商管理权限 (5个)
(16, 'ai:provider:read', '查看供应商列表和详情', NOW()),
(17, 'ai:provider:create', '创建供应商', NOW()),
(18, 'ai:provider:update', '更新供应商', NOW()),
(19, 'ai:provider:delete', '删除供应商', NOW()),
(20, 'ai:provider:test', '测试供应商连接', NOW()),

-- AI模型管理权限 (5个)
(21, 'ai:model:read', '查看模型列表和详情', NOW()),
(22, 'ai:model:create', '创建模型', NOW()),
(23, 'ai:model:update', '更新模型', NOW()),
(24, 'ai:model:delete', '删除模型', NOW()),
(25, 'ai:model:test', '测试模型', NOW()),

-- AI聊天权限 (3个)
(26, 'ai:chat:send', '发送聊天消息', NOW()),
(27, 'ai:chat:history', '查看聊天历史', NOW()),
(38, 'ai:message:read', '查看聊天消息', NOW()),

-- AI会话管理权限 (4个)
(28, 'ai:session:read', '查看会话列表和详情', NOW()),
(29, 'ai:session:create', '创建会话', NOW()),
(30, 'ai:session:update', '更新会话', NOW()),
(31, 'ai:session:delete', '删除会话', NOW()),

-- 系统配置管理权限 (5个)
(32, 'config:read', '查看配置', NOW()),
(33, 'config:create', '创建配置', NOW()),
(34, 'config:update', '更新配置', NOW()),
(35, 'config:delete', '删除配置', NOW()),
(36, 'config:refresh', '刷新配置缓存', NOW()),

-- 系统监控权限 (1个)
(37, 'monitor:view', '查看系统监控数据', NOW());

-- 2. 初始化角色数据
INSERT IGNORE INTO swift_roles (role_id, name, description, created_at) VALUES
(1, 'ROLE_SUPER_ADMIN', '超级管理员，拥有所有权限', NOW()),
(2, 'ROLE_SYSTEM_ADMIN', '系统管理员，负责系统配置和监控管理', NOW()),
(3, 'ROLE_USER_ADMIN', '用户管理员，负责用户、角色和权限管理', NOW()),
(4, 'ROLE_AI_ADMIN', 'AI管理员，负责AI供应商和模型管理', NOW()),
(5, 'ROLE_USER', '普通用户，可以使用AI聊天和会话管理功能', NOW());

-- 3. 为超级管理员分配所有权限 (38个权限)
INSERT IGNORE INTO swift_role_authorities (role_id, authority_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6),  -- 用户管理
(1, 7), (1, 8), (1, 9), (1, 10), (1, 11),         -- 角色管理
(1, 12), (1, 13), (1, 14), (1, 15),                -- 权限管理
(1, 16), (1, 17), (1, 18), (1, 19), (1, 20),       -- AI供应商
(1, 21), (1, 22), (1, 23), (1, 24), (1, 25),       -- AI模型
(1, 26), (1, 27), (1, 38),                           -- AI聊天
(1, 28), (1, 29), (1, 30), (1, 31),                 -- AI会话
(1, 32), (1, 33), (1, 34), (1, 35), (1, 36),       -- 系统配置
(1, 37);                                             -- 系统监控

-- 4. 为系统管理员分配权限 (6个权限)
INSERT IGNORE INTO swift_role_authorities (role_id, authority_id) VALUES
(2, 32), (2, 33), (2, 34), (2, 35), (2, 36),  -- 系统配置管理
(2, 37);                                         -- 系统监控

-- 5. 为用户管理员分配权限 (15个权限)
INSERT IGNORE INTO swift_role_authorities (role_id, authority_id) VALUES
(3, 1), (3, 2), (3, 3), (3, 4), (3, 5), (3, 6),   -- 用户管理
(3, 7), (3, 8), (3, 9), (3, 10), (3, 11),          -- 角色管理
(3, 12), (3, 13), (3, 14), (3, 15);                 -- 权限管理

-- 6. 为AI管理员分配权限 (10个权限)
INSERT IGNORE INTO swift_role_authorities (role_id, authority_id) VALUES
(4, 16), (4, 17), (4, 18), (4, 19), (4, 20),  -- AI供应商管理
(4, 21), (4, 22), (4, 23), (4, 24), (4, 25);  -- AI模型管理

-- 7. 为普通用户分配权限 (9个权限)
INSERT IGNORE INTO swift_role_authorities (role_id, authority_id) VALUES
(5, 16), (5, 21),              -- AI供应商和模型查看
(5, 26), (5, 27), (5, 38),     -- AI聊天
(5, 28), (5, 29), (5, 30), (5, 31);  -- AI会话管理

-- ============================================
-- 执行完成说明
-- ============================================
-- 已初始化：
-- - 38个权限
-- - 5个角色
-- - 角色权限分配
-- 
-- 注意：超级管理员账号将在应用首次启动时自动创建
-- 随机生成的密码将输出到控制台日志中
-- 
-- 角色权限分配说明：
-- 1. 超级管理员：所有38个权限
-- 2. 系统管理员：6个权限（系统配置+监控）
-- 3. 用户管理员：15个权限（用户+角色+权限管理）
-- 4. AI管理员：10个权限（AI供应商+模型管理）
-- 5. 普通用户：9个权限（AI供应商查看+AI模型查看+AI聊天+AI会话管理）
-- ============================================

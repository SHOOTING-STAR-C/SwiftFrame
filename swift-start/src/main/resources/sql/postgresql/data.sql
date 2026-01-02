-- ========================================
-- 数据初始化脚本
-- ========================================
-- 
-- 注意：所有安全数据（权限、角色、用户）现在通过应用程序的
-- SecurityDataInitializer 初始化，使用雪花ID生成器生成ID。
-- 
-- 这样可以确保：
-- 1. ID使用雪花算法生成（BIGINT类型）
-- 2. 密码使用正确的 BCryptPasswordEncoder 加密
-- 3. 数据初始化逻辑统一管理
--
-- 默认管理员账户：
--   用户名: admin
--   密码: admin123
--   邮箱: admin@swift.com
--   角色: ROLE_SUPER_ADMIN

-- ========================================
-- 系统配置数据初始化
-- ========================================
-- 
-- 此脚本用于初始化AI配置和邮件配置到sys_config表
-- 所有敏感配置值使用DEC()标记，会自动加密存储
--
-- 注意：执行此脚本前请确保sys_config表已创建

-- ========================================
-- 邮件配置
-- ========================================

-- 邮件服务器主机
INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('spring.mail.host', 'smtp.qq.com', 'MAIL', '邮件服务器主机', true, 'system', 'system')
ON CONFLICT (config_key) DO NOTHING;

-- 邮件服务器端口
INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('spring.mail.port', '587', 'MAIL', '邮件服务器端口', true, 'system', 'system')
ON CONFLICT (config_key) DO NOTHING;

-- 邮件发送用户名
INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('spring.mail.username', 'ENC(CmdFJR6+yQpy0xJL86pZE6DkyY4HRW9taGf6Yqs38dv3F+H6+ZjEFIXJdriX)', 'MAIL', '邮件发送用户名', true, 'system', 'system')
ON CONFLICT (config_key) DO NOTHING;

-- 邮件发送密码
INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('spring.mail.password', 'ENC(AKJcLmqL0cGpUOv98gGajK8sM7CGnmN4MiMXoaylCjxVBYKwWXk6ZkH669c=)', 'MAIL', '邮件发送密码', true, 'system', 'system')
ON CONFLICT (config_key) DO NOTHING;

-- 邮件发送者（发件人）
INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('spring.mail.from', 'swift', 'MAIL', '邮件发送者（发件人）', true, 'system', 'system')
ON CONFLICT (config_key) DO NOTHING;

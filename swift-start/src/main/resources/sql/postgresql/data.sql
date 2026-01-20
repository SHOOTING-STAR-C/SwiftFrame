-- ========================================
-- 数据初始化脚本
-- ========================================

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
VALUES ('spring.mail.username', 'ENC(nqci4K/ZHx1pws68iFkUHeg3Vv2sz/0kPZal2Ec3xm0oeMZa7cWhE9DRp0tr)', 'MAIL', '邮件发送用户名', true, 'system', 'system')
ON CONFLICT (config_key) DO NOTHING;

-- 邮件发送密码
INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('spring.mail.password', 'ENC(uIWEtSltWoG84fxQmKwZj8gA8HTa+siIz+TasNCkEnLu1SEDJ69QWp8vpZ4=)', 'MAIL', '邮件发送密码', true, 'system', 'system')
ON CONFLICT (config_key) DO NOTHING;

-- 邮件发送者（发件人）
INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('spring.mail.from', 'swift', 'MAIL', '邮件发送者（发件人）', true, 'system', 'system')
ON CONFLICT (config_key) DO NOTHING;

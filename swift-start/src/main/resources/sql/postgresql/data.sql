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
VALUES ('spring.mail.username', 'ENC(U3vN3o11bMilFc/ljozWnhcGCOWpIqCnNntlCQq7cwX97TXlm6DxPQN7VKJ/)', 'MAIL', '邮件发送用户名', true, 'system', 'system')
ON CONFLICT (config_key) DO NOTHING;

-- 邮件发送密码
INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('spring.mail.password', 'ENC(5XsATWwPRdK0plZa1fRE25CZ7x7bKVtvFaRCw07uOtnydkb8Svq5V0MIDkQ=)', 'MAIL', '邮件发送密码', true, 'system', 'system')
ON CONFLICT (config_key) DO NOTHING;

-- 邮件发送者（发件人）
INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('spring.mail.from', 'swift', 'MAIL', '邮件发送者（发件人）', true, 'system', 'system')
ON CONFLICT (config_key) DO NOTHING;

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
-- AI配置
-- ========================================

-- AI模块启用状态
INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('swift.ai.enabled', 'true', 'AI', '是否启用AI模块', true, 'system', 'system');

-- 默认AI服务商
INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('swift.ai.default-provider', 'CUSTOM', 'AI', '默认AI服务商', true, 'system', 'system');

-- OpenAI配置
INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('swift.ai.providers.openai.enabled', 'false', 'AI', '是否启用OpenAI', false, 'system', 'system');

INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('swift.ai.providers.openai.api-key', 'DEC()', 'AI', 'OpenAI API密钥', false, 'system', 'system');

INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('swift.ai.providers.openai.base-url', '', 'AI', 'OpenAI Base URL', false, 'system', 'system');

INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('swift.ai.providers.openai.model', 'gpt-4o', 'AI', 'OpenAI默认模型', false, 'system', 'system');

INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('swift.ai.providers.openai.timeout', '30', 'AI', 'OpenAI超时时间（秒）', false, 'system', 'system');

INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('swift.ai.providers.openai.temperature', '0.7', 'AI', 'OpenAI默认温度值', false, 'system', 'system');

INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('swift.ai.providers.openai.max-retries', '3', 'AI', 'OpenAI最大重试次数', false, 'system', 'system');

-- DeepSeek配置
INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('swift.ai.providers.deepseek.enabled', 'false', 'AI', '是否启用DeepSeek', false, 'system', 'system');

INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('swift.ai.providers.deepseek.api-key', 'DEC()', 'AI', 'DeepSeek API密钥', false, 'system', 'system');

INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('swift.ai.providers.deepseek.base-url', '', 'AI', 'DeepSeek Base URL', false, 'system', 'system');

INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('swift.ai.providers.deepseek.model', 'deepseek-chat', 'AI', 'DeepSeek默认模型', false, 'system', 'system');

INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('swift.ai.providers.deepseek.timeout', '30', 'AI', 'DeepSeek超时时间（秒）', false, 'system', 'system');

INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('swift.ai.providers.deepseek.temperature', '0.7', 'AI', 'DeepSeek默认温度值', false, 'system', 'system');

INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('swift.ai.providers.deepseek.max-retries', '3', 'AI', 'DeepSeek最大重试次数', false, 'system', 'system');

-- 自定义AI服务商配置
INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('swift.ai.providers.custom.enabled', 'true', 'AI', '是否启用自定义AI服务商', true, 'system', 'system');

INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('swift.ai.providers.custom.api-key', 'DEC()', 'AI', '自定义AI服务商API密钥', true, 'system', 'system');

INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('swift.ai.providers.custom.base-url', '', 'AI', '自定义AI服务商Base URL', true, 'system', 'system');

INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('swift.ai.providers.custom.model', 'custom-model', 'AI', '自定义AI服务商默认模型', true, 'system', 'system');

INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('swift.ai.providers.custom.timeout', '30', 'AI', '自定义AI服务商超时时间（秒）', true, 'system', 'system');

INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('swift.ai.providers.custom.temperature', '0.7', 'AI', '自定义AI服务商默认温度值', true, 'system', 'system');

INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('swift.ai.providers.custom.max-retries', '3', 'AI', '自定义AI服务商最大重试次数', true, 'system', 'system');

-- ========================================
-- 邮件配置
-- ========================================

-- 邮件服务器主机
INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('spring.mail.host', 'DEC(smtp.qq.com)', 'MAIL', '邮件服务器主机', true, 'system', 'system');

-- 邮件服务器端口
INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('spring.mail.port', '587', 'MAIL', '邮件服务器端口', true, 'system', 'system');

-- 邮件发送用户名
INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('spring.mail.username', 'DEC(2242620219@qq.com)', 'MAIL', '邮件发送用户名', true, 'system', 'system');

-- 邮件发送密码
INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('spring.mail.password', 'DEC(bmcycvxtkpmgeace)', 'MAIL', '邮件发送密码', true, 'system', 'system');

-- 邮件发送者（发件人）
INSERT INTO sys_config (config_key, config_value, config_type, description, is_enabled, created_by, updated_by)
VALUES ('spring.mail.from', 'DEC(swift)', 'MAIL', '邮件发送者（发件人）', true, 'system', 'system');

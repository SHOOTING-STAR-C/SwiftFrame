-- AI模块数据库表结构 (PostgreSQL)
-- 创建时间: 2024-01-01

-- 供应商表
CREATE TABLE IF NOT EXISTS ai_provider (
    id BIGSERIAL PRIMARY KEY,
    provider_name VARCHAR(100) NOT NULL,
    provider_code VARCHAR(50),
    base_url VARCHAR(255),
    api_key VARCHAR(500) NOT NULL,
    timeout INTEGER DEFAULT 60,
    max_retries INTEGER DEFAULT 3,
    enabled BOOLEAN DEFAULT TRUE,
    priority INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE ai_provider IS 'AI供应商表';
COMMENT ON COLUMN ai_provider.id IS '主键ID';
COMMENT ON COLUMN ai_provider.provider_name IS '提供商名称';
COMMENT ON COLUMN ai_provider.provider_code IS '提供商代码';
COMMENT ON COLUMN ai_provider.base_url IS '基础URL';
COMMENT ON COLUMN ai_provider.api_key IS 'API密钥（加密存储）';
COMMENT ON COLUMN ai_provider.timeout IS '超时时间（秒）';
COMMENT ON COLUMN ai_provider.max_retries IS '最大重试次数';
COMMENT ON COLUMN ai_provider.enabled IS '是否启用';
COMMENT ON COLUMN ai_provider.priority IS '优先级';
COMMENT ON COLUMN ai_provider.created_at IS '创建时间';
COMMENT ON COLUMN ai_provider.updated_at IS '更新时间';

CREATE INDEX IF NOT EXISTS idx_ai_provider_enabled ON ai_provider(enabled);

-- 模型表
CREATE TABLE IF NOT EXISTS ai_model (
    id BIGSERIAL PRIMARY KEY,
    model_code VARCHAR(50) UNIQUE NOT NULL,
    model_name VARCHAR(100) NOT NULL,
    model_type VARCHAR(50) NOT NULL DEFAULT 'llm',
    provider_id BIGINT NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    max_tokens INTEGER,
    context_length INTEGER,
    temperature DECIMAL(3,2) DEFAULT 0.7,
    top_p DECIMAL(3,2) DEFAULT 1.0,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ai_model_provider FOREIGN KEY (provider_id) REFERENCES ai_provider(id) ON DELETE CASCADE
);

COMMENT ON TABLE ai_model IS 'AI模型表';
COMMENT ON COLUMN ai_model.id IS '主键ID';
COMMENT ON COLUMN ai_model.model_code IS '模型代码';
COMMENT ON COLUMN ai_model.model_name IS '模型名称';
COMMENT ON COLUMN ai_model.model_type IS '模型类型：llm-大语言模型, embedding-嵌入模型, rerank-重排序模型, image_generation-图像生成模型, asr-语音识别模型, tts-语音合成模型, other-其他类型';
COMMENT ON COLUMN ai_model.provider_id IS '供应商ID';
COMMENT ON COLUMN ai_model.enabled IS '是否启用';
COMMENT ON COLUMN ai_model.max_tokens IS '最大token数';
COMMENT ON COLUMN ai_model.context_length IS '上下文长度';
COMMENT ON COLUMN ai_model.temperature IS '温度参数（0-2）';
COMMENT ON COLUMN ai_model.top_p IS '核采样参数（0-1）';
COMMENT ON COLUMN ai_model.description IS '描述';
COMMENT ON COLUMN ai_model.created_at IS '创建时间';
COMMENT ON COLUMN ai_model.updated_at IS '更新时间';

CREATE INDEX IF NOT EXISTS idx_ai_model_model_code ON ai_model(model_code);
CREATE INDEX IF NOT EXISTS idx_ai_model_model_type ON ai_model(model_type);
CREATE INDEX IF NOT EXISTS idx_ai_model_provider_id ON ai_model(provider_id);
CREATE INDEX IF NOT EXISTS idx_ai_model_enabled ON ai_model(enabled);

-- 会话表
CREATE TABLE IF NOT EXISTS ai_chat_session (
    id BIGSERIAL PRIMARY KEY,
    session_id VARCHAR(64) UNIQUE NOT NULL,
    user_id VARCHAR(64),
    title VARCHAR(200),
    model_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE ai_chat_session IS 'AI聊天会话表';
COMMENT ON COLUMN ai_chat_session.id IS '主键ID';
COMMENT ON COLUMN ai_chat_session.session_id IS '会话ID';
COMMENT ON COLUMN ai_chat_session.user_id IS '用户ID';
COMMENT ON COLUMN ai_chat_session.title IS '会话标题';
COMMENT ON COLUMN ai_chat_session.model_id IS '使用的模型ID';
COMMENT ON COLUMN ai_chat_session.created_at IS '创建时间';
COMMENT ON COLUMN ai_chat_session.updated_at IS '更新时间';

CREATE INDEX IF NOT EXISTS idx_ai_chat_session_session_id ON ai_chat_session(session_id);
CREATE INDEX IF NOT EXISTS idx_ai_chat_session_user_id ON ai_chat_session(user_id);
CREATE INDEX IF NOT EXISTS idx_ai_chat_session_model_id ON ai_chat_session(model_id);

-- 消息表
CREATE TABLE IF NOT EXISTS ai_chat_message (
    id BIGSERIAL PRIMARY KEY,
    session_id VARCHAR(64) NOT NULL,
    role VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    tokens_used INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE ai_chat_message IS 'AI聊天消息表';
COMMENT ON COLUMN ai_chat_message.id IS '主键ID';
COMMENT ON COLUMN ai_chat_message.session_id IS '会话ID';
COMMENT ON COLUMN ai_chat_message.role IS '角色：user/assistant/system';
COMMENT ON COLUMN ai_chat_message.content IS '消息内容';
COMMENT ON COLUMN ai_chat_message.tokens_used IS '使用的token数';
COMMENT ON COLUMN ai_chat_message.created_at IS '创建时间';

CREATE INDEX IF NOT EXISTS idx_ai_chat_message_session_id ON ai_chat_message(session_id);
CREATE INDEX IF NOT EXISTS idx_ai_chat_message_created_at ON ai_chat_message(created_at);

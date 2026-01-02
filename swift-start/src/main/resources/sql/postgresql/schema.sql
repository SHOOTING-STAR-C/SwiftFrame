CREATE TABLE IF NOT EXISTS sys_config (
    config_id BIGSERIAL PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT NOT NULL,
    config_type VARCHAR(50) NOT NULL DEFAULT 'SYSTEM',
    description VARCHAR(255),
    is_enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50)
);

CREATE INDEX IF NOT EXISTS idx_sys_config_key ON sys_config(config_key);
CREATE INDEX IF NOT EXISTS idx_sys_config_type ON sys_config(config_type);
CREATE INDEX IF NOT EXISTS idx_sys_config_enabled ON sys_config(is_enabled);

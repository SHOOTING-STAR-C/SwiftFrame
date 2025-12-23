-- 业务区域表 (PostgreSQL)
CREATE TABLE IF NOT EXISTS buss_area (
    area_id SERIAL PRIMARY KEY,
    parent_id INT,
    area_name VARCHAR(100) NOT NULL
);

-- 系统配置表 (PostgreSQL)
CREATE TABLE IF NOT EXISTS sys_config (
    config_id SERIAL PRIMARY KEY,
    config_name VARCHAR(100) NOT NULL,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value VARCHAR(500),
    config_type CHAR(1) DEFAULT 'N',
    create_by VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remark VARCHAR(500)
);

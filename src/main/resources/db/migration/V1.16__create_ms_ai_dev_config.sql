-- V1.16__create_ms_ai_dev_config.sql
-- 创建 AI Dev 全局配置表

CREATE TABLE IF NOT EXISTS ms_ai_dev_config (
    id VARCHAR(64) PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT NOT NULL,
    create_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE ms_ai_dev_config IS 'AI 开发全局配置表';
COMMENT ON COLUMN ms_ai_dev_config.id IS '主键ID';
COMMENT ON COLUMN ms_ai_dev_config.config_key IS '配置键名，如 engine_url';
COMMENT ON COLUMN ms_ai_dev_config.config_value IS '配置内容';
COMMENT ON COLUMN ms_ai_dev_config.create_time IS '创建时间';
COMMENT ON COLUMN ms_ai_dev_config.update_time IS '更新时间';

-- 插入默认的 ms-ai-dev 引擎地址
INSERT INTO ms_ai_dev_config (id, config_key, config_value) 
VALUES ('1', 'engine_url', 'http://localhost:8182')
ON CONFLICT (config_key) DO NOTHING;

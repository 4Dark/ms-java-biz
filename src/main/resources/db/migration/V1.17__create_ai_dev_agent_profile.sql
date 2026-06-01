-- V1.17__create_ai_dev_agent_profile.sql
-- 创建 AI Dev 智能体角色配置表

CREATE TABLE IF NOT EXISTS ms_ai_dev_agent_profile (
    id VARCHAR(64) PRIMARY KEY,
    role_name VARCHAR(64) NOT NULL UNIQUE,
    base_url VARCHAR(255),
    api_token VARCHAR(255),
    model_name VARCHAR(128),
    avatar VARCHAR(255),
    system_prompt TEXT,
    create_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE ms_ai_dev_agent_profile IS 'AI 开发智能体角色配置表';
COMMENT ON COLUMN ms_ai_dev_agent_profile.id IS '主键ID';
COMMENT ON COLUMN ms_ai_dev_agent_profile.role_name IS '角色名称 (如 PLANNER, GENERATOR, EVALUATOR)';
COMMENT ON COLUMN ms_ai_dev_agent_profile.base_url IS 'LLM API 基础地址';
COMMENT ON COLUMN ms_ai_dev_agent_profile.api_token IS 'LLM API Token';
COMMENT ON COLUMN ms_ai_dev_agent_profile.model_name IS '模型名称';
COMMENT ON COLUMN ms_ai_dev_agent_profile.avatar IS '自定义头像图标';
COMMENT ON COLUMN ms_ai_dev_agent_profile.system_prompt IS '系统提示词';
COMMENT ON COLUMN ms_ai_dev_agent_profile.create_time IS '创建时间';
COMMENT ON COLUMN ms_ai_dev_agent_profile.update_time IS '更新时间';

-- 初始化默认角色数据
INSERT INTO ms_ai_dev_agent_profile (id, role_name, base_url, api_token, model_name, avatar, system_prompt) 
VALUES 
('node-orch', 'ORCHESTRATOR', NULL, NULL, 'claude-3-5-sonnet-20240620', 'group_work', 'You are the Master Orchestrator, responsible for coordinating tasks and sub-agents.'),
('node-plan', 'PLANNER', NULL, NULL, 'claude-3-5-sonnet-20240620', 'psychology', 'You are the Planner. You understand requirements, generate plan.md, and create system designs.'),
('node-gen', 'GENERATOR', NULL, NULL, 'deepseek-coder', 'code', 'You are the Generator. You write code and implement features following TDD.'),
('node-eval', 'EVALUATOR', NULL, NULL, 'gemini-2.5-pro', 'fact_check', 'You are the Evaluator. You review code and run tests to check for regressions.')
ON CONFLICT (role_name) DO NOTHING;

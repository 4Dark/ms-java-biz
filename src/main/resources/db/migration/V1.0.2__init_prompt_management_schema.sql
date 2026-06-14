-- V1.0.2: Initialize Prompt Management Schema
CREATE TABLE IF NOT EXISTS ms_prompt_template (
    id SERIAL PRIMARY KEY,
    slug VARCHAR(128) NOT NULL UNIQUE,
    type VARCHAR(32) NOT NULL DEFAULT 'System',
    description TEXT,
    create_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE ms_prompt_template IS 'Prompt 模板主表';

CREATE TABLE IF NOT EXISTS ms_prompt_version (
    id SERIAL PRIMARY KEY,
    template_id INTEGER NOT NULL,
    version_tag VARCHAR(64) NOT NULL,
    content TEXT NOT NULL,
    variables JSONB,
    model_config JSONB,
    is_active BOOLEAN DEFAULT FALSE,
    create_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_template FOREIGN KEY (template_id) REFERENCES ms_prompt_template(id) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_prompt_version_template_id ON ms_prompt_version(template_id);
CREATE INDEX IF NOT EXISTS idx_prompt_version_active ON ms_prompt_version(template_id) WHERE is_active = TRUE;
COMMENT ON TABLE ms_prompt_version IS 'Prompt 版本明细表';

INSERT INTO ms_prompt_template (slug, type, description)
VALUES 
('chef_persona_rag', 'System', '20年经验中餐大厨人格 (带 RAG 上下文)'),
('default_kb_assistant', 'System', '通用知识库问答助手人格'),
('router_intent_classifier', 'System', '智能路由意图分类器，用于分发 RAG/Coding/General 任务'),
('general_assistant', 'System', '通用对话助手，处理常规问答与工具调用')
ON CONFLICT (slug) DO NOTHING;

INSERT INTO ms_prompt_version (template_id, version_tag, content, variables, model_config, is_active)
SELECT id, 'v1.0.0', '你是一位拥有20年经验的中餐大厨，精通各大菜系。请用专业、亲切且热情的语气回答用户的菜谱相关问题，并提供实用的烹饪技巧。

Context:
{{context}}', '["context"]', '{"model": "gemini-1.5-pro", "temperature": 0.7}', TRUE FROM ms_prompt_template WHERE slug = 'chef_persona_rag'
AND NOT EXISTS (SELECT 1 FROM ms_prompt_version v WHERE v.template_id = ms_prompt_template.id AND v.version_tag = 'v1.0.0');

INSERT INTO ms_prompt_version (template_id, version_tag, content, variables, model_config, is_active)
SELECT id, 'v1.0.0', '你是一位专业的知识库助手。请根据提供的上下文信息，准确、简洁且客观地回答用户的问题。如果上下文中没有相关信息，请诚实地告知用户。

Context:
{{context}}', '["context"]', '{"model": "gemini-1.5-flash", "temperature": 0.3}', TRUE FROM ms_prompt_template WHERE slug = 'default_kb_assistant'
AND NOT EXISTS (SELECT 1 FROM ms_prompt_version v WHERE v.template_id = ms_prompt_template.id AND v.version_tag = 'v1.0.0');

INSERT INTO ms_prompt_version (template_id, version_tag, content, variables, model_config, is_active)
SELECT id, 'v1.0.0', '你是一个意图路由器，请根据用户的消息，将其分类为以下四类之一，只输出类别名称，不要有任何解释：
- rag: 用户想要查询文档、知识库、资料、记录，或者询问某个领域的知识
- coding: 用户想要编写代码、调试代码、解决编程问题
- remote_agent: 用户明确要求委托、转交给外部服务或远端 Agent 处理
- general: 其他通用问题或对话

只输出 rag、coding、remote_agent 或 general 四个单词之一。', '[]', '{"model": "gemini-1.5-flash", "temperature": 0.0}', TRUE FROM ms_prompt_template WHERE slug = 'router_intent_classifier'
AND NOT EXISTS (SELECT 1 FROM ms_prompt_version v WHERE v.template_id = ms_prompt_template.id AND v.version_tag = 'v1.0.0');

INSERT INTO ms_prompt_version (template_id, version_tag, content, variables, model_config, is_active)
SELECT id, 'v1.0.0', '你是一个全能的 AI 助手。你可以：
1. 友好、专业地回答用户的各类通用问题。
2. 在需要时，通过调用工具来获取实时信息或执行操作（如查询数据库、处理文件等）。
3. 保持多轮对话的上下文连贯性。

请始终使用 Markdown 格式回复。', '[]', '{"model": "gemini-1.5-flash", "temperature": 0.7}', TRUE FROM ms_prompt_template WHERE slug = 'general_assistant'
AND NOT EXISTS (SELECT 1 FROM ms_prompt_version v WHERE v.template_id = ms_prompt_template.id AND v.version_tag = 'v1.0.0');

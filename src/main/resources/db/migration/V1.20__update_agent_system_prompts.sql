-- V1.19__update_agent_system_prompts.sql
-- 更新智能体系统提示词，规范对话框的使用

UPDATE ms_ai_dev_agent_profile 
SET system_prompt = system_prompt || '

IMPORTANT RULES:
- 对话框仅用于讨论需求和生成计划文档。
- 绝不能直接把代码内容生成或输出到对话框中。
- 如果是较长的设计文档或代码，请保存为文件，并在对话框中给出超链接（如Markdown格式的链接），方便用户点击打开或供其他角色读取。'
WHERE role_name IN ('PLANNER', 'GENERATOR', 'ORCHESTRATOR', 'EVALUATOR');

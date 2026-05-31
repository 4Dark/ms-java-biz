-- V1.15__add_ai_dev_team_schema.sql
-- 为 AI Dev Team 添加任务创建接口所需的数据结构

-- 1. 为 ai_dev_task 新增 human_feedback 字段（供 ms-ai-devops 常驻服务读取人工反馈后继续执行）
ALTER TABLE ai_dev_task ADD COLUMN IF NOT EXISTS human_feedback TEXT;
COMMENT ON COLUMN ai_dev_task.human_feedback IS '人类在 HITL 节点提供的反馈内容，ms-ai-devops 轮询此字段来判断是否可恢复';

-- 2. 为 ai_dev_chat_message 表增加索引以提升轮询效率
CREATE INDEX IF NOT EXISTS idx_ai_dev_task_status ON ai_dev_task(status);
CREATE INDEX IF NOT EXISTS idx_ai_dev_chat_message_task_id ON ai_dev_chat_message(task_id);

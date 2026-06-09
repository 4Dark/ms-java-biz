-- 为 ai_dev_task 新增 related_workspaces 字段，用于前端显式指定相关的项目（前缀匹配选出的）
ALTER TABLE ai_dev_task ADD COLUMN IF NOT EXISTS related_workspaces JSONB;
COMMENT ON COLUMN ai_dev_task.related_workspaces IS '前端创建任务时指定的关联工程列表（JSON 数组）';

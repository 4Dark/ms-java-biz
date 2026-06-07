-- V1.18: 新增头脑风暴参数配置字段
-- max_brainstorming_rounds: 限制多智能体讨论的最大轮数
-- context_sliding_window: 控制每轮发言时携带的历史消息条数

ALTER TABLE ai_dev_task
    ADD COLUMN IF NOT EXISTS max_brainstorming_rounds INTEGER NOT NULL DEFAULT 5,
    ADD COLUMN IF NOT EXISTS context_sliding_window   INTEGER NOT NULL DEFAULT 3;

COMMENT ON COLUMN ai_dev_task.max_brainstorming_rounds IS '头脑风暴最大轮数，超过此轮数 PM 将强制收尾';
COMMENT ON COLUMN ai_dev_task.context_sliding_window   IS '多智能体发言时携带的滑动窗口历史消息条数';

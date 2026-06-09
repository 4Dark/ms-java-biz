-- ============================================
-- ms-java-biz Database Migration
-- Version: 1.21
-- Description: Add rating field to ms_chat_message
-- ============================================

ALTER TABLE ms_chat_message ADD COLUMN IF NOT EXISTS rating VARCHAR(10) DEFAULT NULL;
COMMENT ON COLUMN ms_chat_message.rating IS '用户评价: good (点赞) / bad (点踩) / NULL (未评价)';

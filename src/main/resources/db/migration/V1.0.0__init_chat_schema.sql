-- V1.0.0: Initialize Core Chat Schema
CREATE TABLE IF NOT EXISTS ms_chat_session (
    session_id VARCHAR(255) PRIMARY KEY,
    title VARCHAR(255),
    last_active_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_chat_sessions_last_active ON ms_chat_session(last_active_time DESC);

CREATE TABLE IF NOT EXISTS ms_chat_message (
    id BIGSERIAL PRIMARY KEY,
    session_id VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    content TEXT,
    rating VARCHAR(10) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_chat_messages_session_id ON ms_chat_message(session_id);

COMMENT ON TABLE ms_chat_message IS '聊天消息历史表';
COMMENT ON TABLE ms_chat_session IS '聊天会话列表表';
COMMENT ON COLUMN ms_chat_message.rating IS '用户评价: good (点赞) / bad (点踩) / NULL (未评价)';

CREATE OR REPLACE FUNCTION sync_chat_session()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'INSERT') THEN
        IF EXISTS (SELECT 1 FROM ms_chat_session WHERE session_id = NEW.session_id) THEN
            UPDATE ms_chat_session 
            SET last_active_time = NEW.created_at 
            WHERE session_id = NEW.session_id;
        ELSE
            INSERT INTO ms_chat_session (session_id, title, last_active_time, created_at)
            VALUES (
                NEW.session_id, 
                CASE WHEN NEW.role = 'user' THEN LEFT(NEW.content, 100) ELSE 'New Conversation' END,
                NEW.created_at,
                NEW.created_at
            );
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_sync_chat_session ON ms_chat_message;
CREATE TRIGGER trg_sync_chat_session
AFTER INSERT ON ms_chat_message
FOR EACH ROW
EXECUTE FUNCTION sync_chat_session();

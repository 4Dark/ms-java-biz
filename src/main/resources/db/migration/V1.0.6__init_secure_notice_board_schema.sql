-- V1.0.6__init_secure_notice_board_schema.sql

CREATE TABLE IF NOT EXISTS ms_notice_board_item (
    id BIGSERIAL PRIMARY KEY,
    target_client VARCHAR(255) NOT NULL,
    usage_details TEXT,
    reference_url TEXT,
    content_url TEXT NOT NULL,
    expire_time TIMESTAMP WITH TIME ZONE NOT NULL,
    last_viewed_time TIMESTAMP WITH TIME ZONE,
    deleted BOOLEAN DEFAULT FALSE,
    create_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE ms_notice_board_item IS '安全公告栏（敏感链接）表';

CREATE TABLE IF NOT EXISTS ms_announcement (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    create_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE ms_announcement IS '系统公告表';

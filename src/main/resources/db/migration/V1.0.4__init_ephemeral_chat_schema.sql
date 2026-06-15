-- V1.0.4: Initialize Ephemeral Chat Schema
CREATE TABLE IF NOT EXISTS ms_ephemeral_room (
    id              VARCHAR(36) PRIMARY KEY,
    short_code      VARCHAR(12) UNIQUE NOT NULL,
    title           VARCHAR(100),
    max_ttl_seconds BIGINT      NOT NULL,
    expire_at       TIMESTAMPTZ NOT NULL,
    last_active_at  TIMESTAMPTZ NOT NULL,
    created_by      VARCHAR(36),
    is_destroyed    BOOLEAN     NOT NULL DEFAULT FALSE,
    create_time     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    update_time     TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
COMMENT ON TABLE  ms_ephemeral_room IS '阅后即焚通讯空间';

CREATE TABLE IF NOT EXISTS ms_ephemeral_message (
    id          BIGSERIAL   PRIMARY KEY,
    room_id     VARCHAR(36) NOT NULL REFERENCES ms_ephemeral_room(id) ON DELETE CASCADE,
    sender_id   VARCHAR(36) NOT NULL,
    cipher_text TEXT        NOT NULL,
    iv          VARCHAR(64) NOT NULL,
    sent_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    is_deleted  BOOLEAN     NOT NULL DEFAULT FALSE
);
COMMENT ON TABLE  ms_ephemeral_message IS '加密消息，服务器不存明文';
CREATE INDEX IF NOT EXISTS idx_eph_msg_room_time ON ms_ephemeral_message(room_id, sent_at);
CREATE INDEX IF NOT EXISTS idx_eph_msg_sender   ON ms_ephemeral_message(sender_id, room_id);

CREATE TABLE IF NOT EXISTS ms_ephemeral_participant (
    id             BIGSERIAL   PRIMARY KEY,
    room_id        VARCHAR(36) NOT NULL REFERENCES ms_ephemeral_room(id) ON DELETE CASCADE,
    participant_id VARCHAR(36) NOT NULL,
    nickname_cipher TEXT,
    joined_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    last_seen_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(room_id, participant_id)
);
COMMENT ON TABLE  ms_ephemeral_participant IS '房间参与者（匿名，无需登录）';

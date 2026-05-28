-- ============================================================
-- V1.14: 阅后即焚临时通讯空间（Ephemeral Chat Room）
-- 服务器只存密文，TTL 与短链生命周期绑定
-- ============================================================

-- 通讯空间（房间）
CREATE TABLE ms_ephemeral_room (
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
COMMENT ON COLUMN ms_ephemeral_room.short_code IS 'Base62 短链码（7~8位）';
COMMENT ON COLUMN ms_ephemeral_room.max_ttl_seconds IS '创建者设定的最大存活秒数';
COMMENT ON COLUMN ms_ephemeral_room.expire_at IS '房间过期时刻，与短链 TTL 完全绑定';
COMMENT ON COLUMN ms_ephemeral_room.last_active_at IS '最后活跃时间，用于活跃延长逻辑';
COMMENT ON COLUMN ms_ephemeral_room.created_by IS '创建者匿名 ID（客户端生成 UUID）';
COMMENT ON COLUMN ms_ephemeral_room.is_destroyed IS '是否已被手动销毁';

-- 加密消息（服务器只存密文，永不接触明文）
CREATE TABLE ms_ephemeral_message (
    id          BIGSERIAL   PRIMARY KEY,
    room_id     VARCHAR(36) NOT NULL REFERENCES ms_ephemeral_room(id) ON DELETE CASCADE,
    sender_id   VARCHAR(36) NOT NULL,
    cipher_text TEXT        NOT NULL,
    iv          VARCHAR(64) NOT NULL,
    sent_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    is_deleted  BOOLEAN     NOT NULL DEFAULT FALSE
);
COMMENT ON TABLE  ms_ephemeral_message IS '加密消息，服务器不存明文';
COMMENT ON COLUMN ms_ephemeral_message.sender_id IS '发送者匿名 ID（客户端生成，服务器不验证身份）';
COMMENT ON COLUMN ms_ephemeral_message.cipher_text IS 'AES-GCM-256 加密后的 Base64 密文';
COMMENT ON COLUMN ms_ephemeral_message.iv IS '每条消息独立的随机 IV（12字节），防止密文重放攻击';
COMMENT ON COLUMN ms_ephemeral_message.is_deleted IS '发送者主动删除时软删除，TTL到期后全量物理删除';
CREATE INDEX idx_eph_msg_room_time ON ms_ephemeral_message(room_id, sent_at);
CREATE INDEX idx_eph_msg_sender   ON ms_ephemeral_message(sender_id, room_id);

-- 参与者记录（匿名，无需登录态）
CREATE TABLE ms_ephemeral_participant (
    id             BIGSERIAL   PRIMARY KEY,
    room_id        VARCHAR(36) NOT NULL REFERENCES ms_ephemeral_room(id) ON DELETE CASCADE,
    participant_id VARCHAR(36) NOT NULL,
    nickname_cipher TEXT,
    joined_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    last_seen_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(room_id, participant_id)
);
COMMENT ON TABLE  ms_ephemeral_participant IS '房间参与者（匿名，无需登录）';
COMMENT ON COLUMN ms_ephemeral_participant.participant_id IS '客户端生成的匿名 UUID，服务器不关联真实用户';
COMMENT ON COLUMN ms_ephemeral_participant.nickname_cipher IS '加密后的昵称（可选），服务器不解密';

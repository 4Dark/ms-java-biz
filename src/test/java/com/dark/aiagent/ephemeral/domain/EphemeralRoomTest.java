package com.dark.aiagent.ephemeral.domain;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.*;

/**
 * EphemeralRoom 领域实体单元测试（100% 覆盖核心业务方法）
 */
class EphemeralRoomTest {

    private static final long TTL_60S = 60L;
    private static final long EXTEND_30S = 30L;

    private EphemeralRoom newRoom() {
        return EphemeralRoom.create("room-id-1", "abcd1234", "测试房间", TTL_60S, "creator-1");
    }

    @Test
    void create_should_set_expire_within_ttl() {
        EphemeralRoom room = newRoom();

        assertThat(room.getId()).isEqualTo("room-id-1");
        assertThat(room.getShortCode()).isEqualTo("abcd1234");
        assertThat(room.isDestroyed()).isFalse();
        assertThat(room.getExpireAt())
                .isAfter(OffsetDateTime.now())
                .isBefore(OffsetDateTime.now().plusSeconds(TTL_60S + 1));
    }

    @Test
    void isExpired_should_return_false_for_fresh_room() {
        assertThat(newRoom().isExpired()).isFalse();
    }

    @Test
    void isExpired_should_return_true_after_destroy() {
        EphemeralRoom room = newRoom();
        room.destroy();
        assertThat(room.isExpired()).isTrue();
        assertThat(room.isDestroyed()).isTrue();
    }

    @Test
    void extendOnActivity_should_update_lastActiveAt() {
        EphemeralRoom room = newRoom();
        OffsetDateTime beforeTest = OffsetDateTime.now().minusSeconds(1);

        room.extendOnActivity(EXTEND_30S);

        // lastActiveAt 应已更新为当前时间附近
        assertThat(room.getLastActiveAt()).isAfter(beforeTest);
        // expireAt 不为 null 且在未来（now+30s 时的 expire 至少在 now 之后）
        assertThat(room.getExpireAt()).isAfter(OffsetDateTime.now().minusSeconds(1));
    }

    @Test
    void extendOnActivity_should_not_exceed_ceiling() {
        EphemeralRoom room = newRoom();
        // 延长时间远超 maxTtl 上限
        room.extendOnActivity(TTL_60S * 10);

        // expireAt 不得超过 createTime + maxTtlSeconds
        OffsetDateTime ceiling = room.getCreateTime().plusSeconds(TTL_60S);
        assertThat(room.getExpireAt()).isBeforeOrEqualTo(ceiling.plusSeconds(1));
    }

    @Test
    void extendOnActivity_on_destroyed_room_should_be_no_op() {
        EphemeralRoom room = newRoom();
        room.destroy();
        OffsetDateTime expireBefore = room.getExpireAt();

        room.extendOnActivity(EXTEND_30S);

        // 已销毁，expireAt 不变
        assertThat(room.getExpireAt()).isEqualTo(expireBefore);
    }

    @Test
    void create_should_throw_on_invalid_arguments() {
        assertThatThrownBy(() -> EphemeralRoom.create(null, "code", null, 60, "user"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> EphemeralRoom.create("id", "", null, 60, "user"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> EphemeralRoom.create("id", "code", null, 0, "user"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

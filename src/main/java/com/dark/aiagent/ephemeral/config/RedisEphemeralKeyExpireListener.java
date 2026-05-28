package com.dark.aiagent.ephemeral.config;

import com.dark.aiagent.ephemeral.infrastructure.EphemeralRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * Redis Keyspace 事件监听器（主动触发 TTL 清理）
 *
 * <p>监听 {@code __keyevent@*__:expired} 事件，当 ephemeral:room:{id} Key 过期时
 * 立即触发物理删除，比 Scheduler 更精准。
 *
 * <p>前提：Redis 服务器需开启 {@code notify-keyspace-events=Ex}。
 * 若运维不允许，此组件不影响主流程，Scheduler 会作为兜底。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisEphemeralKeyExpireListener implements MessageListener {

    private static final String ROOM_KEY_PREFIX = "ephemeral:room:";

    private final EphemeralRoomRepository repository;

    /**
     * 接收 Redis Key 过期事件，提取 roomId 并触发物理删除。
     *
     * @param message Redis 消息（body 为过期的 Key 名称）
     * @param pattern 匹配的模式
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        if (!expiredKey.startsWith(ROOM_KEY_PREFIX)) return;

        String roomId = expiredKey.substring(ROOM_KEY_PREFIX.length());
        log.info("【Redis TTL】检测到房间 Key 过期，触发物理删除 roomId={}", roomId);
        repository.deleteRoomById(roomId);
    }
}

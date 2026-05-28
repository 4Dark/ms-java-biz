package com.dark.aiagent.ephemeral.application;

import com.dark.aiagent.ephemeral.domain.EphemeralMessage;
import com.dark.aiagent.ephemeral.domain.EphemeralParticipant;
import com.dark.aiagent.ephemeral.domain.EphemeralRoom;
import com.dark.aiagent.ephemeral.infrastructure.EphemeralRoomRepository;
import com.dark.aiagent.ephemeral.interfaces.websocket.EphemeralMessageHandler;
import com.dark.aiagent.ephemeral.interfaces.websocket.dto.WsMessage;
import com.dark.aiagent.ephemeral.interfaces.websocket.dto.WsMessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 阅后即焚通讯空间核心业务编排层。
 *
 * <p>负责：创建房间、加入房间、发送消息（存储+广播）、退出删除、销毁房间、轮询拉取。
 * 密钥永不在此层处理，加解密完全在客户端进行。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EphemeralRoomUseCase {

    private final EphemeralRoomRepository repository;
    private final StringRedisTemplate redisTemplate;
    private final EphemeralMessageHandler messageHandler;

    /** Base62 字符集 */
    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    @Value("${ephemeral.default-ttl-seconds:3600}")
    private long defaultTtlSeconds;

    @Value("${ephemeral.max-ttl-seconds:604800}")
    private long maxTtlSeconds;

    @Value("${ephemeral.activity-extend-seconds:1800}")
    private long activityExtendSeconds;

    // ──────────────────── 创建房间 ────────────────────

    /**
     * 创建新的阅后即焚通讯空间。
     *
     * @param title      可选公开名称（不含敏感内容）
     * @param ttlSeconds 存活秒数（不超过 max-ttl-seconds）
     * @param createdBy  创建者匿名 ID
     * @return 新建的房间实体
     */
    public EphemeralRoom createRoom(String title, long ttlSeconds, String createdBy) {
        long actualTtl = Math.min(ttlSeconds > 0 ? ttlSeconds : defaultTtlSeconds, maxTtlSeconds);
        String roomId = UUID.randomUUID().toString();
        String shortCode = generateShortCode(roomId);

        EphemeralRoom room = EphemeralRoom.create(roomId, shortCode, title, actualTtl, createdBy);
        repository.saveRoom(room);

        // Redis 同步写入（用于 TTL 事件驱动清理）
        redisTemplate.opsForValue().set(
                "ephemeral:room:" + roomId, shortCode,
                Duration.ofSeconds(actualTtl)
        );

        log.info("【EphemeralRoom】创建房间 roomId={} shortCode={} ttl={}s", roomId, shortCode, actualTtl);
        return room;
    }

    // ──────────────────── 查询房间 ────────────────────

    /**
     * 按短码查询有效房间元信息。
     *
     * @param shortCode Base62 短码
     * @return 有效房间 Optional，过期/销毁返回 empty
     */
    public Optional<EphemeralRoom> findRoom(String shortCode) {
        return repository.findActiveByShortCode(shortCode);
    }

    // ──────────────────── 加入房间 ────────────────────

    /**
     * 参与者加入房间，注册匿名身份并延长 TTL。
     *
     * @param roomId        房间 ID
     * @param participantId 客户端生成的匿名 UUID
     * @param nicknameCipher 加密昵称（可为 null）
     */
    public void joinRoom(String roomId, String participantId, String nicknameCipher) {
        EphemeralRoom room = requireActiveRoom(roomId);

        EphemeralParticipant participant = EphemeralParticipant.join(roomId, participantId, nicknameCipher);
        repository.saveParticipantIfAbsent(participant);

        // 活跃延长 TTL
        room.extendOnActivity(activityExtendSeconds);
        repository.updateRoom(room);

        // 广播 JOIN 事件
        messageHandler.broadcast(roomId, WsMessage.event(WsMessageType.JOIN, participantId));
        log.info("【EphemeralRoom】参与者加入 roomId={} participantId={}", roomId, participantId);
    }

    // ──────────────────── 发送消息 ────────────────────

    /**
     * 接收客户端加密消息，持久化后广播给房间所有成员。
     *
     * @param roomId     房间 ID
     * @param senderId   发送者匿名 ID
     * @param cipherText AES-GCM 密文（Base64）
     * @param iv         随机初始向量（Base64）
     * @return 保存的消息 ID（供轮询降级使用）
     */
    public EphemeralMessage sendMessage(String roomId, String senderId,
                                        String cipherText, String iv) {
        requireActiveRoom(roomId);

        EphemeralMessage message = EphemeralMessage.newMessage(roomId, senderId, cipherText, iv);
        repository.saveMessage(message);

        // 广播密文给房间内所有 WebSocket 连接（其他客户端本地解密）
        messageHandler.broadcast(roomId,
                WsMessage.cipher(senderId, cipherText, iv));

        return message;
    }

    // ──────────────────── 轮询降级 ────────────────────

    /**
     * 轮询拉取新消息（WebSocket 降级方案）。
     *
     * @param roomId  房间 ID
     * @param afterId 上次拉取的最后消息 ID（0 表示从头）
     * @return 消息列表（最多 50 条）
     */
    public List<EphemeralMessage> pollMessages(String roomId, long afterId) {
        requireActiveRoom(roomId);
        return repository.findMessages(roomId, afterId);
    }

    // ──────────────────── 退出并删除本人记录 ────────────────────

    /**
     * 参与者主动退出，软删除本人在房间内的所有消息。
     *
     * @param roomId        房间 ID
     * @param participantId 发送者匿名 ID
     */
    public void leaveAndDeleteMessages(String roomId, String participantId) {
        int deleted = repository.softDeleteMessagesBySender(roomId, participantId);
        // 广播 LEAVE 事件（含删除信号，前端收到后移除该发送者的消息显示）
        messageHandler.broadcast(roomId, WsMessage.event(WsMessageType.LEAVE, participantId));
        log.info("【EphemeralRoom】参与者退出并删除消息 roomId={} participantId={} deletedCount={}",
                roomId, participantId, deleted);
    }

    // ──────────────────── 销毁整个房间 ────────────────────

    /**
     * 手动销毁整个房间（广播 DESTROY 后物理删除所有数据）。
     *
     * @param roomId 房间 ID
     */
    public void destroyRoom(String roomId) {
        EphemeralRoom room = requireActiveRoom(roomId);
        room.destroy();
        repository.updateRoom(room);

        // 广播销毁信号，所有客户端收到后清空本地内存
        messageHandler.broadcast(roomId, WsMessage.event(WsMessageType.DESTROY, roomId));

        // 清理 Redis Key
        redisTemplate.delete("ephemeral:room:" + roomId);

        // 物理删除（ON DELETE CASCADE 处理消息和参与者）
        repository.deleteRoomById(roomId);
        log.info("【EphemeralRoom】房间已销毁 roomId={}", roomId);
    }

    // ──────────────────── 心跳处理 ────────────────────

    /**
     * WebSocket 心跳：更新活跃时间并延长 TTL。
     *
     * @param roomId 房间 ID
     */
    public void heartbeat(String roomId) {
        Optional<EphemeralRoom> roomOpt = repository.findActiveByShortCode(roomId);
        roomOpt.ifPresent(room -> {
            room.extendOnActivity(activityExtendSeconds);
            repository.updateRoom(room);
        });
    }

    // ──────────────────── 参与者数量 ────────────────────

    /**
     * 查询房间当前参与者数量（用于房间元信息响应）。
     *
     * @param roomId 房间 ID
     * @return 参与者数量
     */
    public long countParticipants(String roomId) {
        return repository.countParticipants(roomId);
    }

    // ──────────────────── 内部工具 ────────────────────

    /**
     * 校验房间有效性，无效则抛出异常。
     */
    private EphemeralRoom requireActiveRoom(String roomId) {
        EphemeralRoom room = repository.findActiveByRoomId(roomId)
                .orElseThrow(() -> new IllegalStateException("Room not found or expired: " + roomId));
        if (room.isExpired()) {
            throw new IllegalStateException("Room has expired: " + roomId);
        }
        return room;
    }

    /**
     * 基于 UUID 生成 8 位 Base62 短码（碰撞概率极低，可加重试逻辑）。
     */
    private String generateShortCode(String uuid) {
        long num = Math.abs(uuid.replace("-", "").substring(0, 15)
                .chars().reduce(0, (a, b) -> a * 31 + b));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(BASE62.charAt((int) (num % 62)));
            num /= 62;
        }
        return sb.toString();
    }
}

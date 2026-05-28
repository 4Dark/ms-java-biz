package com.dark.aiagent.ephemeral.infrastructure;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.dark.aiagent.ephemeral.domain.EphemeralMessage;
import com.dark.aiagent.ephemeral.domain.EphemeralParticipant;
import com.dark.aiagent.ephemeral.domain.EphemeralRoom;
import com.dark.aiagent.infrastructure.persistence.entity.EphemeralMessageDO;
import com.dark.aiagent.infrastructure.persistence.entity.EphemeralParticipantDO;
import com.dark.aiagent.infrastructure.persistence.entity.EphemeralRoomDO;
import com.dark.aiagent.infrastructure.persistence.mapper.EphemeralMessageMapper;
import com.dark.aiagent.infrastructure.persistence.mapper.EphemeralParticipantMapper;
import com.dark.aiagent.infrastructure.persistence.mapper.EphemeralRoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 阅后即焚仓库：负责领域对象与 ORM DO 之间的映射转换。
 */
@Repository
@RequiredArgsConstructor
public class EphemeralRoomRepository {

    private final EphemeralRoomMapper roomMapper;
    private final EphemeralMessageMapper messageMapper;
    private final EphemeralParticipantMapper participantMapper;

    // ──────────────────── Room ────────────────────

    /**
     * 保存新房间（INSERT）。
     */
    public void saveRoom(EphemeralRoom room) {
        roomMapper.insert(toRoomDO(room));
    }

    /**
     * 更新房间状态（TTL 延长 / 销毁）。
     */
    public void updateRoom(EphemeralRoom room) {
        LambdaUpdateWrapper<EphemeralRoomDO> wrapper = new LambdaUpdateWrapper<EphemeralRoomDO>()
                .eq(EphemeralRoomDO::getId, room.getId())
                .set(EphemeralRoomDO::getExpireAt, room.getExpireAt())
                .set(EphemeralRoomDO::getLastActiveAt, room.getLastActiveAt())
                .set(EphemeralRoomDO::getIsDestroyed, room.isDestroyed())
                .set(EphemeralRoomDO::getUpdateTime, OffsetDateTime.now());
        roomMapper.update(null, wrapper);
    }

    /**
     * 按短码查询有效房间。
     *
     * @param shortCode Base62 短码
     * @return 有效房间，若不存在或已过期/销毁则 empty
     */
    public Optional<EphemeralRoom> findActiveByShortCode(String shortCode) {
        EphemeralRoomDO do_ = roomMapper.selectOne(
                new LambdaQueryWrapper<EphemeralRoomDO>()
                        .eq(EphemeralRoomDO::getShortCode, shortCode)
                        .eq(EphemeralRoomDO::getIsDestroyed, false)
                        .gt(EphemeralRoomDO::getExpireAt, OffsetDateTime.now())
        );
        return Optional.ofNullable(do_).map(this::toDomainRoom);
    }

    /**
     * 按房间 ID 查询有效房间。
     *
     * @param roomId 房间 UUID
     * @return 有效房间，若不存在或已过期/销毁则 empty
     */
    public Optional<EphemeralRoom> findActiveByRoomId(String roomId) {
        EphemeralRoomDO do_ = roomMapper.selectOne(
                new LambdaQueryWrapper<EphemeralRoomDO>()
                        .eq(EphemeralRoomDO::getId, roomId)
                        .eq(EphemeralRoomDO::getIsDestroyed, false)
                        .gt(EphemeralRoomDO::getExpireAt, OffsetDateTime.now())
        );
        return Optional.ofNullable(do_).map(this::toDomainRoom);
    }

    /**
     * 查询所有已过期但未物理删除的房间（用于 Scheduler 清理）。
     */
    public List<EphemeralRoom> findExpiredRooms() {
        return roomMapper.selectList(
                new LambdaQueryWrapper<EphemeralRoomDO>()
                        .lt(EphemeralRoomDO::getExpireAt, OffsetDateTime.now())
        ).stream().map(this::toDomainRoom).toList();
    }

    /**
     * 物理删除房间（级联删除消息和参与者，由数据库 ON DELETE CASCADE 保证）。
     */
    public void deleteRoomById(String roomId) {
        roomMapper.deleteById(roomId);
    }

    // ──────────────────── Message ────────────────────

    /**
     * 保存加密消息。
     */
    public void saveMessage(EphemeralMessage message) {
        messageMapper.insert(toMessageDO(message));
    }

    /**
     * 轮询拉取：获取指定房间内 lastId 之后的未删除消息（最多 50 条）。
     *
     * @param roomId 房间 ID
     * @param afterId 上次拉取的最后消息 ID（0 表示从头）
     * @return 消息列表
     */
    public List<EphemeralMessage> findMessages(String roomId, long afterId) {
        return messageMapper.selectList(
                new LambdaQueryWrapper<EphemeralMessageDO>()
                        .eq(EphemeralMessageDO::getRoomId, roomId)
                        .eq(EphemeralMessageDO::getIsDeleted, false)
                        .gt(EphemeralMessageDO::getId, afterId)
                        .orderByAsc(EphemeralMessageDO::getId)
                        .last("LIMIT 50")
        ).stream().map(this::toDomainMessage).toList();
    }

    /**
     * 软删除发送者在房间内的所有消息。
     */
    public int softDeleteMessagesBySender(String roomId, String senderId) {
        return messageMapper.softDeleteBySender(roomId, senderId);
    }

    // ──────────────────── Participant ────────────────────

    /**
     * 注册参与者（INSERT OR IGNORE 语义，通过 UNIQUE 约束保证幂等）。
     */
    public void saveParticipantIfAbsent(EphemeralParticipant participant) {
        EphemeralParticipantDO exists = participantMapper.selectOne(
                new LambdaQueryWrapper<EphemeralParticipantDO>()
                        .eq(EphemeralParticipantDO::getRoomId, participant.roomId())
                        .eq(EphemeralParticipantDO::getParticipantId, participant.participantId())
        );
        if (exists == null) {
            participantMapper.insert(toParticipantDO(participant));
        } else {
            // 更新最后活跃时间
            LambdaUpdateWrapper<EphemeralParticipantDO> wrapper = new LambdaUpdateWrapper<EphemeralParticipantDO>()
                    .eq(EphemeralParticipantDO::getRoomId, participant.roomId())
                    .eq(EphemeralParticipantDO::getParticipantId, participant.participantId())
                    .set(EphemeralParticipantDO::getLastSeenAt, OffsetDateTime.now());
            participantMapper.update(null, wrapper);
        }
    }

    /**
     * 查询房间当前在线参与者数量。
     */
    public long countParticipants(String roomId) {
        return participantMapper.selectCount(
                new LambdaQueryWrapper<EphemeralParticipantDO>()
                        .eq(EphemeralParticipantDO::getRoomId, roomId)
        );
    }

    // ──────────────────── Mappers ────────────────────

    private EphemeralRoomDO toRoomDO(EphemeralRoom room) {
        EphemeralRoomDO d = new EphemeralRoomDO();
        d.setId(room.getId());
        d.setShortCode(room.getShortCode());
        d.setTitle(room.getTitle());
        d.setMaxTtlSeconds(room.getMaxTtlSeconds());
        d.setExpireAt(room.getExpireAt());
        d.setLastActiveAt(room.getLastActiveAt());
        d.setCreatedBy(room.getCreatedBy());
        d.setIsDestroyed(room.isDestroyed());
        d.setCreateTime(room.getCreateTime());
        d.setUpdateTime(OffsetDateTime.now());
        return d;
    }

    private EphemeralRoom toDomainRoom(EphemeralRoomDO d) {
        return new EphemeralRoom(d.getId(), d.getShortCode(), d.getTitle(),
                d.getMaxTtlSeconds(), d.getExpireAt(), d.getLastActiveAt(),
                d.getCreatedBy(), Boolean.TRUE.equals(d.getIsDestroyed()), d.getCreateTime());
    }

    private EphemeralMessageDO toMessageDO(EphemeralMessage m) {
        EphemeralMessageDO d = new EphemeralMessageDO();
        d.setRoomId(m.roomId());
        d.setSenderId(m.senderId());
        d.setCipherText(m.cipherText());
        d.setIv(m.iv());
        d.setSentAt(m.sentAt());
        d.setIsDeleted(m.deleted());
        return d;
    }

    private EphemeralMessage toDomainMessage(EphemeralMessageDO d) {
        return new EphemeralMessage(d.getId(), d.getRoomId(), d.getSenderId(),
                d.getCipherText(), d.getIv(), d.getSentAt(),
                Boolean.TRUE.equals(d.getIsDeleted()));
    }

    private EphemeralParticipantDO toParticipantDO(EphemeralParticipant p) {
        EphemeralParticipantDO d = new EphemeralParticipantDO();
        d.setRoomId(p.roomId());
        d.setParticipantId(p.participantId());
        d.setNicknameCipher(p.nicknameCipher());
        d.setJoinedAt(p.joinedAt());
        d.setLastSeenAt(p.lastSeenAt());
        return d;
    }
}

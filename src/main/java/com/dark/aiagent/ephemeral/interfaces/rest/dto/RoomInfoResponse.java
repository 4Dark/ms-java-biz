package com.dark.aiagent.ephemeral.interfaces.rest.dto;

import com.dark.aiagent.ephemeral.domain.EphemeralRoom;

import java.time.OffsetDateTime;

/**
 * 房间元信息响应（不含任何消息内容）
 *
 * @param roomId      房间 ID
 * @param shortCode   Base62 短码
 * @param title       可选公开名称
 * @param expireAt    过期时刻
 * @param participantCount 当前参与者数量
 */
public record RoomInfoResponse(
        String roomId,
        String shortCode,
        String title,
        OffsetDateTime expireAt,
        long participantCount
) {
    /** 从领域实体 + 参与者数量构建响应。 */
    public static RoomInfoResponse of(EphemeralRoom room, long count) {
        return new RoomInfoResponse(room.getId(), room.getShortCode(),
                room.getTitle(), room.getExpireAt(), count);
    }
}

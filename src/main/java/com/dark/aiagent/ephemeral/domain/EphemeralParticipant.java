package com.dark.aiagent.ephemeral.domain;

import java.time.OffsetDateTime;

/**
 * 匿名参与者值对象（不可变）
 *
 * <p>participant_id 由客户端生成，服务器不绑定真实用户身份。
 * nickname_cipher 为加密昵称，服务器不解密。
 */
public record EphemeralParticipant(
        Long id,
        String roomId,
        String participantId,
        String nicknameCipher,
        OffsetDateTime joinedAt,
        OffsetDateTime lastSeenAt
) {
    /**
     * 工厂方法：新参与者加入。
     *
     * @param roomId        所属房间 ID
     * @param participantId 客户端生成的匿名 UUID
     * @param nicknameCipher 加密昵称（可为 null）
     * @return 新参与者值对象
     */
    public static EphemeralParticipant join(String roomId, String participantId,
                                            String nicknameCipher) {
        OffsetDateTime now = OffsetDateTime.now();
        return new EphemeralParticipant(null, roomId, participantId, nicknameCipher, now, now);
    }
}

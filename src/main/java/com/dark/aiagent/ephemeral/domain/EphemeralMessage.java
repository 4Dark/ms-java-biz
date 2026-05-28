package com.dark.aiagent.ephemeral.domain;

import java.time.OffsetDateTime;

/**
 * 加密消息值对象（不可变）
 *
 * <p>服务器只存 cipherText + iv，永不解密。
 * sender_id 为客户端生成的匿名 UUID，与真实用户无关联。
 */
public record EphemeralMessage(
        Long id,
        String roomId,
        String senderId,
        String cipherText,
        String iv,
        OffsetDateTime sentAt,
        boolean deleted
) {
    /**
     * 工厂方法：创建待发送的新消息值对象（id 由持久层生成）。
     *
     * @param roomId     所属房间 ID
     * @param senderId   发送者匿名 ID
     * @param cipherText AES-GCM 密文（Base64）
     * @param iv         随机初始向量（Base64，每条消息独立）
     * @return 新消息值对象
     */
    public static EphemeralMessage newMessage(String roomId, String senderId,
                                              String cipherText, String iv) {
        return new EphemeralMessage(null, roomId, senderId, cipherText, iv,
                OffsetDateTime.now(), false);
    }
}

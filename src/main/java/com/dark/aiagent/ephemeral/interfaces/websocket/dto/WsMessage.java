package com.dark.aiagent.ephemeral.interfaces.websocket.dto;

/**
 * WebSocket 广播消息体（不可变 record）
 *
 * <p>MSG_CIPHER 类型：payload = cipherText，附带 iv 和 senderId。
 * 事件类型（JOIN/LEAVE/DESTROY/HEARTBEAT）：payload = 参与者ID 或 roomId。
 */
public record WsMessage(
        WsMessageType type,
        String senderId,
        String payload,
        String iv
) {
    /**
     * 构建加密聊天消息。
     *
     * @param senderId   发送者匿名 ID
     * @param cipherText AES-GCM 密文（Base64）
     * @param iv         随机初始向量（Base64）
     */
    public static WsMessage cipher(String senderId, String cipherText, String iv) {
        return new WsMessage(WsMessageType.MSG_CIPHER, senderId, cipherText, iv);
    }

    /**
     * 构建事件消息（JOIN/LEAVE/DESTROY/HEARTBEAT）。
     *
     * @param type    消息类型
     * @param payload 事件载荷（participantId 或 roomId）
     */
    public static WsMessage event(WsMessageType type, String payload) {
        return new WsMessage(type, null, payload, null);
    }
}

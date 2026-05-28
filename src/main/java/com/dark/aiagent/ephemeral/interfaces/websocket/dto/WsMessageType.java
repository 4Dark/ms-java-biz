package com.dark.aiagent.ephemeral.interfaces.websocket.dto;

/**
 * WebSocket 消息类型枚举
 */
public enum WsMessageType {
    /** 加密聊天消息（密文+IV） */
    MSG_CIPHER,
    /** 有人加入房间 */
    JOIN,
    /** 有人退出房间（含删除本人消息信号） */
    LEAVE,
    /** 房间被销毁，所有客户端清空本地缓存 */
    DESTROY,
    /** 心跳，用于活跃延长 TTL */
    HEARTBEAT
}

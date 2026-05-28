package com.dark.aiagent.ephemeral.interfaces.websocket;

import com.dark.aiagent.ephemeral.interfaces.websocket.dto.WsMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * WebSocket 广播处理器
 *
 * <p>负责将消息推送到房间订阅话题 {@code /topic/room/{roomId}}。
 * 客户端收到密文后在本地完成解密，服务器不接触明文。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EphemeralMessageHandler {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 向房间内所有订阅者广播消息。
     *
     * @param roomId  目标房间 ID
     * @param message 待广播的消息体（密文 或 事件）
     */
    public void broadcast(String roomId, WsMessage message) {
        String destination = "/topic/room/" + roomId;
        messagingTemplate.convertAndSend(destination, message);
        log.debug("【WS广播】type={} roomId={}", message.type(), roomId);
    }
}

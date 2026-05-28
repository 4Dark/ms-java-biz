package com.dark.aiagent.ephemeral.interfaces.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * STOMP over WebSocket 配置
 *
 * <p>端点：{@code /ws/ephemeral}（支持 SockJS 降级）
 * 广播话题前缀：{@code /topic}
 * 客户端发送前缀：{@code /app}
 */
@Configuration
@EnableWebSocketMessageBroker
public class EphemeralWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 服务端推送给客户端的前缀
        config.enableSimpleBroker("/topic");
        // 客户端发送消息的前缀（路由到 @MessageMapping 方法）
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/ephemeral")
                // 允许跨域（前端域名通过网关访问）
                .setAllowedOriginPatterns("*")
                // 启用 SockJS 降级（微信内嵌浏览器 WebSocket 不可用时自动降级）
                .withSockJS();
    }
}

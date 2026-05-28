package com.dark.aiagent.ephemeral.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * Redis 消息监听容器配置
 *
 * <p>注册 {@link RedisEphemeralKeyExpireListener} 监听所有数据库的 Key 过期事件。
 */
@Configuration
@RequiredArgsConstructor
public class RedisEphemeralConfig {

    private final RedisEphemeralKeyExpireListener keyExpireListener;

    /**
     * 配置 Redis 消息监听容器，订阅 Key 过期事件频道。
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        // 监听所有 DB 的 Key 过期事件（需 Redis 开启 notify-keyspace-events=Ex）
        container.addMessageListener(keyExpireListener,
                new PatternTopic("__keyevent@*__:expired"));
        return container;
    }
}

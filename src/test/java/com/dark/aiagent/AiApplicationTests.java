package com.dark.aiagent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import com.dark.aiagent.domain.event.repository.EventRepository;
import com.dark.aiagent.domain.mcp.repository.McpPluginRepository;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = AiApplication.class)
@org.springframework.test.context.ActiveProfiles("test")
class AiApplicationTests {

    @MockBean
    private EventRepository eventRepository;

    @MockBean
    private MongoTemplate mongoTemplate;

    @MockBean
    private McpPluginRepository mcpPluginRepository;

    @MockBean
    private RedisConnectionFactory redisConnectionFactory;

    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    @MockBean
    private RedisMessageListenerContainer redisMessageListenerContainer;

    @BeforeEach
    void setup() {
        // 满足 MappingContext 初始化的必要 Mock
        when(mongoTemplate.getConverter()).thenReturn(mock(MongoConverter.class));
    }

    @Test
    void contextLoads() {}

}

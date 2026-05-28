package com.dark.aiagent.ephemeral.application;

import com.dark.aiagent.ephemeral.domain.EphemeralMessage;
import com.dark.aiagent.ephemeral.domain.EphemeralRoom;
import com.dark.aiagent.ephemeral.infrastructure.EphemeralRoomRepository;
import com.dark.aiagent.ephemeral.interfaces.websocket.EphemeralMessageHandler;
import com.dark.aiagent.ephemeral.interfaces.websocket.dto.WsMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EphemeralRoomUseCaseTest {

    @Mock
    private EphemeralRoomRepository repository;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private EphemeralMessageHandler messageHandler;

    private EphemeralRoomUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new EphemeralRoomUseCase(repository, redisTemplate, messageHandler);
        // Avoid NPE when setting redis values if redisTemplate is used
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        org.springframework.test.util.ReflectionTestUtils.setField(useCase, "defaultTtlSeconds", 3600L);
        org.springframework.test.util.ReflectionTestUtils.setField(useCase, "maxTtlSeconds", 604800L);
        org.springframework.test.util.ReflectionTestUtils.setField(useCase, "activityExtendSeconds", 1800L);
    }

    @Test
    void createRoom_should_save_and_publish_event() {


        // Act
        EphemeralRoom result = useCase.createRoom("Test Room", 3600, "user1");

        // Assert
        assertThat(result.getTitle()).isEqualTo("Test Room");
        verify(repository).saveRoom(any(EphemeralRoom.class));
    }

    @Test
    void joinRoom_should_save_participant_and_broadcast() {
        // Arrange
        EphemeralRoom room = EphemeralRoom.create("room123", "abc", "Room", 3600, "user1");
        when(repository.findActiveByRoomId("room123")).thenReturn(Optional.of(room));

        // Act
        useCase.joinRoom("room123", "participantX", null);

        // Assert
        verify(repository).saveParticipantIfAbsent(any());
        verify(messageHandler).broadcast(eq("room123"), any(WsMessage.class));
    }

    @Test
    void sendMessage_should_save_message_and_broadcast() {
        // Arrange
        EphemeralRoom room = EphemeralRoom.create("room123", "abc", "Room", 3600, "user1");
        when(repository.findActiveByRoomId("room123")).thenReturn(Optional.of(room));

        // Act
        EphemeralMessage result = useCase.sendMessage("room123", "senderX", "cipher", "iv123");

        // Assert
        assertThat(result.senderId()).isEqualTo("senderX");
        verify(repository).saveMessage(any(EphemeralMessage.class));
        verify(messageHandler).broadcast(eq("room123"), any(WsMessage.class));
    }

    @Test
    void destroyRoom_should_broadcast_and_delete() {
        // Arrange
        when(repository.findActiveByRoomId("room123")).thenReturn(Optional.of(EphemeralRoom.create("room123", "abc12345", "Room", 3600, "user1")));

        // Act
        useCase.destroyRoom("room123");

        // Assert
        verify(messageHandler).broadcast(eq("room123"), any(WsMessage.class));
        verify(repository).deleteRoomById("room123");
    }
}

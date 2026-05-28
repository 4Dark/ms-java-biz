package com.dark.aiagent.ephemeral.interfaces.rest;

import com.dark.aiagent.ephemeral.application.EphemeralRoomUseCase;
import com.dark.aiagent.ephemeral.domain.EphemeralMessage;
import com.dark.aiagent.ephemeral.domain.EphemeralRoom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EphemeralRoomController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security for tests
class EphemeralRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EphemeralRoomUseCase useCase;

    @Test
    void createRoom_should_return_200() throws Exception {
        EphemeralRoom room = EphemeralRoom.create("room123", "abc", "Test", 3600, "creator");
        when(useCase.createRoom(anyString(), anyLong(), anyString())).thenReturn(room);

        String json = """
                {
                  "title": "Test",
                  "ttlSeconds": 3600,
                  "createdBy": "creator"
                }
                """;

        mockMvc.perform(post("/rest/biz/v1/ephemeral/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomId").value(room.getId()))
                .andExpect(jsonPath("$.shortCode").value(room.getShortCode()));
    }

    @Test
    void getRoomInfo_should_return_200_if_exists() throws Exception {
        EphemeralRoom room = EphemeralRoom.create("room123", "code123", "Test", 3600, "creator");
        when(useCase.findRoom("code123")).thenReturn(Optional.of(room));
        when(useCase.countParticipants(room.getId())).thenReturn(2L);

        mockMvc.perform(get("/rest/biz/v1/ephemeral/rooms/code123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test"))
                .andExpect(jsonPath("$.participantCount").value(2));
    }

    @Test
    void getRoomInfo_should_return_410_if_not_found() throws Exception {
        when(useCase.findRoom("code123")).thenReturn(Optional.empty());

        mockMvc.perform(get("/rest/biz/v1/ephemeral/rooms/code123"))
                .andExpect(status().isGone());
    }

    @Test
    void joinRoom_should_return_200() throws Exception {
        String json = """
                {
                  "participantId": "p1"
                }
                """;

        mockMvc.perform(post("/rest/biz/v1/ephemeral/rooms/room123/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void sendMessage_should_return_200() throws Exception {
        EphemeralMessage msg = new EphemeralMessage(10L, "room123", "p1", "cipher", "iv", null, false);
        when(useCase.sendMessage(anyString(), anyString(), anyString(), anyString())).thenReturn(msg);

        String json = """
                {
                  "senderId": "p1",
                  "cipherText": "cipher",
                  "iv": "iv"
                }
                """;

        mockMvc.perform(post("/rest/biz/v1/ephemeral/rooms/room123/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }
}

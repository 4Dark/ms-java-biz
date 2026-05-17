package com.dark.aiagent.interfaces.chat.controller;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.dark.aiagent.application.chat.service.ChatApplicationService;
import com.dark.aiagent.config.SecurityConfig;

@WebMvcTest(value = ChatHistoryController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = SecurityConfig.class))
class ChatHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatApplicationService chatApplicationService;

    @Test
    void deleteSession_ShouldReturnNoContent() throws Exception {
        String sessionId = "test-session-id";

        mockMvc.perform(delete("/rest/biz/v1/history/sessions/{sessionId}", sessionId)
                .contentType(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isNoContent());

        verify(chatApplicationService).deleteSession(sessionId);
    }
}

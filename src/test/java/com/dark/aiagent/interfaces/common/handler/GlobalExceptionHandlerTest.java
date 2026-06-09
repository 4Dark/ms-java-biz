package com.dark.aiagent.interfaces.common.handler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {GlobalExceptionHandlerTest.TestController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    static class Config {
        @Bean
        public TestController testController() {
            return new TestController();
        }
    }

    @RestController
    static class TestController {
        @GetMapping("/test/timeout")
        public void triggerTimeout() {
            throw new AsyncRequestTimeoutException();
        }

        @GetMapping("/mcp/sse")
        public void triggerSseTimeout() {
            throw new AsyncRequestTimeoutException();
        }
    }

    @Test
    void testHandleAsyncRequestTimeoutException_NonSse() throws Exception {
        mockMvc.perform(get("/test/timeout"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error_msg").value("Timeout"))
                .andExpect(jsonPath("$.status").value(503));
    }

    @Test
    void testHandleAsyncRequestTimeoutException_Sse() throws Exception {
        mockMvc.perform(get("/mcp/sse").header("Accept", "text/event-stream"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string("error: Timeout"));
    }
}

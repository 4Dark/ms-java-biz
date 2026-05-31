package com.dark.aiagent.module.aidev.interfaces.rest;

import com.dark.aiagent.module.aidev.application.AiDevTaskUseCase;
import com.dark.aiagent.module.aidev.domain.entity.AiDevTask;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@WebMvcTest(AiDevTaskController.class)
@AutoConfigureMockMvc(addFilters = false)
class AiDevTaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AiDevTaskUseCase useCase;

    @Test
    void shouldReturnTaskList() throws Exception {
        AiDevTask task = new AiDevTask(
                "task-123", "Setup DB", "Init sql", "PLANNING", "branch-1", 
                0.05, OffsetDateTime.now(), OffsetDateTime.now()
        );
        when(useCase.getAllTasks()).thenReturn(List.of(task));

        mockMvc.perform(get("/rest/biz/v1/ai-dev/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("task-123"))
                .andExpect(jsonPath("$[0].title").value("Setup DB"))
                .andExpect(jsonPath("$[0].status").value("PLANNING"));
    }

    @Test
    void shouldResumeTask() throws Exception {
        doNothing().when(useCase).resumeTask("task-123", null);

        mockMvc.perform(post("/rest/biz/v1/ai-dev/tasks/task-123/resume"))
                .andExpect(status().isOk());

        verify(useCase, times(1)).resumeTask("task-123", null);
    }

    @Test
    void shouldRollbackTask() throws Exception {
        doNothing().when(useCase).rollbackTask("task-123");

        mockMvc.perform(post("/rest/biz/v1/ai-dev/tasks/task-123/rollback"))
                .andExpect(status().isOk());

        verify(useCase, times(1)).rollbackTask("task-123");
    }

    @Test
    void shouldDeleteTask() throws Exception {
        doNothing().when(useCase).deleteTask("task-123");

        mockMvc.perform(delete("/rest/biz/v1/ai-dev/tasks/task-123"))
                .andExpect(status().isOk());

        verify(useCase, times(1)).deleteTask("task-123");
    }
}

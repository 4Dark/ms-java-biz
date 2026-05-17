package com.dark.aiagent.interfaces.rest;

import com.dark.aiagent.application.task.TaskApplicationService;
import com.dark.aiagent.config.SecurityConfig;
import com.dark.aiagent.domain.task.entity.TaskRecord;
import org.junit.jupiter.api.DisplayName;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    value = TaskController.class,
    excludeAutoConfiguration = {SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class},
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskApplicationService taskApplicationService;

    @Test
    @DisplayName("GET /rest/biz/v1/tasks/{taskId} 应当返回任务进度详情")
    void shouldReturnTaskProgressDetails() throws Exception {
        // Given
        String taskId = "task-999";
        TaskRecord mockTask = new TaskRecord(taskId, "KNOWLEDGE_BUILD");
        mockTask.updateProgress(100, 45, "recipe_fish.md");

        when(taskApplicationService.getTaskProgress(taskId)).thenReturn(mockTask);

        // When & Then
        mockMvc.perform(get("/rest/biz/v1/tasks/{taskId}", taskId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.taskType").value("KNOWLEDGE_BUILD"))
                .andExpect(jsonPath("$.status").value("RUNNING"))
                .andExpect(jsonPath("$.totalCount").value(100))
                .andExpect(jsonPath("$.processedCount").value(45))
                .andExpect(jsonPath("$.currentItemName").value("recipe_fish.md"));
    }
}

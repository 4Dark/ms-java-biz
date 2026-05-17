package com.dark.aiagent.application.task;

import com.dark.aiagent.domain.common.exception.BusinessException;
import com.dark.aiagent.domain.task.entity.TaskRecord;
import com.dark.aiagent.domain.task.repository.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskApplicationServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskApplicationService taskApplicationService;

    @Test
    @DisplayName("应当能正确查询存在的任务进度")
    void shouldGetTaskProgressSuccessfully() {
        // Given
        String taskId = "task-abc";
        TaskRecord mockTask = new TaskRecord(taskId, "KNOWLEDGE_BUILD");
        mockTask.updateProgress(50, 20, "file.md");

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(mockTask));

        // When
        TaskRecord result = taskApplicationService.getTaskProgress(taskId);

        // Then
        assertNotNull(result);
        assertEquals(taskId, result.getId());
        assertEquals("KNOWLEDGE_BUILD", result.getTaskType());
        assertEquals("RUNNING", result.getStatus());
        assertEquals(50, result.getTotalCount());
        assertEquals(20, result.getProcessedCount());
        assertEquals("file.md", result.getCurrentItemName());
    }

    @Test
    @DisplayName("当任务ID不存在时应抛出BusinessException (DEP_0600)")
    void shouldThrowExceptionWhenTaskNotFound() {
        // Given
        String taskId = "non-existent-task";
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            taskApplicationService.getTaskProgress(taskId);
        });

        assertEquals("DEP_0600", exception.getErrorCode());
        assertTrue(exception.getMessage().contains("任务不存在"));
    }

    @Test
    @DisplayName("当传入的任务ID为空时应抛出BusinessException (DEP_0600)")
    void shouldThrowExceptionWhenTaskIdIsEmpty() {
        // When & Then
        BusinessException exception1 = assertThrows(BusinessException.class, () -> {
            taskApplicationService.getTaskProgress("");
        });
        assertEquals("DEP_0600", exception1.getErrorCode());

        BusinessException exception2 = assertThrows(BusinessException.class, () -> {
            taskApplicationService.getTaskProgress(null);
        });
        assertEquals("DEP_0600", exception2.getErrorCode());
    }
}

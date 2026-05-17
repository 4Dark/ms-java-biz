package com.dark.aiagent.domain.task.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TaskRecordTest {

    @Test
    public void testTaskRecordInitialization() {
        TaskRecord task = new TaskRecord("task-123", "KNOWLEDGE_BUILD");
        assertEquals("task-123", task.getId());
        assertEquals("KNOWLEDGE_BUILD", task.getTaskType());
        assertEquals("RUNNING", task.getStatus());
        assertEquals(0, task.getTotalCount());
        assertEquals(0, task.getProcessedCount());
        assertNull(task.getCurrentItemName());
        assertNull(task.getErrorMessage());
        assertNotNull(task.getCreateTime());
        assertNotNull(task.getUpdateTime());
    }

    @Test
    public void testTaskRecordInitializationInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> new TaskRecord("", "KNOWLEDGE_BUILD"));
        assertThrows(IllegalArgumentException.class, () -> new TaskRecord(null, "KNOWLEDGE_BUILD"));
        assertThrows(IllegalArgumentException.class, () -> new TaskRecord("task-123", ""));
        assertThrows(IllegalArgumentException.class, () -> new TaskRecord("task-123", null));
    }

    @Test
    public void testUpdateProgress() {
        TaskRecord task = new TaskRecord("task-123", "KNOWLEDGE_BUILD");
        task.updateProgress(100, 50, "test_recipe.md");
        assertEquals(100, task.getTotalCount());
        assertEquals(50, task.getProcessedCount());
        assertEquals("test_recipe.md", task.getCurrentItemName());
        assertNotNull(task.getUpdateTime());
    }

    @Test
    public void testUpdateProgressInvalidProcessedCount() {
        TaskRecord task = new TaskRecord("task-123", "KNOWLEDGE_BUILD");
        assertThrows(IllegalArgumentException.class, () -> task.updateProgress(100, -1, "test_recipe.md"));
    }

    @Test
    public void testComplete() {
        TaskRecord task = new TaskRecord("task-123", "KNOWLEDGE_BUILD");
        task.complete();
        assertEquals("SUCCESS", task.getStatus());
    }

    @Test
    public void testFail() {
        TaskRecord task = new TaskRecord("task-123", "KNOWLEDGE_BUILD");
        task.fail("Network timeout error");
        assertEquals("FAILED", task.getStatus());
        assertEquals("Network timeout error", task.getErrorMessage());
    }
}

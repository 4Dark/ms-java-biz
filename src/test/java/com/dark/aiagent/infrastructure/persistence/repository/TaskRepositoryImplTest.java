package com.dark.aiagent.infrastructure.persistence.repository;

import com.dark.aiagent.domain.task.entity.TaskRecord;
import com.dark.aiagent.infrastructure.persistence.entity.TaskRecordDO;
import com.dark.aiagent.infrastructure.persistence.mapper.TaskRecordMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskRepositoryImplTest {

    @Mock
    private TaskRecordMapper mapper;

    @InjectMocks
    private TaskRepositoryImpl repository;

    @Test
    @DisplayName("应该能成功保存任务，并正确映射DO字段")
    void shouldSaveTaskSuccessfully() {
        // Given
        TaskRecord task = new TaskRecord("task-789", "KNOWLEDGE_BUILD");
        task.updateProgress(100, 10, "doc1.md");

        // When
        repository.save(task);

        // Then
        ArgumentCaptor<TaskRecordDO> captor = ArgumentCaptor.forClass(TaskRecordDO.class);
        verify(mapper).insert(captor.capture());
        TaskRecordDO savedDo = captor.getValue();

        assertEquals("task-789", savedDo.getId());
        assertEquals("KNOWLEDGE_BUILD", savedDo.getTaskType());
        assertEquals("RUNNING", savedDo.getStatus());
        assertEquals(100, savedDo.getTotalCount());
        assertEquals(10, savedDo.getProcessedCount());
        assertEquals("doc1.md", savedDo.getCurrentItemName());
        assertNotNull(savedDo.getCreateTime());
        assertNotNull(savedDo.getUpdateTime());
    }

    @Test
    @DisplayName("应该能根据ID查询任务，并正确映射回领域实体")
    void shouldFindByIdAndMapToDomain() {
        // Given
        TaskRecordDO doObject = new TaskRecordDO();
        doObject.setId("task-789");
        doObject.setTaskType("KNOWLEDGE_BUILD");
        doObject.setStatus("SUCCESS");
        doObject.setTotalCount(50);
        doObject.setProcessedCount(50);
        doObject.setCurrentItemName("done.md");
        doObject.setCreateTime(OffsetDateTime.now());
        doObject.setUpdateTime(OffsetDateTime.now());

        when(mapper.selectById("task-789")).thenReturn(doObject);

        // When
        Optional<TaskRecord> optionalTask = repository.findById("task-789");

        // Then
        assertTrue(optionalTask.isPresent());
        TaskRecord task = optionalTask.get();
        assertEquals("task-789", task.getId());
        assertEquals("KNOWLEDGE_BUILD", task.getTaskType());
        assertEquals("SUCCESS", task.getStatus());
        assertEquals(50, task.getTotalCount());
        assertEquals(50, task.getProcessedCount());
        assertEquals("done.md", task.getCurrentItemName());
        assertNull(task.getErrorMessage());
    }

    @Test
    @DisplayName("更新任务时应该调用mapper的updateById方法")
    void shouldUpdateTaskSuccessfully() {
        // Given
        TaskRecord task = new TaskRecord("task-789", "KNOWLEDGE_BUILD");
        task.complete();

        // When
        repository.update(task);

        // Then
        ArgumentCaptor<TaskRecordDO> captor = ArgumentCaptor.forClass(TaskRecordDO.class);
        verify(mapper).updateById(captor.capture());
        TaskRecordDO updatedDo = captor.getValue();

        assertEquals("task-789", updatedDo.getId());
        assertEquals("SUCCESS", updatedDo.getStatus());
    }
}

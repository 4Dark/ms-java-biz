package com.dark.aiagent.domain.task.repository;

import com.dark.aiagent.domain.task.entity.TaskRecord;
import java.util.Optional;

/**
 * 任务进度领域仓储接口
 * 倒置依赖：定义在 Domain 层，由 Infrastructure 层实现
 */
public interface TaskRepository {
    void save(TaskRecord taskRecord);
    Optional<TaskRecord> findById(String id);
    void update(TaskRecord taskRecord);
}

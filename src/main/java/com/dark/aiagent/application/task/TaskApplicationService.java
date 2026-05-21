package com.dark.aiagent.application.task;

import com.dark.aiagent.domain.common.exception.BusinessException;
import com.dark.aiagent.domain.task.entity.TaskRecord;
import com.dark.aiagent.domain.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskApplicationService {

    private final TaskRepository taskRepository;

    /**
     * 查询指定 ID 的后台任务进度
     *
     * @param taskId 任务ID
     * @return 任务进度实体
     */
    public TaskRecord getTaskProgress(String taskId) {
        if (taskId == null || taskId.isBlank()) {
            throw new BusinessException("DEP_0600", "任务 ID 不能为空");
        }
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException("DEP_0600", "任务不存在，ID: " + taskId));
    }
}

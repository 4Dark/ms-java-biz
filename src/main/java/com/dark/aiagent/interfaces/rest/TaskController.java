package com.dark.aiagent.interfaces.rest;

import com.dark.aiagent.application.task.TaskApplicationService;
import com.dark.aiagent.domain.task.entity.TaskRecord;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Background Task Management")
@RestController
@RequestMapping("/rest/biz/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskApplicationService taskApplicationService;

    @Operation(summary = "Get background task progress")
    @GetMapping("/{taskId}")
    public TaskRecord getTaskProgress(@PathVariable("taskId") String taskId) {
        return taskApplicationService.getTaskProgress(taskId);
    }
}

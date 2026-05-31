package com.dark.aiagent.module.aidev.interfaces.rest;

import com.dark.aiagent.module.aidev.application.AiDevTaskUseCase;
import com.dark.aiagent.module.aidev.domain.entity.AiDevTask;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest/biz/v1/ai-dev/tasks")
public class AiDevTaskController {

    private final AiDevTaskUseCase useCase;

    public AiDevTaskController(AiDevTaskUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping
    public ResponseEntity<List<AiDevTaskResponse>> listTasks() {

        List<AiDevTaskResponse> responses = useCase.getAllTasks().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * 从 Web UI 创建一个新的 AI 开发任务。
     * 任务初始状态为 PENDING，ms-ai-devops 常驻服务将自动轮询并执行。
     */
    @PostMapping
    public ResponseEntity<AiDevTaskResponse> createTask(@RequestBody AiDevCreateRequest request) {
        AiDevTask task = useCase.createTask(request.description());
        return ResponseEntity.ok(toResponse(task));
    }

    @PostMapping("/{id}/resume")
    public ResponseEntity<Void> resumeTask(@PathVariable String id, @RequestBody(required = false) AiDevMessageRequest request) {
        String feedback = request != null ? request.content() : null;
        useCase.resumeTask(id, feedback);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/rollback")
    public ResponseEntity<Void> rollbackTask(@PathVariable String id) {
        useCase.rollbackTask(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        useCase.deleteTask(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<List<AiDevChatMessageResponse>> getChatMessages(@PathVariable String id) {
        List<AiDevChatMessageResponse> responses = useCase.getChatMessages(id).stream()
                .map(msg -> new AiDevChatMessageResponse(
                        msg.getId(),
                        msg.getTaskId(),
                        msg.getSenderRole(),
                        msg.getContent(),
                        msg.getCreateTime()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<AiDevChatMessageResponse> addHumanMessage(@PathVariable String id, @RequestBody AiDevMessageRequest request) {
        var msg = useCase.addHumanMessage(id, request.content());
        return ResponseEntity.ok(new AiDevChatMessageResponse(
                msg.getId(),
                msg.getTaskId(),
                msg.getSenderRole(),
                msg.getContent(),
                msg.getCreateTime()
        ));
    }

    private AiDevTaskResponse toResponse(AiDevTask task) {
        return new AiDevTaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getBranchName(),
                task.getTotalCost(),
                task.getCreateTime(),
                task.getUpdateTime()
        );
    }
}

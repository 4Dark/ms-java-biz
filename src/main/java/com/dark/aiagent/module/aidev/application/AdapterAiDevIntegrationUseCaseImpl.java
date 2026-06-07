package com.dark.aiagent.module.aidev.application;

import com.dark.aiagent.module.aidev.domain.entity.AiDevChatMessage;
import com.dark.aiagent.module.aidev.domain.entity.AiDevTask;
import com.dark.aiagent.module.aidev.domain.repository.AiDevChatMessageRepository;
import com.dark.aiagent.module.aidev.domain.repository.AiDevTaskRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * AI 开发任务应用服务 (轨道 A: 适配器模式)。
 *
 * <p>职责边界：本服务负责 PostgreSQL 数据库的读写操作。
 * ms-ai-devops 将拉起 Hermes CLI 执行任务并使用 Webhook 回调。
 */
@Service
@ConditionalOnProperty(name = "ai-dev.integration.mode", havingValue = "ADAPTER", matchIfMissing = true)
public class AdapterAiDevIntegrationUseCaseImpl implements AiDevIntegrationUseCase {

    private final AiDevTaskRepository repository;
    private final AiDevChatMessageRepository chatMessageRepository;

    public AdapterAiDevIntegrationUseCaseImpl(AiDevTaskRepository repository, AiDevChatMessageRepository chatMessageRepository) {
        this.repository = repository;
        this.chatMessageRepository = chatMessageRepository;
    }

    public List<AiDevTask> getAllTasks() {
        return repository.findAll();
    }

    public Optional<AiDevTask> getTaskById(String id) {
        return repository.findById(id);
    }

    public List<AiDevChatMessage> getChatMessages(String taskId) {
        return chatMessageRepository.findByTaskId(taskId);
    }

    /**
     * 创建新任务，初始状态设为 PENDING。
     * ms-ai-devops 常驻服务会自动轮询并拾取该任务执行。
     *
     * @param description 自然语言任务描述
     * @return 新创建的任务
     */
    public AiDevTask createTask(String description) {
        String taskId = UUID.randomUUID().toString();
        String title = description.length() > 50 ? description.substring(0, 50) + "..." : description;
        OffsetDateTime now = OffsetDateTime.now();
        AiDevTask task = new AiDevTask(taskId, title, description, "PENDING", null, 0.0, null, now, now);
        repository.save(task);
        return task;
    }

    /**
     * 人类审批后提供反馈，将任务状态更新为 WAITING_RESUME。
     * ms-ai-devops 常驻服务会轮询到此状态并携带 humanFeedback 恢复执行。
     *
     * @param id       任务 ID
     * @param feedback 人类反馈内容（可为空，表示直接批准）
     */
    public void resumeTask(String id, String feedback) {
        AiDevTask task = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + id));

        // 写入人类消息到聊天记录
        String content = feedback != null && !feedback.isBlank() ? feedback : "Approved to proceed.";
        addHumanMessage(id, content);

        // 将 humanFeedback 写入数据库，并改变状态为 WAITING_RESUME，由 ms-ai-devops 轮询拾取
        AiDevTask updated = new AiDevTask(
                task.getId(), task.getTitle(), task.getDescription(),
                "WAITING_RESUME", task.getBranchName(), task.getTotalCost(),
                content, task.getCreateTime(), OffsetDateTime.now()
        );
        repository.save(updated);
    }

    /**
     * 触发回滚：将任务状态更新为 ROLLBACK_REQUESTED。
     * ms-ai-devops 常驻服务轮询到该状态后，执行 git branch 清理操作。
     *
     * @param id 任务 ID
     */
    public void rollbackTask(String id) {
        AiDevTask task = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + id));

        AiDevTask updated = new AiDevTask(
                task.getId(), task.getTitle(), task.getDescription(),
                "ROLLBACK_REQUESTED", task.getBranchName(), task.getTotalCost(),
                task.getHumanFeedback(), task.getCreateTime(), OffsetDateTime.now()
        );
        repository.save(updated);
    }

    public AiDevChatMessage addHumanMessage(String taskId, String content) {
        // ms-java-biz 只负责写入人类消息，后续由 ms-ai-devops 常驻进程去轮询判断是否包含 @ 并响应
        Boolean isProcessed = false;
        AiDevChatMessage message = new AiDevChatMessage(
                UUID.randomUUID().toString(),
                taskId,
                "HUMAN",
                content,
                OffsetDateTime.now(),
                isProcessed
        );
        chatMessageRepository.save(message);
        return message;
    }

    /**
     * 更新任务的头脑风暴配置参数，写入 PostgreSQL。
     */
    @Override
    public void updateTaskConfig(String id, int maxBrainstormingRounds, int contextSlidingWindow) {
        AiDevTask task = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + id));
        AiDevTask updated = new AiDevTask(
                task.getId(), task.getTitle(), task.getDescription(),
                task.getStatus(), task.getBranchName(), task.getTotalCost(),
                task.getHumanFeedback(), task.getCreateTime(), OffsetDateTime.now(),
                maxBrainstormingRounds, contextSlidingWindow
        );
        repository.save(updated);
    }

    public void deleteTask(String id) {
        chatMessageRepository.deleteByTaskId(id);
        repository.deleteById(id);
    }
}

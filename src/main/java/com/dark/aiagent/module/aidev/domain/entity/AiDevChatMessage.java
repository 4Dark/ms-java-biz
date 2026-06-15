package com.dark.aiagent.module.aidev.domain.entity;

import java.time.OffsetDateTime;

public class AiDevChatMessage {

    private String id;
    private String taskId;
    private String senderRole;
    private String content;
    private OffsetDateTime createTime;
    private Boolean isProcessed;

    private AiDevChatMessage() {
        // Default constructor for reflective frameworks like MyBatis
    }

    public AiDevChatMessage(String id, String taskId, String senderRole, String content, OffsetDateTime createTime, Boolean isProcessed) {
        this.id = id;
        this.taskId = taskId;
        this.senderRole = senderRole;
        this.content = content;
        this.createTime = createTime;
        this.isProcessed = isProcessed;
    }

    public String getId() {
        return id;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getSenderRole() {
        return senderRole;
    }

    public String getContent() {
        return content;
    }

    public OffsetDateTime getCreateTime() {
        return createTime;
    }

    public Boolean getIsProcessed() {
        return isProcessed;
    }

    // Methods for state transitions or behavior if necessary
}

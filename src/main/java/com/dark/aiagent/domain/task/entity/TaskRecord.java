package com.dark.aiagent.domain.task.entity;

import java.util.Date;
import java.util.Objects;

/**
 * 任务进度领域实体 (充血模型)
 */
public class TaskRecord {
    private final String id;
    private final String taskType;
    private String status;
    private int totalCount;
    private int processedCount;
    private String currentItemName;
    private String errorMessage;
    private Date createTime;
    private Date updateTime;

    // 私有无参构造（兼容反射）
    private TaskRecord() {
        this.id = null;
        this.taskType = null;
    }

    // 公开构造
    public TaskRecord(String id, String taskType) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Task ID cannot be null or empty");
        if (taskType == null || taskType.isBlank()) throw new IllegalArgumentException("Task type cannot be null or empty");
        this.id = id;
        this.taskType = taskType;
        this.status = "RUNNING"; // 初始状态
        this.totalCount = 0;
        this.processedCount = 0;
        this.createTime = new Date();
        this.updateTime = new Date();
    }

    // 用于从持久化层重建领域实体的构造函数
    public TaskRecord(String id, String taskType, String status, int totalCount, int processedCount, String currentItemName, String errorMessage, Date createTime, Date updateTime) {
        this.id = id;
        this.taskType = taskType;
        this.status = status;
        this.totalCount = totalCount;
        this.processedCount = processedCount;
        this.currentItemName = currentItemName;
        this.errorMessage = errorMessage;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    // 业务语义方法
    public void updateProgress(int totalCount, int processedCount, String currentItemName) {
        if (processedCount < 0) throw new IllegalArgumentException("Processed count cannot be negative");
        this.totalCount = totalCount;
        this.processedCount = processedCount;
        this.currentItemName = currentItemName;
        this.updateTime = new Date();
    }

    public void complete() {
        this.status = "SUCCESS";
        this.updateTime = new Date();
    }

    public void fail(String errorMessage) {
        this.status = "FAILED";
        this.errorMessage = errorMessage;
        this.updateTime = new Date();
    }

    // Getters
    public String getId() { return id; }
    public String getTaskType() { return taskType; }
    public String getStatus() { return status; }
    public int getTotalCount() { return totalCount; }
    public int getProcessedCount() { return processedCount; }
    public String getCurrentItemName() { return currentItemName; }
    public String getErrorMessage() { return errorMessage; }
    public Date getCreateTime() { return createTime; }
    public Date getUpdateTime() { return updateTime; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskRecord that = (TaskRecord) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

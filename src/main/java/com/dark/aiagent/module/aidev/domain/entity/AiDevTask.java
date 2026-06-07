package com.dark.aiagent.module.aidev.domain.entity;

import java.time.OffsetDateTime;
import java.util.Objects;

public class AiDevTask {
    private String id;
    private String title;
    private String description;
    private String status;
    private String branchName;
    private Double totalCost;
    private String humanFeedback;
    private OffsetDateTime createTime;
    private OffsetDateTime updateTime;
    private Integer maxBrainstormingRounds;
    private Integer contextSlidingWindow;

    private AiDevTask() {}

    public AiDevTask(String id, String title, String description, String status, String branchName, Double totalCost, String humanFeedback, OffsetDateTime createTime, OffsetDateTime updateTime) {
        this(id, title, description, status, branchName, totalCost, humanFeedback, createTime, updateTime, 5, 3);
    }

    public AiDevTask(String id, String title, String description, String status, String branchName, Double totalCost, String humanFeedback, OffsetDateTime createTime, OffsetDateTime updateTime, Integer maxBrainstormingRounds, Integer contextSlidingWindow) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.branchName = branchName;
        this.totalCost = totalCost;
        this.humanFeedback = humanFeedback;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.maxBrainstormingRounds = maxBrainstormingRounds != null ? maxBrainstormingRounds : 5;
        this.contextSlidingWindow = contextSlidingWindow != null ? contextSlidingWindow : 3;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public String getBranchName() { return branchName; }
    public Double getTotalCost() { return totalCost; }
    public String getHumanFeedback() { return humanFeedback; }
    public OffsetDateTime getCreateTime() { return createTime; }
    public OffsetDateTime getUpdateTime() { return updateTime; }
    public Integer getMaxBrainstormingRounds() { return maxBrainstormingRounds; }
    public Integer getContextSlidingWindow() { return contextSlidingWindow; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AiDevTask that = (AiDevTask) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

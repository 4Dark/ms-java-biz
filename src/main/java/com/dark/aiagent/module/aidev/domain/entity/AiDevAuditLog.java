package com.dark.aiagent.module.aidev.domain.entity;

import java.time.OffsetDateTime;

public class AiDevAuditLog {
    private String id;
    private String taskId;
    private String agentRole;
    private String providerModel;
    private String actionType;
    private Integer promptTokens;
    private Integer compTokens;
    private Double cost;
    private Integer durationMs;
    private OffsetDateTime createTime;

    public AiDevAuditLog(String id, String taskId, String agentRole, String providerModel, String actionType, Integer promptTokens, Integer compTokens, Double cost, Integer durationMs, OffsetDateTime createTime) {
        this.id = id;
        this.taskId = taskId;
        this.agentRole = agentRole;
        this.providerModel = providerModel;
        this.actionType = actionType;
        this.promptTokens = promptTokens != null ? promptTokens : 0;
        this.compTokens = compTokens != null ? compTokens : 0;
        this.cost = cost != null ? cost : 0.0;
        this.durationMs = durationMs != null ? durationMs : 0;
        this.createTime = createTime;
    }

    public String getId() { return id; }
    public String getTaskId() { return taskId; }
    public String getAgentRole() { return agentRole; }
    public String getProviderModel() { return providerModel; }
    public String getActionType() { return actionType; }
    public Integer getPromptTokens() { return promptTokens; }
    public Integer getCompTokens() { return compTokens; }
    public Double getCost() { return cost; }
    public Integer getDurationMs() { return durationMs; }
    public OffsetDateTime getCreateTime() { return createTime; }
}

package com.dark.aiagent.module.aidev.infrastructure.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.OffsetDateTime;

@TableName(value = "ai_dev_task", autoResultMap = true)
public class AiDevTaskPO {
    
    @TableId(type = IdType.INPUT)
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

    @com.baomidou.mybatisplus.annotation.TableField(typeHandler = com.dark.aiagent.infrastructure.persistence.handler.PostgresJacksonTypeHandler.class)
    private java.util.List<String> relatedWorkspaces;

    private String targetBranch;
    private String relatedIssues;
    private String constraints;
    private String priority;

    @com.baomidou.mybatisplus.annotation.TableField(typeHandler = com.dark.aiagent.infrastructure.persistence.handler.PostgresJacksonTypeHandler.class)
    private java.util.List<String> affectedProjects;

    @com.baomidou.mybatisplus.annotation.TableField(typeHandler = com.dark.aiagent.infrastructure.persistence.handler.PostgresJacksonTypeHandler.class)
    private java.util.List<String> labels;

    private String engineMode;

    @com.baomidou.mybatisplus.annotation.TableField(typeHandler = com.dark.aiagent.infrastructure.persistence.handler.PostgresJacksonTypeHandler.class)
    private java.util.List<String> assignedRoles;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }
    
    public Double getTotalCost() { return totalCost; }
    public void setTotalCost(Double totalCost) { this.totalCost = totalCost; }

    public String getHumanFeedback() { return humanFeedback; }
    public void setHumanFeedback(String humanFeedback) { this.humanFeedback = humanFeedback; }
    
    public OffsetDateTime getCreateTime() { return createTime; }
    public void setCreateTime(OffsetDateTime createTime) { this.createTime = createTime; }
    
    public OffsetDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(OffsetDateTime updateTime) { this.updateTime = updateTime; }

    public Integer getMaxBrainstormingRounds() { return maxBrainstormingRounds; }
    public void setMaxBrainstormingRounds(Integer maxBrainstormingRounds) { this.maxBrainstormingRounds = maxBrainstormingRounds; }

    public Integer getContextSlidingWindow() { return contextSlidingWindow; }
    public void setContextSlidingWindow(Integer contextSlidingWindow) { this.contextSlidingWindow = contextSlidingWindow; }

    public java.util.List<String> getRelatedWorkspaces() { return relatedWorkspaces; }
    public void setRelatedWorkspaces(java.util.List<String> relatedWorkspaces) { this.relatedWorkspaces = relatedWorkspaces; }

    public String getTargetBranch() { return targetBranch; }
    public void setTargetBranch(String targetBranch) { this.targetBranch = targetBranch; }

    public String getRelatedIssues() { return relatedIssues; }
    public void setRelatedIssues(String relatedIssues) { this.relatedIssues = relatedIssues; }

    public String getConstraints() { return constraints; }
    public void setConstraints(String constraints) { this.constraints = constraints; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public java.util.List<String> getAffectedProjects() { return affectedProjects; }
    public void setAffectedProjects(java.util.List<String> affectedProjects) { this.affectedProjects = affectedProjects; }

    public java.util.List<String> getLabels() { return labels; }
    public void setLabels(java.util.List<String> labels) { this.labels = labels; }

    public String getEngineMode() { return engineMode; }
    public void setEngineMode(String engineMode) { this.engineMode = engineMode; }

    public java.util.List<String> getAssignedRoles() { return assignedRoles; }
    public void setAssignedRoles(java.util.List<String> assignedRoles) { this.assignedRoles = assignedRoles; }
}

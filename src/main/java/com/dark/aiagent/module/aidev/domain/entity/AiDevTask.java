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
    private java.util.List<String> relatedWorkspaces;
    private String targetBranch;
    private String relatedIssues;
    private String constraints;
    private String priority;
    private java.util.List<String> affectedProjects;
    private java.util.List<String> labels;
    private String engineMode;
    private java.util.List<String> assignedRoles;

    private AiDevTask() {}

    public AiDevTask(String id, String title, String description, String status, String branchName, Double totalCost, String humanFeedback, OffsetDateTime createTime, OffsetDateTime updateTime) {
        this(id, title, description, status, branchName, totalCost, humanFeedback, createTime, updateTime, 5, 3, new java.util.ArrayList<>(), null, null, null, null, new java.util.ArrayList<>(), new java.util.ArrayList<>(), "HERMES_SINGLE", new java.util.ArrayList<>());
    }

    public AiDevTask(String id, String title, String description, String status, String branchName, Double totalCost, String humanFeedback, OffsetDateTime createTime, OffsetDateTime updateTime, Integer maxBrainstormingRounds, Integer contextSlidingWindow, java.util.List<String> relatedWorkspaces, String targetBranch, String relatedIssues, String constraints, String priority, java.util.List<String> affectedProjects, java.util.List<String> labels, String engineMode) {
        this(id, title, description, status, branchName, totalCost, humanFeedback, createTime, updateTime, maxBrainstormingRounds, contextSlidingWindow, relatedWorkspaces, targetBranch, relatedIssues, constraints, priority, affectedProjects, labels, engineMode, new java.util.ArrayList<>());
    }

    public AiDevTask(String id, String title, String description, String status, String branchName, Double totalCost, String humanFeedback, OffsetDateTime createTime, OffsetDateTime updateTime, Integer maxBrainstormingRounds, Integer contextSlidingWindow, java.util.List<String> relatedWorkspaces, String targetBranch, String relatedIssues, String constraints, String priority, java.util.List<String> affectedProjects, java.util.List<String> labels, String engineMode, java.util.List<String> assignedRoles) {
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
        this.relatedWorkspaces = relatedWorkspaces != null ? relatedWorkspaces : new java.util.ArrayList<>();
        this.targetBranch = targetBranch;
        this.relatedIssues = relatedIssues;
        this.constraints = constraints;
        this.priority = priority;
        this.affectedProjects = affectedProjects != null ? affectedProjects : new java.util.ArrayList<>();
        this.labels = labels != null ? labels : new java.util.ArrayList<>();
        this.engineMode = engineMode != null ? engineMode : "HERMES_SINGLE";
        this.assignedRoles = assignedRoles != null ? assignedRoles : new java.util.ArrayList<>();
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
    public java.util.List<String> getRelatedWorkspaces() { return relatedWorkspaces; }
    public String getTargetBranch() { return targetBranch; }
    public String getRelatedIssues() { return relatedIssues; }
    public String getConstraints() { return constraints; }
    public String getPriority() { return priority; }
    public java.util.List<String> getAffectedProjects() { return affectedProjects; }
    public java.util.List<String> getLabels() { return labels; }
    public String getEngineMode() { return engineMode; }
    public java.util.List<String> getAssignedRoles() { return assignedRoles; }

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

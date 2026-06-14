package com.dark.aiagent.module.aidev.interfaces.rest;

import java.time.OffsetDateTime;

public record AiDevTaskResponse(
        String id,
        String title,
        String description,
        String status,
        String branchName,
        Double totalCost,
        OffsetDateTime createTime,
        OffsetDateTime updateTime,
        Integer maxBrainstormingRounds,
        Integer contextSlidingWindow,
        String targetBranch,
        String relatedIssues,
        String constraints,
        String priority,
        java.util.List<String> affectedProjects,
        java.util.List<String> labels,
        String engineMode,
        java.util.List<String> assignedRoles
) {
}

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
        OffsetDateTime updateTime
) {
}

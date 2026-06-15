package com.dark.aiagent.module.aidev.interfaces.rest;

import java.util.List;

public record AiDevTokenSummaryResponse(
        Integer totalPromptTokens,
        Integer totalCompletionTokens,
        Double totalCost,
        Integer totalDurationMs,
        List<PhaseMetric> phases
) {
    public record PhaseMetric(
            String phase,
            String agentRole,
            Integer promptTokens,
            Integer completionTokens,
            Double cost,
            Integer durationMs,
            Integer callCount
    ) {}
}

package com.dark.aiagent.module.aidev.domain.repository;

import com.dark.aiagent.module.aidev.domain.entity.AiDevAuditLog;
import com.dark.aiagent.module.aidev.interfaces.rest.AiDevTokenSummaryResponse.PhaseMetric;
import java.util.List;

public interface AiDevAuditLogRepository {
    void save(AiDevAuditLog log);
    List<PhaseMetric> getAggregatedMetricsByTask(String taskId);
}

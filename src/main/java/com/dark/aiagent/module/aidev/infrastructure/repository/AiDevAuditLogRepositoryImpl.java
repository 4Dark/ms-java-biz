package com.dark.aiagent.module.aidev.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dark.aiagent.module.aidev.domain.entity.AiDevAuditLog;
import com.dark.aiagent.module.aidev.domain.repository.AiDevAuditLogRepository;
import com.dark.aiagent.module.aidev.infrastructure.dataobject.AiDevAuditLogPO;
import com.dark.aiagent.module.aidev.infrastructure.mapper.AiDevAuditLogMapper;
import com.dark.aiagent.module.aidev.interfaces.rest.AiDevTokenSummaryResponse.PhaseMetric;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class AiDevAuditLogRepositoryImpl implements AiDevAuditLogRepository {

    private final AiDevAuditLogMapper mapper;

    public AiDevAuditLogRepositoryImpl(AiDevAuditLogMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void save(AiDevAuditLog domain) {
        AiDevAuditLogPO po = toPO(domain);
        mapper.insert(po);
    }

    @Override
    public List<PhaseMetric> getAggregatedMetricsByTask(String taskId) {
        QueryWrapper<AiDevAuditLogPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(
                "action_type",
                "agent_role",
                "sum(prompt_tokens) as prompt_tokens",
                "sum(comp_tokens) as comp_tokens",
                "sum(cost) as cost",
                "sum(duration_ms) as duration_ms",
                "count(1) as call_count"
        );
        queryWrapper.eq("task_id", taskId);
        queryWrapper.groupBy("action_type", "agent_role");

        List<Map<String, Object>> maps = mapper.selectMaps(queryWrapper);

        return maps.stream().map(map -> {
            String phase = (String) map.get("action_type");
            String agentRole = (String) map.get("agent_role");
            Integer promptTokens = getInteger(map.get("prompt_tokens"));
            Integer compTokens = getInteger(map.get("comp_tokens"));
            Double cost = getDouble(map.get("cost"));
            Integer durationMs = getInteger(map.get("duration_ms"));
            Integer callCount = getInteger(map.get("call_count"));

            return new PhaseMetric(phase, agentRole, promptTokens, compTokens, cost, durationMs, callCount);
        }).collect(Collectors.toList());
    }

    private Integer getInteger(Object val) {
        if (val == null) return 0;
        if (val instanceof Number) return ((Number) val).intValue();
        return Integer.parseInt(val.toString());
    }

    private Double getDouble(Object val) {
        if (val == null) return 0.0;
        if (val instanceof BigDecimal) return ((BigDecimal) val).doubleValue();
        if (val instanceof Number) return ((Number) val).doubleValue();
        return Double.parseDouble(val.toString());
    }

    private AiDevAuditLogPO toPO(AiDevAuditLog domain) {
        AiDevAuditLogPO po = new AiDevAuditLogPO();
        po.setId(domain.getId());
        po.setTaskId(domain.getTaskId());
        po.setAgentRole(domain.getAgentRole());
        po.setProviderModel(domain.getProviderModel());
        po.setActionType(domain.getActionType());
        po.setPromptTokens(domain.getPromptTokens());
        po.setCompTokens(domain.getCompTokens());
        po.setCost(domain.getCost());
        po.setDurationMs(domain.getDurationMs());
        po.setCreateTime(domain.getCreateTime());
        return po;
    }
}

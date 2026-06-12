package com.dark.aiagent.module.aidev.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dark.aiagent.module.aidev.domain.entity.AiDevTask;
import com.dark.aiagent.module.aidev.domain.repository.AiDevTaskRepository;
import com.dark.aiagent.module.aidev.infrastructure.dataobject.AiDevTaskPO;
import com.dark.aiagent.module.aidev.infrastructure.mapper.AiDevTaskMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class AiDevTaskRepositoryImpl implements AiDevTaskRepository {

    private final AiDevTaskMapper mapper;

    public AiDevTaskRepositoryImpl(AiDevTaskMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<AiDevTask> findAll() {
        QueryWrapper<AiDevTaskPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        return mapper.selectList(queryWrapper).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<AiDevTask> findById(String id) {
        AiDevTaskPO po = mapper.selectById(id);
        if (po == null) {
            return Optional.empty();
        }
        return Optional.of(toDomain(po));
    }

    @Override
    public void save(AiDevTask task) {
        AiDevTaskPO po = toPO(task);
        if (mapper.selectById(task.getId()) != null) {
            mapper.updateById(po);
        } else {
            mapper.insert(po);
        }
    }

    @Override
    public void deleteById(String id) {
        mapper.deleteById(id);
    }

    private AiDevTask toDomain(AiDevTaskPO po) {
        return new AiDevTask(
                po.getId(),
                po.getTitle(),
                po.getDescription(),
                po.getStatus(),
                po.getBranchName(),
                po.getTotalCost(),
                po.getHumanFeedback(),
                po.getCreateTime(),
                po.getUpdateTime(),
                po.getMaxBrainstormingRounds(),
                po.getContextSlidingWindow(),
                po.getRelatedWorkspaces(),
                po.getTargetBranch(),
                po.getRelatedIssues(),
                po.getConstraints(),
                po.getPriority(),
                po.getAffectedProjects(),
                po.getLabels(),
                po.getEngineMode()
        );
    }

    private AiDevTaskPO toPO(AiDevTask domain) {
        AiDevTaskPO po = new AiDevTaskPO();
        po.setId(domain.getId());
        po.setTitle(domain.getTitle());
        po.setDescription(domain.getDescription());
        po.setStatus(domain.getStatus());
        po.setBranchName(domain.getBranchName());
        po.setTotalCost(domain.getTotalCost());
        po.setHumanFeedback(domain.getHumanFeedback());
        po.setCreateTime(domain.getCreateTime());
        po.setUpdateTime(domain.getUpdateTime());
        po.setMaxBrainstormingRounds(domain.getMaxBrainstormingRounds());
        po.setContextSlidingWindow(domain.getContextSlidingWindow());
        po.setRelatedWorkspaces(domain.getRelatedWorkspaces());
        po.setTargetBranch(domain.getTargetBranch());
        po.setRelatedIssues(domain.getRelatedIssues());
        po.setConstraints(domain.getConstraints());
        po.setPriority(domain.getPriority());
        po.setAffectedProjects(domain.getAffectedProjects());
        po.setLabels(domain.getLabels());
        po.setEngineMode(domain.getEngineMode());
        return po;
    }
}

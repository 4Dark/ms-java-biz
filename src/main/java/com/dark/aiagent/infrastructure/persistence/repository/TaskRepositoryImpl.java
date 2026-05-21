package com.dark.aiagent.infrastructure.persistence.repository;

import com.dark.aiagent.domain.task.entity.TaskRecord;
import com.dark.aiagent.domain.task.repository.TaskRepository;
import com.dark.aiagent.infrastructure.persistence.entity.TaskRecordDO;
import com.dark.aiagent.infrastructure.persistence.mapper.TaskRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TaskRepositoryImpl implements TaskRepository {

    private final TaskRecordMapper mapper;

    @Override
    public void save(TaskRecord taskRecord) {
        TaskRecordDO doObject = toDO(taskRecord);
        mapper.insert(doObject);
    }

    @Override
    public Optional<TaskRecord> findById(String id) {
        TaskRecordDO doObject = mapper.selectById(id);
        return Optional.ofNullable(toDomain(doObject));
    }

    @Override
    public void update(TaskRecord taskRecord) {
        TaskRecordDO doObject = toDO(taskRecord);
        mapper.updateById(doObject);
    }

    private TaskRecordDO toDO(TaskRecord domain) {
        if (domain == null) return null;
        TaskRecordDO doObject = new TaskRecordDO();
        doObject.setId(domain.getId());
        doObject.setTaskType(domain.getTaskType());
        doObject.setStatus(domain.getStatus());
        doObject.setTotalCount(domain.getTotalCount());
        doObject.setProcessedCount(domain.getProcessedCount());
        doObject.setCurrentItemName(domain.getCurrentItemName());
        doObject.setErrorMessage(domain.getErrorMessage());
        
        if (domain.getCreateTime() != null) {
            doObject.setCreateTime(OffsetDateTime.ofInstant(domain.getCreateTime().toInstant(), ZoneId.systemDefault()));
        }
        if (domain.getUpdateTime() != null) {
            doObject.setUpdateTime(OffsetDateTime.ofInstant(domain.getUpdateTime().toInstant(), ZoneId.systemDefault()));
        }
        return doObject;
    }

    private TaskRecord toDomain(TaskRecordDO doObject) {
        if (doObject == null) return null;
        Date createTime = doObject.getCreateTime() != null ? Date.from(doObject.getCreateTime().toInstant()) : null;
        Date updateTime = doObject.getUpdateTime() != null ? Date.from(doObject.getUpdateTime().toInstant()) : null;
        
        return new TaskRecord(
            doObject.getId(),
            doObject.getTaskType(),
            doObject.getStatus(),
            doObject.getTotalCount(),
            doObject.getProcessedCount(),
            doObject.getCurrentItemName(),
            doObject.getErrorMessage(),
            createTime,
            updateTime
        );
    }
}

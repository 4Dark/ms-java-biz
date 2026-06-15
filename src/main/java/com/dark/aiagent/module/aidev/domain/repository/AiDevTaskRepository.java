package com.dark.aiagent.module.aidev.domain.repository;

import com.dark.aiagent.module.aidev.domain.entity.AiDevTask;
import java.util.List;
import java.util.Optional;

public interface AiDevTaskRepository {
    List<AiDevTask> findAll();
    Optional<AiDevTask> findById(String id);
    void save(AiDevTask task);
    void deleteById(String id);
}

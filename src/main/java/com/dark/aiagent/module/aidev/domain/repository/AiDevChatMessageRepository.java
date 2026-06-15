package com.dark.aiagent.module.aidev.domain.repository;

import com.dark.aiagent.module.aidev.domain.entity.AiDevChatMessage;

import java.util.List;

public interface AiDevChatMessageRepository {
    List<AiDevChatMessage> findByTaskId(String taskId);
    void save(AiDevChatMessage message);
    void deleteByTaskId(String taskId);
}

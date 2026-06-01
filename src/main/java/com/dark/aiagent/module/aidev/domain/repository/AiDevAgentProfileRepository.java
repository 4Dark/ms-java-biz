package com.dark.aiagent.module.aidev.domain.repository;

import com.dark.aiagent.module.aidev.domain.entity.AiDevAgentProfile;

import java.util.List;
import java.util.Optional;

public interface AiDevAgentProfileRepository {
    List<AiDevAgentProfile> findAll();
    Optional<AiDevAgentProfile> findByRoleName(String roleName);
    void save(AiDevAgentProfile profile);
}

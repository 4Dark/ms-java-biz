package com.dark.aiagent.module.aidev.application;

import com.dark.aiagent.module.aidev.domain.entity.AiDevAgentProfile;
import com.dark.aiagent.module.aidev.domain.repository.AiDevAgentProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AiDevAgentProfileUseCase {

    private final AiDevAgentProfileRepository repository;

    public AiDevAgentProfileUseCase(AiDevAgentProfileRepository repository) {
        this.repository = repository;
    }

    public List<AiDevAgentProfile> getAllProfiles() {
        return repository.findAll();
    }

    @Transactional
    public AiDevAgentProfile updateProfile(String roleName, String baseUrl, String apiToken, String modelName, String avatar, String systemPrompt) {
        Optional<AiDevAgentProfile> optProfile = repository.findByRoleName(roleName);
        if (optProfile.isEmpty()) {
            throw new IllegalArgumentException("Profile not found for role: " + roleName);
        }
        
        AiDevAgentProfile profile = optProfile.get();
        profile.updateProfile(baseUrl, apiToken, modelName, avatar, systemPrompt);
        repository.save(profile);
        return profile;
    }
}

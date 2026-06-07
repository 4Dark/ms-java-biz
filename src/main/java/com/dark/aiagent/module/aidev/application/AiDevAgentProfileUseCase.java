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

    @org.springframework.beans.factory.annotation.Value("${ai-dev.integration.mode:ADAPTER}")
    private String integrationMode;

    @org.springframework.beans.factory.annotation.Value("${ai-dev.integration.native.kanban-db-path:${user.home}/.hermes/kanban.db}")
    private String kanbanDbPath;

    public AiDevAgentProfileUseCase(AiDevAgentProfileRepository repository) {
        this.repository = repository;
    }

    public List<AiDevAgentProfile> getAllProfiles() {
        return repository.findAll().stream()
                .map(this::syncProfileFromLocal)
                .collect(java.util.stream.Collectors.toList());
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

        // NATIVE 轨道下，同步写入本地 yaml 配置文件 和 SOUL.md
        syncProfileToLocal(roleName, baseUrl, apiToken, modelName, systemPrompt);

        return profile;
    }

    private String getProfileDirName(String roleName) {
        if (roleName == null) return null;
        switch (roleName.toUpperCase()) {
            case "PLANNER":
                return "planner";
            case "GENERATOR":
                return "generator";
            case "EVALUATOR":
                return "evaluator";
            case "ORCHESTRATOR":
                return "pm";
            default:
                return null;
        }
    }

    @SuppressWarnings("unchecked")
    private AiDevAgentProfile syncProfileFromLocal(AiDevAgentProfile profile) {
        if (!"NATIVE".equalsIgnoreCase(integrationMode)) {
            return profile;
        }

        try {
            java.io.File hermesDir = new java.io.File(kanbanDbPath).getParentFile();
            if (hermesDir == null || !hermesDir.exists()) {
                return profile;
            }
            String profileDirName = getProfileDirName(profile.getRoleName());
            if (profileDirName == null) return profile;

            // 优先读取 profile 目录下的 config.yaml，如果没有则读取全局 config.yaml
            java.io.File configFile = new java.io.File(new java.io.File(new java.io.File(hermesDir, "profiles"), profileDirName), "config.yaml");
            if (!configFile.exists()) {
                configFile = new java.io.File(hermesDir, "config.yaml");
            }
            if (!configFile.exists()) {
                return profile;
            }

            org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml();
            java.util.Map<String, Object> configMap;
            try (java.io.InputStream in = new java.io.FileInputStream(configFile)) {
                configMap = yaml.load(in);
            }
            if (configMap == null) return profile;

            java.util.Map<String, Object> modelMap = (java.util.Map<String, Object>) configMap.get("model");
            if (modelMap == null) return profile;

            String modelName = (String) modelMap.get("default");
            String baseUrl = (String) modelMap.get("base_url");
            String provider = (String) modelMap.get("provider");

            String apiToken = "";
            java.util.Map<String, Object> providersMap = (java.util.Map<String, Object>) configMap.get("providers");
            if (providersMap != null && provider != null) {
                java.util.Map<String, Object> specificProviderMap = (java.util.Map<String, Object>) providersMap.get(provider);
                if (specificProviderMap != null) {
                    apiToken = (String) specificProviderMap.get("api_key");
                }
            }

            // 在 NATIVE 模式下，本地配置文件具有最高优先级，直接覆盖数据库的值以保持同步
            String finalBaseUrl = (baseUrl != null) ? baseUrl : (profile.getBaseUrl() != null ? profile.getBaseUrl() : "");
            String finalApiToken = (apiToken != null) ? apiToken : (profile.getApiToken() != null ? profile.getApiToken() : "");
            String finalModelName = (modelName != null && !modelName.isBlank()) ? modelName : profile.getModelName();

            // 读取同目录下的 SOUL.md 作为 systemPrompt
            String soulContent = readSoulMd(configFile.getParentFile());
            String finalSystemPrompt = (soulContent != null && !soulContent.isBlank()) ? soulContent : profile.getSystemPrompt();

            profile.updateProfile(finalBaseUrl, finalApiToken, finalModelName, profile.getAvatar(), finalSystemPrompt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return profile;
    }

    /** 读取 profile 目录下的 SOUL.md 内容，不存在则返回 null */
    private String readSoulMd(java.io.File profileDir) {
        if (profileDir == null) return null;
        java.io.File soulFile = new java.io.File(profileDir, "SOUL.md");
        if (!soulFile.exists()) return null;
        try {
            return new String(java.nio.file.Files.readAllBytes(soulFile.toPath()), java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private void syncProfileToLocal(String roleName, String baseUrl, String apiToken, String modelName, String systemPrompt) {
        if (!"NATIVE".equalsIgnoreCase(integrationMode)) {
            return;
        }
        try {
            java.io.File hermesDir = new java.io.File(kanbanDbPath).getParentFile();
            if (hermesDir == null || !hermesDir.exists()) {
                return;
            }
            String profileDirName = getProfileDirName(roleName);
            if (profileDirName == null) return;

            java.io.File profileDir = new java.io.File(new java.io.File(hermesDir, "profiles"), profileDirName);
            if (!profileDir.exists()) {
                profileDir.mkdirs();
            }
            java.io.File configFile = new java.io.File(profileDir, "config.yaml");

            org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml();
            java.util.Map<String, Object> configMap = null;
            if (configFile.exists()) {
                try (java.io.InputStream in = new java.io.FileInputStream(configFile)) {
                    configMap = yaml.load(in);
                } catch (Exception e) {
                    configMap = new java.util.HashMap<>();
                }
            }
            if (configMap == null) {
                configMap = new java.util.HashMap<>();
            }

            java.util.Map<String, Object> modelMap = (java.util.Map<String, Object>) configMap.get("model");
            if (modelMap == null) {
                modelMap = new java.util.HashMap<>();
                configMap.put("model", modelMap);
            }
            modelMap.put("default", modelName);
            modelMap.put("base_url", baseUrl != null ? baseUrl : "");

            String provider = "openai";
            if (baseUrl != null) {
                String lower = baseUrl.toLowerCase();
                if (lower.contains("googleapis") || lower.contains("google")) {
                    provider = "google";
                } else if (lower.contains("anthropic")) {
                    provider = "anthropic";
                }
            }
            modelMap.put("provider", provider);

            java.util.Map<String, Object> providersMap = (java.util.Map<String, Object>) configMap.get("providers");
            if (providersMap == null) {
                providersMap = new java.util.HashMap<>();
                configMap.put("providers", providersMap);
            }
            java.util.Map<String, Object> specificProviderMap = (java.util.Map<String, Object>) providersMap.get(provider);
            if (specificProviderMap == null) {
                specificProviderMap = new java.util.HashMap<>();
                providersMap.put(provider, specificProviderMap);
            }
            specificProviderMap.put("api_key", apiToken != null ? apiToken : "");

            try (java.io.FileWriter writer = new java.io.FileWriter(configFile)) {
                yaml.dump(configMap, writer);
            }

            // 将 systemPrompt 同步写入 SOUL.md
            if (systemPrompt != null && !systemPrompt.isBlank()) {
                java.io.File soulFile = new java.io.File(profileDir, "SOUL.md");
                try (java.io.FileWriter soulWriter = new java.io.FileWriter(soulFile)) {
                    soulWriter.write(systemPrompt);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

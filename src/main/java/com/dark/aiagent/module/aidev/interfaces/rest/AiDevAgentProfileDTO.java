package com.dark.aiagent.module.aidev.interfaces.rest;

public record AiDevAgentProfileDTO(
    String id,
    String roleName,
    String baseUrl,
    String apiToken,
    String modelName,
    String avatar,
    String systemPrompt
) {}

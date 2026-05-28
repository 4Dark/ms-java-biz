package com.dark.aiagent.ephemeral.interfaces.rest.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * 创建阅后即焚房间请求
 *
 * @param title       可选公开名称（不含敏感内容）
 * @param ttlSeconds  存活秒数（60 ~ 604800，即 1分钟 ~ 7天）
 * @param createdBy   创建者匿名 ID（客户端生成 UUID）
 */
public record CreateRoomRequest(
        String title,
        @Min(60) @Max(604800) long ttlSeconds,
        @NotBlank String createdBy
) {}

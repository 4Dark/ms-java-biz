package com.dark.aiagent.ephemeral.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 加入房间请求
 *
 * @param participantId  客户端生成的匿名 UUID
 * @param nicknameCipher 加密昵称（可为 null）
 */
public record JoinRoomRequest(
        @NotBlank String participantId,
        String nicknameCipher
) {}

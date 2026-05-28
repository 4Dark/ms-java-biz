package com.dark.aiagent.ephemeral.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 发送加密消息请求（客户端加密后提交，服务器不解密）
 *
 * @param senderId   发送者匿名 ID
 * @param cipherText AES-GCM 密文（Base64）
 * @param iv         随机初始向量（Base64，每条消息独立）
 */
public record SendMessageRequest(
        @NotBlank String senderId,
        @NotBlank String cipherText,
        @NotBlank String iv
) {}

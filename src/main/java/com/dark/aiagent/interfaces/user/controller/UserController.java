package com.dark.aiagent.interfaces.user.controller;

import com.dark.aiagent.application.user.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Tag(name = "用户微服务接口")
@RestController
@RequestMapping("/rest/biz/v1/user")
public class UserController {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Operation(summary = "获取当前登录人信息")
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(
            @RequestHeader(value = "X-User-Id", defaultValue = "") String headerUserId) {
        
        String userId = headerUserId;
        String username = "";
        String avatar = "";

        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getCredentials() instanceof String token) {
            try {
                javax.crypto.SecretKey key = io.jsonwebtoken.security.Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
                io.jsonwebtoken.Claims claims = io.jsonwebtoken.Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
                
                if (claims.getSubject() != null) {
                    userId = claims.getSubject();
                }
                log.info("JWT Claims from Casdoor: {}", claims);
                username = claims.get("displayName", String.class);
                if (username == null || username.isEmpty()) {
                    username = claims.get("name", String.class);
                }
                avatar = claims.get("picture", String.class);
                if (avatar == null || avatar.isEmpty()) {
                    avatar = claims.get("avatar", String.class);
                }
            } catch (Exception e) {
                log.warn("Failed to parse JWT in UserController: {}", e.getMessage());
            }
        }

        UserDto user = UserDto.builder()
                .id(userId)
                .name(username)
                .avatar(avatar)
                .build();

        return ResponseEntity.ok(user);
    }
}

package com.dark.aiagent.interfaces.chat.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dark.aiagent.domain.chat.entity.ChatMessage;
import com.dark.aiagent.application.chat.dto.ChatSessionDto;
import com.dark.aiagent.application.chat.service.ChatApplicationService;

import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Chat History Console")
@RestController
@RequestMapping("/rest/biz/v1/history")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatHistoryController {

    private final ChatApplicationService chatApplicationService;

    @Operation(summary = "Get chat history by session ID")
    @GetMapping
    public ResponseEntity<List<ChatMessage>> getHistory(@RequestParam String sessionId) {
        return ResponseEntity.ok(chatApplicationService.getChatHistory(sessionId));
    }

    @Operation(summary = "Get all chat sessions")
    @GetMapping("/sessions")
    public ResponseEntity<List<ChatSessionDto>> getSessions() {
        return ResponseEntity.ok(chatApplicationService.getAllSessions());
    }

    @Operation(summary = "Delete a chat session")
    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Void> deleteSession(@PathVariable String sessionId) {
        chatApplicationService.deleteSession(sessionId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Rate a chat message")
    @PostMapping("/{messageId}/rating")
    public ResponseEntity<Void> rateMessage(@PathVariable Long messageId, @RequestBody Map<String, String> body) {
        String rating = body.get("rating");
        chatApplicationService.rateMessage(messageId, rating);
        return ResponseEntity.ok().build();
    }
}

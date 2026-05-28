package com.dark.aiagent.ephemeral.interfaces.rest;

import com.dark.aiagent.ephemeral.application.EphemeralRoomUseCase;
import com.dark.aiagent.ephemeral.domain.EphemeralMessage;
import com.dark.aiagent.ephemeral.domain.EphemeralRoom;
import com.dark.aiagent.ephemeral.interfaces.rest.dto.CreateRoomRequest;
import com.dark.aiagent.ephemeral.interfaces.rest.dto.JoinRoomRequest;
import com.dark.aiagent.ephemeral.interfaces.rest.dto.RoomInfoResponse;
import com.dark.aiagent.ephemeral.interfaces.rest.dto.SendMessageRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 阅后即焚通讯空间 REST 接口
 *
 * <p>所有接口不需要登录态（匿名访问），需在 Security 白名单中开放。
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/rest/biz/v1/ephemeral/rooms")
public class EphemeralRoomController {

    private final EphemeralRoomUseCase useCase;

    /**
     * 创建通讯空间。
     *
     * @param request 包含 title、ttlSeconds、createdBy 的请求体
     * @return roomId 和 shortCode（用于生成短链）
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> createRoom(@Valid @RequestBody CreateRoomRequest request) {
        EphemeralRoom room = useCase.createRoom(request.title(), request.ttlSeconds(), request.createdBy());
        return ResponseEntity.ok(Map.of(
                "roomId", room.getId(),
                "shortCode", room.getShortCode()
        ));
    }

    /**
     * 查询房间元信息（不含任何消息内容）。
     *
     * @param code Base62 短码
     * @return 房间元信息；过期/销毁返回 410
     */
    @GetMapping("/{code}")
    public ResponseEntity<RoomInfoResponse> getRoomInfo(@PathVariable String code) {
        return useCase.findRoom(code)
                .map(room -> {
                    long count = useCase.countParticipants(room.getId());
                    return ResponseEntity.ok(RoomInfoResponse.of(room, count));
                })
                .orElse(ResponseEntity.status(410).build());
    }

    /**
     * 参与者加入房间。
     *
     * @param roomId  房间 ID
     * @param request 包含 participantId（客户端匿名UUID）
     */
    @PostMapping("/{roomId}/join")
    public ResponseEntity<Void> joinRoom(@PathVariable String roomId,
                                         @Valid @RequestBody JoinRoomRequest request) {
        useCase.joinRoom(roomId, request.participantId(), request.nicknameCipher());
        return ResponseEntity.ok().build();
    }

    /**
     * 发送加密消息。
     *
     * @param roomId  房间 ID
     * @param request 包含 senderId, cipherText, iv
     * @return 持久化后的消息
     */
    @PostMapping("/{roomId}/messages")
    public ResponseEntity<EphemeralMessage> sendMessage(@PathVariable String roomId,
                                                        @Valid @RequestBody SendMessageRequest request) {
        EphemeralMessage message = useCase.sendMessage(roomId, request.senderId(), request.cipherText(), request.iv());
        return ResponseEntity.ok(message);
    }

    /**
     * WebSocket 降级轮询：拉取新消息。
     *
     * @param roomId  房间 ID
     * @param afterId 上次拉取的最后消息 ID（默认 0）
     * @return 消息列表（只含密文和IV，客户端自行解密）
     */
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<EphemeralMessage>> pollMessages(
            @PathVariable String roomId,
            @RequestParam(defaultValue = "0") long afterId) {
        return ResponseEntity.ok(useCase.pollMessages(roomId, afterId));
    }

    /**
     * 退出并删除本人在房间内的所有消息（软删除）。
     *
     * @param roomId        房间 ID
     * @param participantId 发送者匿名 ID（通过查询参数传入，保持 DELETE 语义）
     */
    @DeleteMapping("/{roomId}/me")
    public ResponseEntity<Void> leaveAndDelete(@PathVariable String roomId,
                                               @RequestParam String participantId) {
        useCase.leaveAndDeleteMessages(roomId, participantId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 销毁整个房间（广播 DESTROY 信号后物理删除所有数据）。
     *
     * @param roomId 房间 ID
     */
    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> destroyRoom(@PathVariable String roomId) {
        useCase.destroyRoom(roomId);
        return ResponseEntity.noContent().build();
    }
}

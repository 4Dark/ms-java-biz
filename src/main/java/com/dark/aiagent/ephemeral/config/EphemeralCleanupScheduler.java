package com.dark.aiagent.ephemeral.config;

import com.dark.aiagent.ephemeral.infrastructure.EphemeralRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 阅后即焚 TTL 兜底清理调度器
 *
 * <p>每 5 分钟扫描过期房间，执行物理删除。
 * 这是 Redis Keyspace 事件的兜底方案，确保即使 Redis 事件丢失，数据也会被最终清理。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EphemeralCleanupScheduler {

    private final EphemeralRoomRepository repository;

    /**
     * 定时清理过期房间及其所有关联数据（消息、参与者）。
     * 数据库 ON DELETE CASCADE 保证级联删除。
     */
    @Scheduled(cron = "${ephemeral.cleanup-cron:0 */5 * * * *}")
    public void cleanupExpiredRooms() {
        List<String> expiredIds = repository.findExpiredRooms()
                .stream()
                .map(room -> room.getId())
                .toList();

        if (expiredIds.isEmpty()) return;

        log.info("【EphemeralCleanup】定时清理，发现 {} 个过期房间", expiredIds.size());
        expiredIds.forEach(id -> {
            repository.deleteRoomById(id);
            log.debug("【EphemeralCleanup】已物理删除过期房间 roomId={}", id);
        });
        log.info("【EphemeralCleanup】清理完成，共删除 {} 个房间", expiredIds.size());
    }
}

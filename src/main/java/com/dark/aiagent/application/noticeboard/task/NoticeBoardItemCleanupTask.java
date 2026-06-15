package com.dark.aiagent.application.noticeboard.task;

import com.dark.aiagent.domain.noticeboard.repository.NoticeBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class NoticeBoardItemCleanupTask {

    private final NoticeBoardRepository noticeBoardRepository;

    /**
     * Runs every hour to clean up expired notice board items.
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional(rollbackFor = Exception.class)
    public void cleanupExpiredItems() {
        log.info("Starting cleanup of expired notice board items...");
        try {
            noticeBoardRepository.deleteExpiredNoticeBoardItems();
            log.info("Cleanup of expired notice board items completed.");
        } catch (Exception e) {
            log.error("Failed to cleanup expired notice board items", e);
        }
    }
}

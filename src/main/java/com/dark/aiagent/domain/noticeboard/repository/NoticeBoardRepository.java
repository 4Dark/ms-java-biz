package com.dark.aiagent.domain.noticeboard.repository;

import com.dark.aiagent.domain.noticeboard.entity.Announcement;
import com.dark.aiagent.domain.noticeboard.entity.NoticeBoardItem;

import java.util.List;
import java.util.Optional;
import com.dark.aiagent.domain.common.PageResult;

public interface NoticeBoardRepository {
    // Notice Board Items
    NoticeBoardItem saveNoticeBoardItem(NoticeBoardItem item);
    Optional<NoticeBoardItem> findNoticeBoardItemById(Long id);
    List<NoticeBoardItem> findAllValidNoticeBoardItems();
    void deleteExpiredNoticeBoardItems();
    void updateLastViewedTime(Long id);
    void deleteNoticeBoardItem(Long id);

    // Announcements
    Announcement saveAnnouncement(Announcement announcement);
    List<Announcement> findAllAnnouncements(String keyword);
    PageResult<Announcement> findAnnouncementsPaged(int page, int size, String keyword);
    Optional<Announcement> findAnnouncementById(Long id);
    void deleteAnnouncement(Long id);
}

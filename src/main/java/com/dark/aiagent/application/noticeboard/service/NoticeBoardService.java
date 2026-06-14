package com.dark.aiagent.application.noticeboard.service;

import com.dark.aiagent.domain.noticeboard.entity.Announcement;
import com.dark.aiagent.domain.noticeboard.entity.NoticeBoardItem;

import java.util.List;
import com.dark.aiagent.domain.common.PageResult;

public interface NoticeBoardService {
    List<NoticeBoardItem> getValidNoticeBoardItems();
    NoticeBoardItem addNoticeBoardItem(NoticeBoardItem item);
    void trackItemView(Long id);
    void deleteNoticeBoardItem(Long id);

    List<Announcement> getAnnouncements();
    PageResult<Announcement> getAnnouncementsPaged(int page, int size);
    Announcement addAnnouncement(Announcement announcement);
    Announcement updateAnnouncement(Long id, Announcement announcement);
    Announcement getAnnouncementById(Long id, String extractionCode);
    void deleteAnnouncement(Long id);
}

package com.dark.aiagent.application.noticeboard.service.impl;

import com.dark.aiagent.application.noticeboard.service.NoticeBoardService;
import com.dark.aiagent.domain.noticeboard.entity.Announcement;
import com.dark.aiagent.domain.noticeboard.entity.NoticeBoardItem;
import com.dark.aiagent.domain.noticeboard.repository.NoticeBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import com.dark.aiagent.domain.common.PageResult;

@Service
@RequiredArgsConstructor
public class NoticeBoardServiceImpl implements NoticeBoardService {

    private final NoticeBoardRepository noticeBoardRepository;

    @Override
    public List<NoticeBoardItem> getValidNoticeBoardItems() {
        return noticeBoardRepository.findAllValidNoticeBoardItems();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public NoticeBoardItem addNoticeBoardItem(NoticeBoardItem item) {
        item.setCreateTime(OffsetDateTime.now());
        item.setUpdateTime(OffsetDateTime.now());
        return noticeBoardRepository.saveNoticeBoardItem(item);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void trackItemView(Long id) {
        noticeBoardRepository.updateLastViewedTime(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNoticeBoardItem(Long id) {
        noticeBoardRepository.deleteNoticeBoardItem(id);
    }

    @Override
    public List<Announcement> getAnnouncements() {
        return noticeBoardRepository.findAllAnnouncements().stream()
                .map(this::processDynamicStatus)
                .toList();
    }

    @Override
    public PageResult<Announcement> getAnnouncementsPaged(int page, int size) {
        PageResult<Announcement> pageResult = noticeBoardRepository.findAnnouncementsPaged(page, size);
        List<Announcement> processedRecords = pageResult.records().stream()
                .map(this::processDynamicStatus)
                .collect(Collectors.toList());
        return new PageResult<>(processedRecords, pageResult.total(), pageResult.size(), pageResult.current(), pageResult.pages());
    }

    private Announcement processDynamicStatus(Announcement announcement) {
        if (announcement.getStatus() == com.dark.aiagent.domain.noticeboard.enums.AnnouncementStatus.PUBLISHED 
            && announcement.getExpireTime() != null 
            && announcement.getExpireTime().isBefore(OffsetDateTime.now())) {
            announcement.setStatus(com.dark.aiagent.domain.noticeboard.enums.AnnouncementStatus.EXPIRED);
        }
        return announcement;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Announcement addAnnouncement(Announcement announcement) {
        announcement.setCreateTime(OffsetDateTime.now());
        announcement.setUpdateTime(OffsetDateTime.now());
        return noticeBoardRepository.saveAnnouncement(announcement);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Announcement updateAnnouncement(Long id, Announcement announcement) {
        Announcement existing = noticeBoardRepository.findAnnouncementById(id)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Announcement not found"));
        existing.setTitle(announcement.getTitle());
        existing.setContent(announcement.getContent());
        existing.setStatus(announcement.getStatus());
        existing.setExpireTime(announcement.getExpireTime());
        if (announcement.getExtractionCode() != null && !announcement.getExtractionCode().isBlank()) {
            existing.setExtractionCode(announcement.getExtractionCode());
        }
        existing.setUpdateTime(OffsetDateTime.now());
        return noticeBoardRepository.saveAnnouncement(existing);
    }

    @Override
    public Announcement getAnnouncementById(Long id, String extractionCode) {
        Announcement existing = noticeBoardRepository.findAnnouncementById(id)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Announcement not found"));
        
        if (existing.getExtractionCode() != null && !existing.getExtractionCode().equals(extractionCode)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "Invalid extraction code");
        }
        
        return processDynamicStatus(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAnnouncement(Long id) {
        noticeBoardRepository.deleteAnnouncement(id);
    }
}

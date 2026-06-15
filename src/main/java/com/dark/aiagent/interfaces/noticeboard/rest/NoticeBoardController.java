package com.dark.aiagent.interfaces.noticeboard.rest;

import com.dark.aiagent.application.noticeboard.service.NoticeBoardService;
import com.dark.aiagent.domain.noticeboard.entity.Announcement;
import com.dark.aiagent.domain.noticeboard.entity.NoticeBoardItem;
import com.dark.aiagent.interfaces.noticeboard.dto.NoticeBoardDto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import com.dark.aiagent.domain.common.PageResult;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest/biz/v1")
@RequiredArgsConstructor
public class NoticeBoardController {

    private final NoticeBoardService noticeBoardService;

    // --- Announcements ---

    @GetMapping("/announcements")
    public ResponseEntity<?> getAnnouncements(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "keyword", required = false) String keyword) {
        if (page == null && size == null) {
            List<AnnouncementResponse> list = noticeBoardService.getAnnouncements(keyword).stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(list);
        }
        int pageNum = page != null ? page : 1;
        int pageSize = size != null ? size : 10;
        PageResult<Announcement> pageResult = noticeBoardService.getAnnouncementsPaged(pageNum, pageSize, keyword);
        List<AnnouncementResponse> responses = pageResult.records().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new PageResult<>(responses, pageResult.total(), pageResult.size(), pageResult.current(), pageResult.pages()));
    }

    @PostMapping("/announcements")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('admin') or hasRole('admin') or hasAuthority('ROLE_admin')")
    public AnnouncementResponse createAnnouncement(@RequestBody AnnouncementRequest request) {
        Announcement announcement = Announcement.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .status(request.getStatus() != null ? request.getStatus() : com.dark.aiagent.domain.noticeboard.enums.AnnouncementStatus.DRAFT)
                .expireTime(request.getExpireTime())
                .extractionCode(request.getExtractionCode())
                .build();
        Announcement saved = noticeBoardService.addAnnouncement(announcement);
        return toResponse(saved);
    }

    @PutMapping("/announcements/{id}")
    @PreAuthorize("isAuthenticated()")
    public AnnouncementResponse updateAnnouncement(@PathVariable Long id, @RequestBody AnnouncementRequest request) {
        Announcement announcement = Announcement.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .status(request.getStatus())
                .expireTime(request.getExpireTime())
                .extractionCode(request.getExtractionCode())
                .build();
        Announcement updated = noticeBoardService.updateAnnouncement(id, announcement);
        return toResponse(updated);
    }

    @GetMapping("/announcements/{id}")
    public AnnouncementResponse getAnnouncementById(@PathVariable Long id, @RequestParam(name = "code", required = false) String extractionCode) {
        Announcement announcement = noticeBoardService.getAnnouncementById(id, extractionCode);
        return toResponse(announcement);
    }

    @DeleteMapping("/announcements/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('admin') or hasRole('admin') or hasAuthority('ROLE_admin')")
    public void deleteAnnouncement(@PathVariable Long id) {
        noticeBoardService.deleteAnnouncement(id);
    }

    // --- Notice Board Items ---

    @GetMapping("/notice-board-items")
    public List<NoticeBoardItemResponse> getNoticeBoardItems() {
        return noticeBoardService.getValidNoticeBoardItems().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @PostMapping("/notice-board-items")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('admin') or hasRole('admin') or hasAuthority('ROLE_admin')")
    public NoticeBoardItemResponse createNoticeBoardItem(@RequestBody NoticeBoardItemRequest request) {
        NoticeBoardItem item = NoticeBoardItem.builder()
                .targetClient(request.getTargetClient())
                .usageDetails(request.getUsageDetails())
                .referenceUrl(request.getReferenceUrl())
                .contentUrl(request.getContentUrl())
                .expireTime(request.getExpireTime())
                .build();
        NoticeBoardItem saved = noticeBoardService.addNoticeBoardItem(item);
        return toResponse(saved);
    }

    @PostMapping("/notice-board-items/{id}/track-view")
    public void trackNoticeBoardItemView(@PathVariable Long id) {
        noticeBoardService.trackItemView(id);
    }

    @DeleteMapping("/notice-board-items/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('admin') or hasRole('admin') or hasAuthority('ROLE_admin')")
    public void deleteNoticeBoardItem(@PathVariable Long id) {
        noticeBoardService.deleteNoticeBoardItem(id);
    }

    // --- Converters ---

    private AnnouncementResponse toResponse(Announcement entity) {
        AnnouncementResponse resp = new AnnouncementResponse();
        resp.setId(entity.getId());
        resp.setTitle(entity.getTitle());
        resp.setContent(entity.getContent());
        resp.setStatus(entity.getStatus());
        resp.setExpireTime(entity.getExpireTime());
        resp.setExtractionCode(entity.getExtractionCode());
        resp.setCreateTime(entity.getCreateTime());
        resp.setUpdateTime(entity.getUpdateTime());
        return resp;
    }

    private NoticeBoardItemResponse toResponse(NoticeBoardItem entity) {
        NoticeBoardItemResponse resp = new NoticeBoardItemResponse();
        resp.setId(entity.getId());
        resp.setTargetClient(entity.getTargetClient());
        resp.setUsageDetails(entity.getUsageDetails());
        resp.setReferenceUrl(entity.getReferenceUrl());
        resp.setContentUrl(entity.getContentUrl());
        resp.setExpireTime(entity.getExpireTime());
        resp.setLastViewedTime(entity.getLastViewedTime());
        resp.setCreateTime(entity.getCreateTime());
        resp.setUpdateTime(entity.getUpdateTime());
        return resp;
    }
}

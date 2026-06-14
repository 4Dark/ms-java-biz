package com.dark.aiagent.interfaces.noticeboard.dto;

import lombok.Data;
import java.time.OffsetDateTime;

import com.dark.aiagent.domain.noticeboard.enums.AnnouncementStatus;

public class NoticeBoardDto {

    @Data
    public static class AnnouncementRequest {
        private String title;
        private String content;
        private AnnouncementStatus status;
        private OffsetDateTime expireTime;
        private String extractionCode;
    }

    @Data
    public static class AnnouncementResponse {
        private Long id;
        private String title;
        private String content;
        private AnnouncementStatus status;
        private OffsetDateTime expireTime;
        private String extractionCode;
        private OffsetDateTime createTime;
        private OffsetDateTime updateTime;
    }

    @Data
    public static class NoticeBoardItemRequest {
        private String targetClient;
        private String usageDetails;
        private String referenceUrl;
        private String contentUrl;
        private OffsetDateTime expireTime;
    }

    @Data
    public static class NoticeBoardItemResponse {
        private Long id;
        private String targetClient;
        private String usageDetails;
        private String referenceUrl;
        private String contentUrl;
        private OffsetDateTime expireTime;
        private OffsetDateTime lastViewedTime;
        private OffsetDateTime createTime;
        private OffsetDateTime updateTime;
    }
}

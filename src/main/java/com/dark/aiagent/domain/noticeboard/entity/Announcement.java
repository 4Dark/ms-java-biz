package com.dark.aiagent.domain.noticeboard.entity;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

import com.dark.aiagent.domain.noticeboard.enums.AnnouncementStatus;

@Data
@Builder
public class Announcement {
    private Long id;
    private String title;
    private String content;
    private AnnouncementStatus status;
    private OffsetDateTime expireTime;
    private String extractionCode;
    private OffsetDateTime createTime;
    private OffsetDateTime updateTime;
}

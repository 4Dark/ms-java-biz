package com.dark.aiagent.domain.noticeboard.entity;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class NoticeBoardItem {
    private Long id;
    private String targetClient;
    private String usageDetails;
    private String referenceUrl;
    private String contentUrl;
    private OffsetDateTime expireTime;
    private OffsetDateTime lastViewedTime;
    private Boolean deleted;
    private OffsetDateTime createTime;
    private OffsetDateTime updateTime;

    public void markAsDeleted() {
        this.deleted = true;
    }

    public void restoreWithNewExpireTime(OffsetDateTime newExpireTime) {
        this.expireTime = newExpireTime;
        this.deleted = false;
    }

    public void trackView() {
        this.lastViewedTime = OffsetDateTime.now();
    }
}

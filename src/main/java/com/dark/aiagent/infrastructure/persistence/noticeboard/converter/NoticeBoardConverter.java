package com.dark.aiagent.infrastructure.persistence.noticeboard.converter;

import com.dark.aiagent.domain.noticeboard.entity.Announcement;
import com.dark.aiagent.domain.noticeboard.entity.NoticeBoardItem;
import com.dark.aiagent.infrastructure.persistence.noticeboard.entity.AnnouncementDO;
import com.dark.aiagent.infrastructure.persistence.noticeboard.entity.NoticeBoardItemDO;
import org.springframework.stereotype.Component;

@Component
public class NoticeBoardConverter {

    public NoticeBoardItemDO toDO(NoticeBoardItem entity) {
        if (entity == null) return null;
        NoticeBoardItemDO itemDO = new NoticeBoardItemDO();
        itemDO.setId(entity.getId());
        itemDO.setTargetClient(entity.getTargetClient());
        itemDO.setUsageDetails(entity.getUsageDetails());
        itemDO.setReferenceUrl(entity.getReferenceUrl());
        itemDO.setContentUrl(entity.getContentUrl());
        itemDO.setExpireTime(entity.getExpireTime());
        itemDO.setLastViewedTime(entity.getLastViewedTime());
        itemDO.setDeleted(entity.getDeleted());
        itemDO.setCreateTime(entity.getCreateTime());
        itemDO.setUpdateTime(entity.getUpdateTime());
        return itemDO;
    }

    public NoticeBoardItem toEntity(NoticeBoardItemDO itemDO) {
        if (itemDO == null) return null;
        return NoticeBoardItem.builder()
                .id(itemDO.getId())
                .targetClient(itemDO.getTargetClient())
                .usageDetails(itemDO.getUsageDetails())
                .referenceUrl(itemDO.getReferenceUrl())
                .contentUrl(itemDO.getContentUrl())
                .expireTime(itemDO.getExpireTime())
                .lastViewedTime(itemDO.getLastViewedTime())
                .deleted(itemDO.getDeleted())
                .createTime(itemDO.getCreateTime())
                .updateTime(itemDO.getUpdateTime())
                .build();
    }

    public AnnouncementDO toDO(Announcement entity) {
        if (entity == null) return null;
        AnnouncementDO announcementDO = new AnnouncementDO();
        announcementDO.setId(entity.getId());
        announcementDO.setTitle(entity.getTitle());
        announcementDO.setContent(entity.getContent());
        announcementDO.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
        announcementDO.setExpireTime(entity.getExpireTime());
        announcementDO.setExtractionCode(entity.getExtractionCode());
        announcementDO.setCreateTime(entity.getCreateTime());
        announcementDO.setUpdateTime(entity.getUpdateTime());
        return announcementDO;
    }

    public Announcement toEntity(AnnouncementDO announcementDO) {
        if (announcementDO == null) return null;
        return Announcement.builder()
                .id(announcementDO.getId())
                .title(announcementDO.getTitle())
                .content(announcementDO.getContent())
                .status(announcementDO.getStatus() != null ? com.dark.aiagent.domain.noticeboard.enums.AnnouncementStatus.valueOf(announcementDO.getStatus()) : null)
                .expireTime(announcementDO.getExpireTime())
                .extractionCode(announcementDO.getExtractionCode())
                .createTime(announcementDO.getCreateTime())
                .updateTime(announcementDO.getUpdateTime())
                .build();
    }
}

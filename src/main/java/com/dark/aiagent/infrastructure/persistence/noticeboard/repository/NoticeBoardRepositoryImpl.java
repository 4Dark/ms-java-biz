package com.dark.aiagent.infrastructure.persistence.noticeboard.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dark.aiagent.domain.common.PageResult;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.dark.aiagent.domain.noticeboard.entity.Announcement;
import com.dark.aiagent.domain.noticeboard.entity.NoticeBoardItem;
import com.dark.aiagent.domain.noticeboard.repository.NoticeBoardRepository;
import com.dark.aiagent.infrastructure.persistence.noticeboard.converter.NoticeBoardConverter;
import com.dark.aiagent.infrastructure.persistence.noticeboard.entity.AnnouncementDO;
import com.dark.aiagent.infrastructure.persistence.noticeboard.entity.NoticeBoardItemDO;
import com.dark.aiagent.infrastructure.persistence.noticeboard.mapper.AnnouncementMapper;
import com.dark.aiagent.infrastructure.persistence.noticeboard.mapper.NoticeBoardItemMapper;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NoticeBoardRepositoryImpl implements NoticeBoardRepository {

    private final NoticeBoardItemMapper noticeBoardItemMapper;
    private final AnnouncementMapper announcementMapper;
    private final NoticeBoardConverter converter;

    @Override
    public NoticeBoardItem saveNoticeBoardItem(NoticeBoardItem item) {
        NoticeBoardItemDO itemDO = converter.toDO(item);
        if (itemDO.getId() == null) {
            noticeBoardItemMapper.insert(itemDO);
        } else {
            noticeBoardItemMapper.updateById(itemDO);
        }
        return converter.toEntity(itemDO);
    }

    @Override
    public Optional<NoticeBoardItem> findNoticeBoardItemById(Long id) {
        NoticeBoardItemDO itemDO = noticeBoardItemMapper.selectById(id);
        return Optional.ofNullable(itemDO).map(converter::toEntity);
    }

    @Override
    public List<NoticeBoardItem> findAllValidNoticeBoardItems() {
        // Find items where expireTime > now
        LambdaQueryWrapper<NoticeBoardItemDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.gt(NoticeBoardItemDO::getExpireTime, OffsetDateTime.now())
                .orderByDesc(NoticeBoardItemDO::getCreateTime);
        return noticeBoardItemMapper.selectList(wrapper).stream().map(converter::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteExpiredNoticeBoardItems() {
        // Soft delete items where expireTime <= now
        LambdaUpdateWrapper<NoticeBoardItemDO> wrapper = new LambdaUpdateWrapper<>();
        wrapper.le(NoticeBoardItemDO::getExpireTime, OffsetDateTime.now());
        noticeBoardItemMapper.delete(wrapper); // MyBatis-Plus TableLogic handles soft delete
    }

    @Override
    public void updateLastViewedTime(Long id) {
        NoticeBoardItemDO itemDO = new NoticeBoardItemDO();
        itemDO.setId(id);
        itemDO.setLastViewedTime(OffsetDateTime.now());
        noticeBoardItemMapper.updateById(itemDO);
    }

    @Override
    public void deleteNoticeBoardItem(Long id) {
        noticeBoardItemMapper.deleteById(id);
    }

    @Override
    public Announcement saveAnnouncement(Announcement announcement) {
        AnnouncementDO announcementDO = converter.toDO(announcement);
        if (announcementDO.getId() == null) {
            announcementMapper.insert(announcementDO);
        } else {
            announcementMapper.updateById(announcementDO);
        }
        return converter.toEntity(announcementDO);
    }

    @Override
    public List<Announcement> findAllAnnouncements(String keyword) {
        LambdaQueryWrapper<AnnouncementDO> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(AnnouncementDO::getTitle, keyword)
                              .or()
                              .like(AnnouncementDO::getContent, keyword));
        }
        wrapper.orderByDesc(AnnouncementDO::getCreateTime);
        return announcementMapper.selectList(wrapper).stream().map(ann -> converter.toEntity(ann))
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<Announcement> findAnnouncementsPaged(int page, int size, String keyword) {
        LambdaQueryWrapper<AnnouncementDO> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(AnnouncementDO::getTitle, keyword)
                              .or()
                              .like(AnnouncementDO::getContent, keyword));
        }
        wrapper.orderByDesc(AnnouncementDO::getCreateTime);
        Page<AnnouncementDO> mpPage = new Page<>(page, size);
        IPage<AnnouncementDO> resultPage = announcementMapper.selectPage(mpPage, wrapper);
        List<Announcement> records = resultPage.getRecords().stream()
                .map(ann -> converter.toEntity(ann))
                .collect(Collectors.toList());
        return new PageResult<>(records, resultPage.getTotal(), resultPage.getSize(), resultPage.getCurrent(), resultPage.getPages());
    }

    @Override
    public Optional<Announcement> findAnnouncementById(Long id) {
        AnnouncementDO announcementDO = announcementMapper.selectById(id);
        return Optional.ofNullable(announcementDO).map(ann -> converter.toEntity(ann));
    }

    @Override
    public void deleteAnnouncement(Long id) {
        announcementMapper.deleteById(id);
    }
}

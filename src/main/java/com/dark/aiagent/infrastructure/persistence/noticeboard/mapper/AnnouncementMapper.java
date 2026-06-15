package com.dark.aiagent.infrastructure.persistence.noticeboard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dark.aiagent.infrastructure.persistence.noticeboard.entity.AnnouncementDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AnnouncementMapper extends BaseMapper<AnnouncementDO> {
}

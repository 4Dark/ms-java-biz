package com.dark.aiagent.infrastructure.persistence.noticeboard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dark.aiagent.infrastructure.persistence.noticeboard.entity.NoticeBoardItemDO;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface NoticeBoardItemMapper extends BaseMapper<NoticeBoardItemDO> {
}

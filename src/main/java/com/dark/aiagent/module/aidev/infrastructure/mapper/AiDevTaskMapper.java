package com.dark.aiagent.module.aidev.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dark.aiagent.module.aidev.infrastructure.dataobject.AiDevTaskPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AiDevTaskMapper extends BaseMapper<AiDevTaskPO> {
}

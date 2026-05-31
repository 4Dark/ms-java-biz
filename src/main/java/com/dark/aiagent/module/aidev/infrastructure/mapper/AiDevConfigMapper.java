package com.dark.aiagent.module.aidev.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dark.aiagent.module.aidev.infrastructure.dataobject.AiDevConfigPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI Dev 配置数据库 Mapper。
 */
@Mapper
public interface AiDevConfigMapper extends BaseMapper<AiDevConfigPO> {
}

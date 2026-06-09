package com.dark.aiagent.module.aidev.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dark.aiagent.module.aidev.infrastructure.dataobject.AiDevAuditLogPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AiDevAuditLogMapper extends BaseMapper<AiDevAuditLogPO> {
}

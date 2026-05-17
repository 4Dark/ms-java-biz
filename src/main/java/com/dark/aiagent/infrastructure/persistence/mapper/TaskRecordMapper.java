package com.dark.aiagent.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dark.aiagent.infrastructure.persistence.entity.TaskRecordDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskRecordMapper extends BaseMapper<TaskRecordDO> {
}

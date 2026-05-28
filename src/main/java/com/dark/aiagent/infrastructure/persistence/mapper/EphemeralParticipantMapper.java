package com.dark.aiagent.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dark.aiagent.infrastructure.persistence.entity.EphemeralParticipantDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 匿名参与者 MyBatis-Plus Mapper
 */
@Mapper
public interface EphemeralParticipantMapper extends BaseMapper<EphemeralParticipantDO> {
}

package com.dark.aiagent.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dark.aiagent.infrastructure.persistence.entity.EphemeralRoomDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 阅后即焚房间 MyBatis-Plus Mapper
 */
@Mapper
public interface EphemeralRoomMapper extends BaseMapper<EphemeralRoomDO> {
}

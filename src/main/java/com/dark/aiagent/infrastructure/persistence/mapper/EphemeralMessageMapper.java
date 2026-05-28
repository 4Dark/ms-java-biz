package com.dark.aiagent.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dark.aiagent.infrastructure.persistence.entity.EphemeralMessageDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 加密消息 MyBatis-Plus Mapper
 */
@Mapper
public interface EphemeralMessageMapper extends BaseMapper<EphemeralMessageDO> {

    /**
     * 软删除指定发送者在指定房间内的所有消息。
     *
     * @param roomId   房间 ID
     * @param senderId 发送者匿名 ID
     * @return 影响行数
     */
    @Update("UPDATE ms_ephemeral_message SET is_deleted = TRUE " +
            "WHERE room_id = #{roomId} AND sender_id = #{senderId} AND is_deleted = FALSE")
    int softDeleteBySender(@Param("roomId") String roomId, @Param("senderId") String senderId);
}

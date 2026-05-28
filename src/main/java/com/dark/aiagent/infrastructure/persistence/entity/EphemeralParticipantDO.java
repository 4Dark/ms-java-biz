package com.dark.aiagent.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.OffsetDateTime;

/**
 * 匿名参与者数据对象（ORM 映射层）
 */
@Data
@TableName("ms_ephemeral_participant")
public class EphemeralParticipantDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String roomId;
    private String participantId;
    private String nicknameCipher;
    private OffsetDateTime joinedAt;
    private OffsetDateTime lastSeenAt;
}

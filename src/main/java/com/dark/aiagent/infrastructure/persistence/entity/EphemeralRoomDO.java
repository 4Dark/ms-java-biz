package com.dark.aiagent.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.OffsetDateTime;

/**
 * 阅后即焚房间数据对象（ORM 映射层，与领域层解耦）
 */
@Data
@TableName("ms_ephemeral_room")
public class EphemeralRoomDO {

    @TableId(type = IdType.INPUT)
    private String id;

    private String shortCode;
    private String title;
    private Long maxTtlSeconds;
    private OffsetDateTime expireAt;
    private OffsetDateTime lastActiveAt;
    private String createdBy;
    private Boolean isDestroyed;
    private OffsetDateTime createTime;
    private OffsetDateTime updateTime;
}

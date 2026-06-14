package com.dark.aiagent.infrastructure.persistence.noticeboard.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@TableName("ms_announcement")
public class AnnouncementDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    private String status;
    private OffsetDateTime expireTime;
    private String extractionCode;
    private OffsetDateTime createTime;
    private OffsetDateTime updateTime;
}

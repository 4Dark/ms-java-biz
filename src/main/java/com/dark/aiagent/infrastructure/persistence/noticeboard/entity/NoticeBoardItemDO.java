package com.dark.aiagent.infrastructure.persistence.noticeboard.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@TableName("ms_notice_board_item")
public class NoticeBoardItemDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String targetClient;
    private String usageDetails;
    private String referenceUrl;
    private String contentUrl;
    private OffsetDateTime expireTime;
    private OffsetDateTime lastViewedTime;
    
    @TableLogic(value = "false", delval = "true")
    private Boolean deleted;
    
    private OffsetDateTime createTime;
    private OffsetDateTime updateTime;
}

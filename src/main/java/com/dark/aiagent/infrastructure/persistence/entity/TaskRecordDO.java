package com.dark.aiagent.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@TableName("ms_task_record")
public class TaskRecordDO {
    @TableId(type = IdType.INPUT)
    private String id;

    private String taskType;

    private String status;

    private Integer totalCount;

    private Integer processedCount;

    private String currentItemName;

    private String errorMessage;

    private OffsetDateTime createTime;

    private OffsetDateTime updateTime;
}

package com.dark.aiagent.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.OffsetDateTime;

/**
 * 加密消息数据对象（ORM 映射层）
 *
 * <p>cipher_text 和 iv 由客户端生成，服务器原样存储，不参与加解密。
 */
@Data
@TableName("ms_ephemeral_message")
public class EphemeralMessageDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String roomId;
    private String senderId;
    private String cipherText;
    private String iv;
    private OffsetDateTime sentAt;
    private Boolean isDeleted;
}

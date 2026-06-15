package com.dark.aiagent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.upload")
public class UploadProperties {

    /** 图片存储的本地目录路径 */
    private String imageDir = "/tmp/ai_uploads/images";
}

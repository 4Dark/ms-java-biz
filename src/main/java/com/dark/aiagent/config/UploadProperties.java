package com.dark.aiagent.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.upload")
public class UploadProperties {

    /** 图片存储的本地目录路径 */
    private String imageDir = "/tmp/ai_uploads/images";
}

package com.dark.aiagent.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 静态资源映射配置。
 * 将本地上传目录映射为可通过 /uploads/images/** 访问的 HTTP 静态资源。
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final UploadProperties uploadProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path imagePath = Paths.get(uploadProperties.getImageDir()).toAbsolutePath().normalize();
        registry.addResourceHandler("/uploads/images/**")
                .addResourceLocations("file:" + imagePath + "/");
    }
}

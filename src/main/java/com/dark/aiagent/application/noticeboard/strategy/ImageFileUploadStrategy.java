package com.dark.aiagent.application.noticeboard.strategy;

import com.dark.aiagent.config.UploadProperties;
import com.dark.aiagent.module.common.upload.FileUploadStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 图片上传策略，对应 type=image。
 * 统一入口：POST /rest/biz/v1/upload/image
 * 图片存储至 app.upload.image-dir，通过 /uploads/images/** 对外访问。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ImageFileUploadStrategy implements FileUploadStrategy {

    private final UploadProperties uploadProperties;

    private static final long MAX_SIZE_BYTES = 10 * 1024 * 1024L; // 10 MB
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/svg+xml"
    );

    @Override
    public String getUploadType() {
        return "image";
    }

    @Override
    public Object handleUpload(MultipartFile file, Map<String, Object> extraParams) {
        // 1. 类型校验
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("不支持的图片类型，仅允许 JPEG / PNG / GIF / WebP / SVG");
        }

        // 2. 大小校验
        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new IllegalArgumentException("图片大小不能超过 10MB");
        }

        try {
            // 3. 确保目录存在
            Path uploadDir = Paths.get(uploadProperties.getImageDir()).toAbsolutePath().normalize();
            Files.createDirectories(uploadDir);

            // 4. 生成唯一文件名（防路径穿越）
            String extension = resolveExtension(file.getOriginalFilename(), contentType);
            String filename = UUID.randomUUID() + extension;
            Path targetPath = uploadDir.resolve(filename);

            // 5. 写入磁盘
            file.transferTo(targetPath);
            log.info("【ImageUpload】Saved: {} ({} bytes)", filename, file.getSize());

            // 6. 返回相对 URL，前端拼接 gateway base URL 使用
            return Map.of("url", "/uploads/images/" + filename);

        } catch (IOException e) {
            log.error("【ImageUpload】Failed to save image", e);
            throw new RuntimeException("图片保存失败，请稍后重试");
        }
    }

    private String resolveExtension(String originalFilename, String contentType) {
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png"  -> ".png";
            case "image/gif"  -> ".gif";
            case "image/webp" -> ".webp";
            case "image/svg+xml" -> ".svg";
            default           -> ".img";
        };
    }
}

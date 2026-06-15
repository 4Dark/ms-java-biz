package com.dark.aiagent.module.aidev.domain.repository;

import java.util.Optional;

/**
 * AI Dev 配置仓库接口（端口）。
 */
public interface AiDevConfigRepository {
    Optional<String> findValueByKey(String key);

    void saveOrUpdate(String key, String value);
}

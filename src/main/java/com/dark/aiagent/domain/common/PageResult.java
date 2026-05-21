package com.dark.aiagent.domain.common;

import java.util.List;

/**
 * 通用分页结果模型 (Domain Layer)
 * 封装分页状态，剥离底层框架，确保领域模型的纯洁性
 */
public record PageResult<T>(
    List<T> records,
    long total,
    long size,
    long current,
    long pages
) {}

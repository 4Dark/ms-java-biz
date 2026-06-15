---
trigger: glob
globs: ["**/*.sql", "**/migration/**", "**/db/**"]
---

# 数据库规范 (ms-java-biz)

## 1. Flyway 迁移规范
- **脚本不可变性**: 严禁修改已 Applied 的 SQL 迁移脚本，变更必须通过新版本号（如 `V1.3__...`）
- **Checksum 修复**: 开发环境误改旧脚本导致 `FlywayValidateException` 时，使用 `FlywayMigrationStrategy.repair()`

## 2. 数据库设计规范
| 规范项 | 要求 |
|--------|------|
| **表命名** | 小写 snake_case，加 `ms_` 前缀（如 `ms_user`，`ms_recipe`） |
| **单数原则** | 表名用单数（`user` 非 `users`） |
| **主键** | 统一使用 `id` |
| **公共字段** | 强制包含 `create_time` / `update_time`，类型为 `TIMESTAMPTZ` |
| **布尔值** | 使用 `is_xxx` 命名（如 `is_deleted`） |
| **注释** | 所有表和字段必须通过 `COMMENT` 添加业务说明 |

## 3. 数据类型映射
- `TIMESTAMPTZ` → Java 必须映射为 `java.time.OffsetDateTime`
- **禁止使用** `LocalDateTime`（会导致时区转换异常及 SQL 语法错误）

## 4. SQL 治理
- 注解中（`@Select`/`@Update`）超过 5 行或含复杂逻辑的 SQL 必须移至 MyBatis XML 或数据库视图
- 大表聚合查询禁止实时 `GROUP BY`，必须建立物理汇总表
- 所有外键字段和高频查询字段必须建立索引，禁止全表扫描

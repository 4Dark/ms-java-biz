---
trigger: glob
globs: ["**/*.yaml", "**/*.yml"]
---

# 本地开发指南 (ms-java-biz)

## 服务配置
- **端口**: `8080`
- **VS Code 启动**: `.vscode/launch.json`

## AI 重启规范
重启服务时必须读取 `.vscode/launch.json` 提取正确的环境变量和 JVM 参数，确保 Nacos 连接等配置一致。

## Nacos 白名单同步
新增放行白名单时，本地 `application.yml` 和 Nacos 配置中心必须**同步更新**，防止 stale 配置导致 403 故障。

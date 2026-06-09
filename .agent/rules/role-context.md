---
trigger: always_on
---

# Role (角色)
你是一位精通 Spring Boot、RAG 系统以及 Model Context Protocol (MCP) 协议的企业级 Java 开发专家。

# Tech Stack (技术栈)
- Java 17+ / Spring Boot 3.x / Spring MVC (Servlet)
- PostgreSQL + pgvector | MyBatis-Plus | LangChain4j

# 系统边界 (System Boundary)
- **本服务职责**: 处理企业核心业务逻辑与数据持久化，封装为 MCP 工具供 ms-py-agent 调用
- **禁止在此实现**: 对话状态管理、复杂 Agent 编排逻辑
- **API 前缀**: 所有对前端暴露的端点必须使用 `/rest/biz/v1/...`

# Key Context
`ms-java-biz` 连接 Nacos 进行服务注册，通过 MCP SSE (`/mcp/sse` + `/mcp/messages`) 供 ms-py-agent 远程调用。

> 📖 编码规范详见 `coding-java.md`（打开 *.java 文件自动激活）
> 🧪 测试规范详见 `testing-java.md`（打开 *Test.java 自动激活）
> 🗄️ 数据库规范详见 `db-migration-rules.md`（打开 *.sql 自动激活）

---
trigger: glob
globs: ["**/*.java"]
---

# Java 编码规范 (ms-java-biz)

## 1. MCP 协议实现
- Controller 层 `McpController` 必须包含：
  - `GET /mcp/sse` — 握手/长连接（SseEmitter）
  - `POST /mcp/messages` — JSON-RPC 指令处理
- **禁止**在 MCP 工具执行时阻塞返回 HTTP 响应，必须通过 `emitters` Map 找到 SSE 连接推送结果
- `McpController` 支持 `sessionId` 可选：缺失时直接作为 HTTP Body 返回（无状态发现）

## 2. 架构模式
- **策略模式**: 所有 AI 工具必须实现 `McpTool` 接口，通过 Spring 自动装配注册
- **DTO**: 使用 Java `record` 定义 MCP 协议对象（如 `JsonRpcRequest`, `JsonRpcResponse`）
- **白名单**: 所有 `/mcp/**` 路由必须在安全配置中显式加入白名单

## 3. 数据访问
- 使用 **MyBatis-Plus**；PostgreSQL `JSONB` 字段必须配置 `JacksonTypeHandler`
- 复杂配置项（如 RAG 配置）定义为不可变 Java `record`，通过 JSONB 存储
- 向量搜索使用 **LangChain4j** Embedding Store 接口

## 4. 代码可读性
- **禁止全路径引用**: 除类名冲突外，必须用 `import` 而非全路径（如禁止 `dev.langchain4j.xxx.Xxx`）
- **禁止通配符导入**: 不允许 `import java.util.*`

## 5. 跨服务安全
- 调用 `ms-py-agent` 等下游服务时，必须通过 `JwtTokenInterceptor` 透传 JWT Token

## 6. API 命名规范
- 对前端暴露端点：前缀统一为 `/rest/biz/v1/...`
- API 定义文档输出至 `/ms-project-docs/api-contracts/`

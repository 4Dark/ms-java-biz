---
name: ms-java-biz-deep-reference
description: >
  ms-java-biz 完整编码规范深度参考。在以下场景下加载：
  - 需要了解 MCP SSE 协议的完整实现细节
  - 需要深入理解 DDD 值对象持久化到 JSONB 的模式
  - 需要了解 LangChain4j 集成的完整规范
  - 处理复杂的跨服务通信架构设计时
---

# ms-java-biz 完整规范参考

> 此文档是完整的深度参考，日常编码请使用 Glob 触发的 `coding-java.md`

## MCP 协议完整规范

### Controller 层设计
- `McpController` 必须包含：
  - `GET /mcp/sse` → 握手/长连接（`SseEmitter`）
  - `POST /mcp/messages` → JSON-RPC 指令处理
- 双模响应：`sessionId` 存在时通过 SSE 推送；缺失时直接作为 HTTP Body 返回
- 所有 `/mcp/**` 路由必须在安全配置中显式加入白名单

### 架构模式
- 所有 AI 工具实现 `McpTool` 接口（策略模式）
- 利用 Spring 自动装配扫描 `@Component` Bean 注入注册中心 Map
- 使用 Java `record` 定义 `JsonRpcRequest`, `JsonRpcResponse`

## 数据访问完整规范

### MyBatis-Plus
- PostgreSQL `JSONB` 字段必须配置 `JacksonTypeHandler` 自动映射
- 复杂配置项（RAG 配置等）定义为不可变 Java `record`，通过 JSONB 存储

### LangChain4j 集成
- 向量搜索通过 LangChain4j Embedding Store 接口
- 禁止全路径引用（如 `dev.langchain4j.model.chat.ChatLanguageModel`），必须 import

## DDD 领域模型规范

### 领域对象纯洁性
- Domain 层 Entity/VO 必须是纯 POJO，**严禁**引入 `jakarta.persistence.*` 或 Spring 核心注解
- 领域层不得依赖 Service、Controller、Repository 的实现逻辑

### 充血模型
- **禁止贫血模型**：实体类不能只有 Getter/Setter
- 状态改变通过业务语义方法（如 `user.changePassword(newPwd)`），不暴露 `setPassword()`
- 提供私有无参构造函数，公开实例化通过工厂方法/Builder

### Entity vs Value Object
- **Entity**: 含唯一 ID，`equals()`/`hashCode()` 仅基于 ID
- **Value Object**: 不可变（Java 14+ 用 `record`），`equals()`/`hashCode()` 基于所有属性

## 微服务间通信
- Application Service 不直接依赖 `RestTemplate`/`HashMap`，通过 Port 接口 + Infrastructure Adapter
- 外部调用使用明确 DTO（Java Record），禁止 `Map<String, Object>`
- 其他微服务 URL 集中管理在 `RemoteApiConstants.java`，按微服务内部静态类分组

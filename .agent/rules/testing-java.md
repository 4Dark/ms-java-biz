---
trigger: glob
globs: ["**/*Test.java", "**/*Tests.java", "**/*IT.java"]
---

# Java 测试规范 (ms-java-biz)

## 1. 基础设施 Mock
- **禁止**在测试中直连 MongoDB；通过 `@TestConfiguration` 提供 Mock `MongoTemplate`，并 Mock `getConverter()`
- 在 `application-test.yml` 排除不必要的自动配置（如 `MongoAutoConfiguration`）以加快启动

## 2. 切片测试优先
- Controller 层测试优先使用 `@WebMvcTest`，而非 `@SpringBootTest`
- 避免加载不必要的业务配置类和基础设施 Bean

## 3. 依赖冲突防护
- 当 Spring Cloud Alibaba 与 LangChain4j BOM 同时引用，出现 `NoSuchMethodError` 时：
  1. 执行 `mvn dependency:tree` 检查版本冲突
  2. 在 `pom.xml` 中显式指定高版本 SDK（通常是 DashScope SDK）

## 4. TDD 原则
- 核心业务逻辑（Use Case, Domain Entity）必须满足 **100% 测试覆盖率**
- 先写测试，再写实现

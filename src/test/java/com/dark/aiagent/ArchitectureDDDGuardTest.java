package com.dark.aiagent;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * 静态架构卫兵测试 (ArchUnit Guard)
 * 严格验证 DDD 4层分层依赖规范，防止任何层级反向/越权越界调用。
 */
@AnalyzeClasses(packages = "com.dark.aiagent", importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchitectureDDDGuardTest {

    /**
     * 核心规则 1：领域模型纯洁性 (Domain Purity Guard)
     * Domain 层的 Entity 和 Value Object 必须是纯 POJO，绝对禁止逆向依赖 Application、Infrastructure 或 Interfaces 层。
     */
    @ArchTest
    public static final ArchRule domain_should_not_depend_on_outer_layers = noClasses()
            .that().resideInAPackage("com.dark.aiagent.domain..")
            .should().dependOnClassesThat().resideInAPackage("com.dark.aiagent.application..")
            .orShould().dependOnClassesThat().resideInAPackage("com.dark.aiagent.infrastructure..")
            .orShould().dependOnClassesThat().resideInAPackage("com.dark.aiagent.interfaces..")
            .orShould().dependOnClassesThat().resideInAPackage("com.dark.aiagent.mcp..")
            .because("Domain layer must be a pure POJO and have no dependencies on outer layers or infrastructure frameworks.");

    /**
     * 核心规则 2：应用逻辑隔离性 (Application Boundary Guard)
     * Application 层负责业务逻辑的编排与表达，绝对不能反向依赖 Interfaces 或 MCP 展示层控制器。
     */
    @ArchTest
    public static final ArchRule application_should_not_depend_on_presentation = noClasses()
            .that().resideInAPackage("com.dark.aiagent.application..")
            .should().dependOnClassesThat().resideInAPackage("com.dark.aiagent.interfaces..")
            .orShould().dependOnClassesThat().resideInAPackage("com.dark.aiagent.mcp..")
            .because("Application layer should have no knowledge of HTTP presentation or outer controllers.");
}

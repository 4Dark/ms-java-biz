package com.dark.aiagent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * MCP 相关配置属性
 */
@Component
@ConfigurationProperties(prefix = "app.mcp")
public class McpProperties {

    @Value("${server.port:8080}")
    private int serverPort;

    /**
     * 本地内置 MCP 服务的基准 URL（如 http://localhost:8080）
     * 如果未配置，将默认回退至 http://localhost:${server.port}
     */
    private String localServerBaseUrl;

    public String getLocalServerBaseUrl() {
        if (localServerBaseUrl == null || localServerBaseUrl.isEmpty()) {
            return "http://localhost:" + serverPort;
        }
        return localServerBaseUrl;
    }

    public void setLocalServerBaseUrl(String localServerBaseUrl) {
        this.localServerBaseUrl = localServerBaseUrl;
    }
}

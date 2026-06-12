-- V1.0.3: Initialize MCP Registry Schema
CREATE TABLE IF NOT EXISTS ms_mcp_server_registry (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL UNIQUE,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    icon VARCHAR(50),
    type VARCHAR(20) NOT NULL,
    config JSONB NOT NULL,
    is_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    is_system BOOLEAN NOT NULL DEFAULT FALSE,
    create_time TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE ms_mcp_server_registry IS 'MCP 服务端定义表';

CREATE TABLE IF NOT EXISTS ms_mcp_tool_cache (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    server_id UUID NOT NULL REFERENCES ms_mcp_server_registry(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    input_schema JSONB NOT NULL,
    create_time TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(server_id, name)
);
COMMENT ON TABLE ms_mcp_tool_cache IS 'MCP 工具Schema缓存表';

CREATE TABLE IF NOT EXISTS ms_user_preference (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id VARCHAR(100) NOT NULL,
    preference_key VARCHAR(100) NOT NULL,
    preference_value JSONB NOT NULL,
    create_time TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, preference_key)
);
COMMENT ON TABLE ms_user_preference IS '用户偏好配置表';

INSERT INTO ms_mcp_server_registry (name, title, description, icon, type, config, is_enabled, is_system)
VALUES 
('filesystem', '本地文件系统', '允许 AI 访问和操作本地工作目录下的文件', 'folder_open', 'stdio', '{"command": "npx", "args": ["-y", "@modelcontextprotocol/server-filesystem", "/Users/pei/projects"]}', true, true),
('java-biz', '业务能力中心', '提供订单查询、知识库检索等企业核心业务能力', 'business_center', 'sse', '{"url": "/mcp/sse", "messages_url": "/mcp/messages"}', true, true)
ON CONFLICT (name) DO NOTHING;

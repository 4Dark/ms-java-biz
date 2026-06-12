-- V1.0.1: Initialize Knowledge and RAG Schema
CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS ms_knowledge_topic (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    icon VARCHAR(64),
    description TEXT,
    visible_scope VARCHAR(64) DEFAULT 'public',
    template_name VARCHAR(64),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE ms_knowledge_topic IS '知识库主题表';

CREATE TABLE IF NOT EXISTS ms_knowledge_document (
    id VARCHAR(64) PRIMARY KEY,
    topic_id VARCHAR(64) NOT NULL,
    title VARCHAR(255) NOT NULL,
    status VARCHAR(64) DEFAULT '未处理',
    author VARCHAR(128),
    file_path TEXT,
    config_json JSONB,
    doc_type VARCHAR(50) DEFAULT 'general',
    file_hash VARCHAR(64) DEFAULT '',
    category VARCHAR(100) DEFAULT '其他',
    metadata JSONB DEFAULT '{}'::jsonb,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE ms_knowledge_document IS '知识库文档映射表';
COMMENT ON COLUMN ms_knowledge_document.doc_type IS '文档类型，如 general (通用), recipe (食谱)';
COMMENT ON COLUMN ms_knowledge_document.file_hash IS '文档哈希值，用于增量校验';
COMMENT ON COLUMN ms_knowledge_document.category IS '文档分类';
COMMENT ON COLUMN ms_knowledge_document.metadata IS '文档扩展属性 JSONB';

CREATE INDEX idx_knowledge_document_topic ON ms_knowledge_document(topic_id);
CREATE INDEX IF NOT EXISTS idx_ms_knowledge_doc_metadata ON ms_knowledge_document USING gin (metadata);

CREATE TABLE IF NOT EXISTS ms_knowledge_chunk (
    id SERIAL PRIMARY KEY,
    document_id VARCHAR(64) NOT NULL REFERENCES ms_knowledge_document(id) ON DELETE CASCADE,
    chunk_index INTEGER NOT NULL,
    content TEXT NOT NULL,
    embedding vector(512),
    create_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE ms_knowledge_chunk IS '通用知识库分块向量表';
COMMENT ON COLUMN ms_knowledge_chunk.id IS '主键ID';
COMMENT ON COLUMN ms_knowledge_chunk.document_id IS '关联的文档ID';
COMMENT ON COLUMN ms_knowledge_chunk.chunk_index IS '分块索引';
COMMENT ON COLUMN ms_knowledge_chunk.content IS '分块文本内容';
COMMENT ON COLUMN ms_knowledge_chunk.embedding IS '分块向量值';

CREATE INDEX IF NOT EXISTS idx_ms_knowledge_chunk_doc_id ON ms_knowledge_chunk(document_id);

CREATE TABLE IF NOT EXISTS ms_task_record (
    id VARCHAR(64) PRIMARY KEY,
    task_type VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL,
    total_count INTEGER DEFAULT 0,
    processed_count INTEGER DEFAULT 0,
    current_item_name VARCHAR(255),
    error_message TEXT,
    create_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE ms_task_record IS '通用后台任务表';

INSERT INTO ms_knowledge_topic (id, name, icon, description, visible_scope, template_name)
VALUES ('topic_recipe_001', '菜谱', 'restaurant', '专业美食菜谱知识库', 'public', 'recipe')
ON CONFLICT (id) DO NOTHING;

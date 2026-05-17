-- V1.13__merge_recipe_into_knowledge.sql

-- 1. Drop obsolete tables
DROP TABLE IF EXISTS ms_recipe_chunk CASCADE;
DROP TABLE IF EXISTS ms_recipe_document CASCADE;

-- 2. Alter ms_knowledge_document to support unified knowledge metadata
ALTER TABLE ms_knowledge_document ADD COLUMN IF NOT EXISTS doc_type VARCHAR(50) DEFAULT 'general';
ALTER TABLE ms_knowledge_document ADD COLUMN IF NOT EXISTS file_hash VARCHAR(64) DEFAULT '';
ALTER TABLE ms_knowledge_document ADD COLUMN IF NOT EXISTS category VARCHAR(100) DEFAULT '其他';
ALTER TABLE ms_knowledge_document ADD COLUMN IF NOT EXISTS metadata JSONB DEFAULT '{}'::jsonb;

-- Add comments for documentation
COMMENT ON COLUMN ms_knowledge_document.doc_type IS '文档类型，如 general (通用), recipe (食谱)';
COMMENT ON COLUMN ms_knowledge_document.file_hash IS '文档哈希值，用于增量校验';
COMMENT ON COLUMN ms_knowledge_document.category IS '文档分类';
COMMENT ON COLUMN ms_knowledge_document.metadata IS '文档扩展属性 JSONB';

-- 3. Create universal knowledge chunk table
CREATE TABLE IF NOT EXISTS ms_knowledge_chunk (
    id SERIAL PRIMARY KEY,
    document_id VARCHAR(64) NOT NULL REFERENCES ms_knowledge_document(id) ON DELETE CASCADE,
    chunk_index INTEGER NOT NULL,
    content TEXT NOT NULL,
    embedding vector(512),               -- pgvector BAAI/bge-small-zh-v1.5 embedding dimension
    create_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE ms_knowledge_chunk IS '通用知识库分块向量表';
COMMENT ON COLUMN ms_knowledge_chunk.id IS '主键ID';
COMMENT ON COLUMN ms_knowledge_chunk.document_id IS '关联的文档ID';
COMMENT ON COLUMN ms_knowledge_chunk.chunk_index IS '分块索引';
COMMENT ON COLUMN ms_knowledge_chunk.content IS '分块文本内容';
COMMENT ON COLUMN ms_knowledge_chunk.embedding IS '分块向量值';
COMMENT ON COLUMN ms_knowledge_chunk.create_time IS '创建时间';
COMMENT ON COLUMN ms_knowledge_chunk.update_time IS '更新时间';

-- 4. Create performance indexes
CREATE INDEX IF NOT EXISTS idx_ms_knowledge_doc_metadata ON ms_knowledge_document USING gin (metadata);
CREATE INDEX IF NOT EXISTS idx_ms_knowledge_chunk_doc_id ON ms_knowledge_chunk(document_id);

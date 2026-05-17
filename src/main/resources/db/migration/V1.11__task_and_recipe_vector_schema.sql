-- V1.11__task_and_recipe_vector_schema.sql

-- Enable pgvector extension if not exists
CREATE EXTENSION IF NOT EXISTS vector;

-- 1. Create ms_task_record
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
COMMENT ON COLUMN ms_task_record.id IS '任务ID';
COMMENT ON COLUMN ms_task_record.task_type IS '任务类型，如 KNOWLEDGE_BUILD';
COMMENT ON COLUMN ms_task_record.status IS '任务状态，RUNNING, SUCCESS, FAILED';
COMMENT ON COLUMN ms_task_record.total_count IS '总数';
COMMENT ON COLUMN ms_task_record.processed_count IS '已处理数';
COMMENT ON COLUMN ms_task_record.current_item_name IS '当前处理项名称';
COMMENT ON COLUMN ms_task_record.error_message IS '错误信息';
COMMENT ON COLUMN ms_task_record.create_time IS '创建时间';
COMMENT ON COLUMN ms_task_record.update_time IS '更新时间';

-- 2. Create ms_recipe_document
CREATE TABLE IF NOT EXISTS ms_recipe_document (
    id SERIAL PRIMARY KEY,
    file_path VARCHAR(512) NOT NULL UNIQUE,
    file_hash VARCHAR(64) NOT NULL,
    dish_name VARCHAR(255) NOT NULL,
    category VARCHAR(64),
    difficulty VARCHAR(32),
    create_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE ms_recipe_document IS '菜谱文档表';
COMMENT ON COLUMN ms_recipe_document.id IS '主键ID';
COMMENT ON COLUMN ms_recipe_document.file_path IS '相对文件路径，唯一标识';
COMMENT ON COLUMN ms_recipe_document.file_hash IS '文件MD5或SHA256哈希值';
COMMENT ON COLUMN ms_recipe_document.dish_name IS '菜名';
COMMENT ON COLUMN ms_recipe_document.category IS '菜品分类';
COMMENT ON COLUMN ms_recipe_document.difficulty IS '烹饪难度';
COMMENT ON COLUMN ms_recipe_document.create_time IS '创建时间';
COMMENT ON COLUMN ms_recipe_document.update_time IS '更新时间';

-- 3. Create ms_recipe_chunk
CREATE TABLE IF NOT EXISTS ms_recipe_chunk (
    id SERIAL PRIMARY KEY,
    document_id INTEGER NOT NULL REFERENCES ms_recipe_document(id) ON DELETE CASCADE,
    chunk_index INTEGER NOT NULL,
    content TEXT NOT NULL,
    embedding vector(512),
    create_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE ms_recipe_chunk IS '菜谱分块向量表';
COMMENT ON COLUMN ms_recipe_chunk.id IS '主键ID';
COMMENT ON COLUMN ms_recipe_chunk.document_id IS '关联的文档ID';
COMMENT ON COLUMN ms_recipe_chunk.chunk_index IS '分块索引';
COMMENT ON COLUMN ms_recipe_chunk.content IS '分块文本内容';
COMMENT ON COLUMN ms_recipe_chunk.embedding IS '分块向量值';
COMMENT ON COLUMN ms_recipe_chunk.create_time IS '创建时间';
COMMENT ON COLUMN ms_recipe_chunk.update_time IS '更新时间';

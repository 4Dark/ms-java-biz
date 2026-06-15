-- V1.0.5: Initialize Recipe Schema
CREATE TABLE IF NOT EXISTS ms_recipe (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(64),
    difficulty VARCHAR(32),
    image_url TEXT,
    ingredients TEXT,
    instructions TEXT,
    create_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
COMMENT ON TABLE ms_recipe IS '菜谱信息表';

CREATE TABLE IF NOT EXISTS ms_user_favorite (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    recipe_id INTEGER NOT NULL,
    create_time TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, recipe_id)
);
COMMENT ON TABLE ms_user_favorite IS '用户菜谱收藏表';

INSERT INTO ms_recipe (name, category, difficulty, ingredients, instructions)
VALUES 
('四川火锅', '川菜', '中等', '火锅底料, 牛肉, 蔬菜', '1. 煮沸底料 2. 加入食材'),
('广式点心', '粤菜', '困难', '面粉, 虾仁, 猪肉', '1. 包裹食材 2. 蒸熟'),
('东坡肉', '鲁菜', '中等', '五花肉, 酱油, 糖', '1. 炖煮 2. 收汁')
ON CONFLICT DO NOTHING;

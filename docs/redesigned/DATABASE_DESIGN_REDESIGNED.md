# AI日记App - 重新设计版本数据库设计文档

## 📋 数据库概述

### 基本信息
- **数据库类型**: PostgreSQL 15+ (主数据库) + Redis (缓存)
- **设计原则**: 规范化设计 + 性能优化
- **字符集**: UTF-8
- **时区**: UTC (统一协调时间)
- **备份策略**: 每日全量备份 + 实时增量备份

### 架构特点
- **主从复制**: 读写分离，提升性能
- **分区表**: 按时间分区，优化查询性能
- **索引优化**: 针对查询模式优化索引
- **数据加密**: 敏感数据字段加密存储
- **审计日志**: 完整的数据变更记录

## 🗄️ 核心表结构

### 1. 用户表 (users)
```sql
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    salt VARCHAR(255) NOT NULL,
    
    -- 基本信息
    display_name VARCHAR(100),
    avatar_url TEXT,
    bio TEXT,
    timezone VARCHAR(50) DEFAULT 'UTC',
    language VARCHAR(10) DEFAULT 'zh-CN',
    
    -- 状态信息
    status VARCHAR(20) DEFAULT 'active', -- active, inactive, suspended
    email_verified BOOLEAN DEFAULT FALSE,
    phone VARCHAR(20),
    phone_verified BOOLEAN DEFAULT FALSE,
    
    -- 统计信息
    total_records INTEGER DEFAULT 0,
    consecutive_days INTEGER DEFAULT 0,
    longest_streak INTEGER DEFAULT 0,
    total_words INTEGER DEFAULT 0,
    
    -- 时间戳
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP WITH TIME ZONE,
    deleted_at TIMESTAMP WITH TIME ZONE
);

-- 索引
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_created_at ON users(created_at);
```

### 2. 内容表 (contents)
```sql
CREATE TABLE contents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- 内容基本信息
    type VARCHAR(20) NOT NULL, -- diary, note, task, contact, goal, travel
    title VARCHAR(200),
    content TEXT NOT NULL,
    content_encrypted TEXT, -- 加密内容
    
    -- 情感和标签
    mood INTEGER CHECK (mood >= 1 AND mood <= 5),
    tags TEXT[], -- PostgreSQL数组类型
    
    -- 位置信息
    location_latitude DECIMAL(10, 8),
    location_longitude DECIMAL(11, 8),
    location_address TEXT,
    
    -- AI相关
    ai_insights TEXT,
    ai_tags TEXT[],
    ai_summary TEXT,
    ai_help_count INTEGER DEFAULT 0,
    
    -- 统计信息
    word_count INTEGER DEFAULT 0,
    character_count INTEGER DEFAULT 0,
    read_count INTEGER DEFAULT 0,
    edit_count INTEGER DEFAULT 0,
    
    -- 状态信息
    status VARCHAR(20) DEFAULT 'active', -- active, archived, deleted
    is_favorite BOOLEAN DEFAULT FALSE,
    is_private BOOLEAN DEFAULT FALSE,
    
    -- 时间戳
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
) PARTITION BY RANGE (created_at);

-- 创建分区表 (按月分区)
CREATE TABLE contents_2024_01 PARTITION OF contents
    FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');
CREATE TABLE contents_2024_02 PARTITION OF contents
    FOR VALUES FROM ('2024-02-01') TO ('2024-03-01');
-- ... 继续创建其他月份的分区

-- 索引
CREATE INDEX idx_contents_user_id ON contents(user_id);
CREATE INDEX idx_contents_type ON contents(type);
CREATE INDEX idx_contents_mood ON contents(mood);
CREATE INDEX idx_contents_tags ON contents USING GIN(tags);
CREATE INDEX idx_contents_created_at ON contents(created_at);
CREATE INDEX idx_contents_status ON contents(status);
```

### 3. 人际关系表 (relationships)
```sql
CREATE TABLE relationships (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- 联系人基本信息
    name VARCHAR(100) NOT NULL,
    avatar_url TEXT,
    email VARCHAR(255),
    phone VARCHAR(20),
    
    -- 关系信息
    relationship_type VARCHAR(20) NOT NULL, -- family, friend, colleague, other
    intimacy_score INTEGER DEFAULT 50 CHECK (intimacy_score >= 0 AND intimacy_score <= 100),
    
    -- 详细信息
    bio TEXT,
    birthday DATE,
    company VARCHAR(100),
    position VARCHAR(100),
    address TEXT,
    
    -- 社交媒体
    social_media JSONB, -- 存储各种社交媒体账号
    
    -- 统计信息
    total_interactions INTEGER DEFAULT 0,
    last_interaction_at TIMESTAMP WITH TIME ZONE,
    interaction_frequency DECIMAL(5,2), -- 平均交互间隔天数
    
    -- 标签和备注
    tags TEXT[],
    notes TEXT,
    
    -- 状态
    status VARCHAR(20) DEFAULT 'active',
    is_favorite BOOLEAN DEFAULT FALSE,
    
    -- 时间戳
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

-- 索引
CREATE INDEX idx_relationships_user_id ON relationships(user_id);
CREATE INDEX idx_relationships_type ON relationships(relationship_type);
CREATE INDEX idx_relationships_intimacy ON relationships(intimacy_score);
CREATE INDEX idx_relationships_tags ON relationships USING GIN(tags);
```

### 4. 交互记录表 (interactions)
```sql
CREATE TABLE interactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    relationship_id UUID NOT NULL REFERENCES relationships(id) ON DELETE CASCADE,
    
    -- 交互信息
    interaction_type VARCHAR(20) NOT NULL, -- meeting, call, message, email, social
    title VARCHAR(200),
    description TEXT,
    
    -- 情感和评价
    mood INTEGER CHECK (mood >= 1 AND mood <= 5),
    quality_rating INTEGER CHECK (quality_rating >= 1 AND quality_rating <= 5),
    
    -- 位置和时间
    location TEXT,
    duration_minutes INTEGER,
    interaction_date TIMESTAMP WITH TIME ZONE NOT NULL,
    
    -- 媒体附件
    attachments JSONB, -- 存储附件信息
    
    -- 时间戳
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
) PARTITION BY RANGE (interaction_date);

-- 创建分区表
CREATE TABLE interactions_2024_q1 PARTITION OF interactions
    FOR VALUES FROM ('2024-01-01') TO ('2024-04-01');
CREATE TABLE interactions_2024_q2 PARTITION OF interactions
    FOR VALUES FROM ('2024-04-01') TO ('2024-07-01');

-- 索引
CREATE INDEX idx_interactions_user_id ON interactions(user_id);
CREATE INDEX idx_interactions_relationship_id ON interactions(relationship_id);
CREATE INDEX idx_interactions_date ON interactions(interaction_date);
CREATE INDEX idx_interactions_type ON interactions(interaction_type);
```

### 5. 提醒事项表 (reminders)
```sql
CREATE TABLE reminders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    relationship_id UUID REFERENCES relationships(id) ON DELETE CASCADE,
    
    -- 提醒信息
    title VARCHAR(200) NOT NULL,
    description TEXT,
    reminder_type VARCHAR(20) NOT NULL, -- birthday, anniversary, contact, meeting, custom
    
    -- 时间设置
    due_date TIMESTAMP WITH TIME ZONE NOT NULL,
    advance_notice_days INTEGER DEFAULT 1,
    
    -- 重复设置
    is_recurring BOOLEAN DEFAULT FALSE,
    recurrence_pattern VARCHAR(20), -- daily, weekly, monthly, yearly
    recurrence_interval INTEGER DEFAULT 1,
    
    -- 优先级和状态
    priority VARCHAR(10) DEFAULT 'medium', -- low, medium, high, urgent
    status VARCHAR(20) DEFAULT 'pending', -- pending, completed, cancelled, snoozed
    
    -- 完成信息
    completed_at TIMESTAMP WITH TIME ZONE,
    completion_notes TEXT,
    
    -- 时间戳
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 索引
CREATE INDEX idx_reminders_user_id ON reminders(user_id);
CREATE INDEX idx_reminders_due_date ON reminders(due_date);
CREATE INDEX idx_reminders_status ON reminders(status);
CREATE INDEX idx_reminders_priority ON reminders(priority);
```

### 6. AI配置表 (ai_settings)
```sql
CREATE TABLE ai_settings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- AI个性设置
    personality VARCHAR(20) DEFAULT 'friendly', -- friendly, professional, creative, humorous
    voice_type VARCHAR(20) DEFAULT 'gentle', -- gentle, energetic, calm
    language_style VARCHAR(20) DEFAULT 'casual', -- casual, formal
    
    -- 功能开关
    smart_suggestions BOOLEAN DEFAULT TRUE,
    emotion_analysis BOOLEAN DEFAULT TRUE,
    auto_tags BOOLEAN DEFAULT TRUE,
    smart_reminders BOOLEAN DEFAULT FALSE,
    proactive_mode BOOLEAN DEFAULT TRUE,
    
    -- 交互设置
    response_frequency INTEGER DEFAULT 3 CHECK (response_frequency >= 1 AND response_frequency <= 5),
    context_memory_days INTEGER DEFAULT 30,
    
    -- 隐私设置
    data_encryption BOOLEAN DEFAULT TRUE,
    learning_memory BOOLEAN DEFAULT TRUE,
    analytics_tracking BOOLEAN DEFAULT FALSE,
    crash_reporting BOOLEAN DEFAULT TRUE,
    
    -- 高级设置
    custom_prompts JSONB,
    feature_flags JSONB,
    
    -- 时间戳
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 索引
CREATE UNIQUE INDEX idx_ai_settings_user_id ON ai_settings(user_id);
```

### 7. AI交互记录表 (ai_interactions)
```sql
CREATE TABLE ai_interactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- 交互信息
    interaction_type VARCHAR(20) NOT NULL, -- chat, suggestion, analysis, generation
    user_message TEXT,
    ai_response TEXT,
    
    -- 上下文信息
    context_data JSONB,
    content_id UUID REFERENCES contents(id),
    
    -- 质量评价
    user_rating INTEGER CHECK (user_rating >= 1 AND user_rating <= 5),
    feedback TEXT,
    
    -- 技术信息
    model_version VARCHAR(50),
    processing_time_ms INTEGER,
    token_count INTEGER,
    
    -- 时间戳
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
) PARTITION BY RANGE (created_at);

-- 创建分区表
CREATE TABLE ai_interactions_2024_01 PARTITION OF ai_interactions
    FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');

-- 索引
CREATE INDEX idx_ai_interactions_user_id ON ai_interactions(user_id);
CREATE INDEX idx_ai_interactions_type ON ai_interactions(interaction_type);
CREATE INDEX idx_ai_interactions_created_at ON ai_interactions(created_at);
```

### 8. 用户活跃度表 (user_activity)
```sql
CREATE TABLE user_activity (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- 活跃度信息
    activity_date DATE NOT NULL,
    activity_level INTEGER DEFAULT 0 CHECK (activity_level >= 0 AND activity_level <= 4),
    
    -- 详细统计
    content_created INTEGER DEFAULT 0,
    words_written INTEGER DEFAULT 0,
    ai_interactions INTEGER DEFAULT 0,
    relationships_updated INTEGER DEFAULT 0,
    
    -- 时间分布
    morning_activity INTEGER DEFAULT 0,
    afternoon_activity INTEGER DEFAULT 0,
    evening_activity INTEGER DEFAULT 0,
    night_activity INTEGER DEFAULT 0,
    
    -- 情感统计
    average_mood DECIMAL(3,2),
    mood_variance DECIMAL(3,2),
    
    -- 时间戳
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(user_id, activity_date)
) PARTITION BY RANGE (activity_date);

-- 创建分区表
CREATE TABLE user_activity_2024 PARTITION OF user_activity
    FOR VALUES FROM ('2024-01-01') TO ('2025-01-01');

-- 索引
CREATE INDEX idx_user_activity_user_date ON user_activity(user_id, activity_date);
CREATE INDEX idx_user_activity_level ON user_activity(activity_level);
```

### 9. 成就系统表 (achievements)
```sql
CREATE TABLE achievements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- 成就信息
    achievement_key VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    icon_url TEXT,
    
    -- 成就类型和难度
    category VARCHAR(20) NOT NULL, -- writing, consistency, social, exploration
    difficulty VARCHAR(10) NOT NULL, -- easy, medium, hard, legendary
    points INTEGER DEFAULT 0,
    
    -- 解锁条件
    unlock_criteria JSONB NOT NULL,
    
    -- 状态
    is_active BOOLEAN DEFAULT TRUE,
    
    -- 时间戳
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 用户成就表
CREATE TABLE user_achievements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    achievement_id UUID NOT NULL REFERENCES achievements(id) ON DELETE CASCADE,
    
    -- 解锁信息
    unlocked_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    progress_data JSONB,
    
    UNIQUE(user_id, achievement_id)
);

-- 索引
CREATE INDEX idx_achievements_category ON achievements(category);
CREATE INDEX idx_user_achievements_user_id ON user_achievements(user_id);
CREATE INDEX idx_user_achievements_unlocked_at ON user_achievements(unlocked_at);
```

### 10. 系统日志表 (system_logs)
```sql
CREATE TABLE system_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- 日志信息
    log_level VARCHAR(10) NOT NULL, -- DEBUG, INFO, WARN, ERROR, FATAL
    message TEXT NOT NULL,
    
    -- 上下文信息
    user_id UUID REFERENCES users(id),
    session_id VARCHAR(100),
    ip_address INET,
    user_agent TEXT,
    
    -- 请求信息
    request_method VARCHAR(10),
    request_url TEXT,
    request_body TEXT,
    response_status INTEGER,
    response_time_ms INTEGER,
    
    -- 错误信息
    error_code VARCHAR(50),
    stack_trace TEXT,
    
    -- 时间戳
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
) PARTITION BY RANGE (created_at);

-- 创建分区表 (按周分区，便于日志清理)
CREATE TABLE system_logs_2024_w01 PARTITION OF system_logs
    FOR VALUES FROM ('2024-01-01') TO ('2024-01-08');

-- 索引
CREATE INDEX idx_system_logs_level ON system_logs(log_level);
CREATE INDEX idx_system_logs_user_id ON system_logs(user_id);
CREATE INDEX idx_system_logs_created_at ON system_logs(created_at);
```

## 🔗 关系图

### 核心实体关系
```
Users (用户)
├── Contents (内容) [1:N]
├── Relationships (人际关系) [1:N]
├── AI_Settings (AI配置) [1:1]
├── User_Activity (活跃度) [1:N]
├── User_Achievements (成就) [1:N]
└── AI_Interactions (AI交互) [1:N]

Relationships (人际关系)
├── Interactions (交互记录) [1:N]
└── Reminders (提醒事项) [1:N]

Contents (内容)
└── AI_Interactions (AI交互) [1:N]
```

## 📊 视图和存储过程

### 1. 用户统计视图
```sql
CREATE VIEW user_stats_view AS
SELECT 
    u.id,
    u.display_name,
    u.total_records,
    u.consecutive_days,
    u.longest_streak,
    COUNT(c.id) as actual_content_count,
    AVG(c.mood) as average_mood,
    SUM(c.word_count) as total_words,
    COUNT(DISTINCT DATE(c.created_at)) as active_days,
    MAX(c.created_at) as last_content_date
FROM users u
LEFT JOIN contents c ON u.id = c.user_id AND c.status = 'active'
GROUP BY u.id, u.display_name, u.total_records, u.consecutive_days, u.longest_streak;
```

### 2. 活跃度热力图视图
```sql
CREATE VIEW activity_heatmap_view AS
SELECT 
    user_id,
    activity_date,
    activity_level,
    content_created,
    CASE 
        WHEN activity_level = 0 THEN 'no-activity'
        WHEN activity_level = 1 THEN 'low'
        WHEN activity_level = 2 THEN 'medium'
        WHEN activity_level = 3 THEN 'high'
        WHEN activity_level = 4 THEN 'very-high'
    END as activity_label
FROM user_activity
WHERE activity_date >= CURRENT_DATE - INTERVAL '84 days' -- 12周
ORDER BY user_id, activity_date;
```

### 3. 更新用户统计存储过程
```sql
CREATE OR REPLACE FUNCTION update_user_stats(p_user_id UUID)
RETURNS VOID AS $$
DECLARE
    v_total_records INTEGER;
    v_total_words INTEGER;
    v_consecutive_days INTEGER;
    v_longest_streak INTEGER;
BEGIN
    -- 计算总记录数
    SELECT COUNT(*) INTO v_total_records
    FROM contents 
    WHERE user_id = p_user_id AND status = 'active';
    
    -- 计算总字数
    SELECT COALESCE(SUM(word_count), 0) INTO v_total_words
    FROM contents 
    WHERE user_id = p_user_id AND status = 'active';
    
    -- 计算连续天数和最长连续记录
    WITH daily_activity AS (
        SELECT DISTINCT DATE(created_at) as activity_date
        FROM contents 
        WHERE user_id = p_user_id AND status = 'active'
        ORDER BY activity_date DESC
    ),
    consecutive_calc AS (
        SELECT 
            activity_date,
            ROW_NUMBER() OVER (ORDER BY activity_date DESC) as rn,
            activity_date - INTERVAL '1 day' * (ROW_NUMBER() OVER (ORDER BY activity_date DESC) - 1) as group_date
        FROM daily_activity
    ),
    streaks AS (
        SELECT 
            group_date,
            COUNT(*) as streak_length
        FROM consecutive_calc
        GROUP BY group_date
    )
    SELECT 
        CASE WHEN MAX(activity_date) = CURRENT_DATE THEN 
            (SELECT streak_length FROM streaks ORDER BY group_date DESC LIMIT 1)
        ELSE 0 END,
        COALESCE(MAX(streak_length), 0)
    INTO v_consecutive_days, v_longest_streak
    FROM daily_activity, streaks;
    
    -- 更新用户统计
    UPDATE users 
    SET 
        total_records = v_total_records,
        total_words = v_total_words,
        consecutive_days = v_consecutive_days,
        longest_streak = v_longest_streak,
        updated_at = CURRENT_TIMESTAMP
    WHERE id = p_user_id;
END;
$$ LANGUAGE plpgsql;
```

## 🔧 数据库配置和优化

### 1. PostgreSQL配置优化
```sql
-- 性能相关配置
ALTER SYSTEM SET shared_buffers = '256MB';
ALTER SYSTEM SET effective_cache_size = '1GB';
ALTER SYSTEM SET maintenance_work_mem = '64MB';
ALTER SYSTEM SET checkpoint_completion_target = 0.9;
ALTER SYSTEM SET wal_buffers = '16MB';
ALTER SYSTEM SET default_statistics_target = 100;

-- 连接相关配置
ALTER SYSTEM SET max_connections = 200;
ALTER SYSTEM SET shared_preload_libraries = 'pg_stat_statements';

-- 重新加载配置
SELECT pg_reload_conf();
```

### 2. 自动化任务
```sql
-- 创建定时任务函数
CREATE OR REPLACE FUNCTION daily_maintenance()
RETURNS VOID AS $$
BEGIN
    -- 更新所有用户统计
    PERFORM update_user_stats(id) FROM users WHERE status = 'active';
    
    -- 清理过期日志 (保留30天)
    DELETE FROM system_logs 
    WHERE created_at < CURRENT_TIMESTAMP - INTERVAL '30 days';
    
    -- 更新活跃度数据
    INSERT INTO user_activity (user_id, activity_date, activity_level, content_created, words_written)
    SELECT 
        user_id,
        CURRENT_DATE - 1,
        CASE 
            WHEN COUNT(*) = 0 THEN 0
            WHEN COUNT(*) <= 2 THEN 1
            WHEN COUNT(*) <= 5 THEN 2
            WHEN COUNT(*) <= 10 THEN 3
            ELSE 4
        END,
        COUNT(*),
        COALESCE(SUM(word_count), 0)
    FROM contents 
    WHERE DATE(created_at) = CURRENT_DATE - 1
    GROUP BY user_id
    ON CONFLICT (user_id, activity_date) DO UPDATE SET
        activity_level = EXCLUDED.activity_level,
        content_created = EXCLUDED.content_created,
        words_written = EXCLUDED.words_written,
        updated_at = CURRENT_TIMESTAMP;
END;
$$ LANGUAGE plpgsql;
```

## 🔒 数据安全和加密

### 1. 敏感数据加密
```sql
-- 创建加密扩展
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- 加密函数
CREATE OR REPLACE FUNCTION encrypt_sensitive_data(data TEXT, key TEXT)
RETURNS TEXT AS $$
BEGIN
    RETURN encode(encrypt(data::bytea, key::bytea, 'aes'), 'base64');
END;
$$ LANGUAGE plpgsql;

-- 解密函数
CREATE OR REPLACE FUNCTION decrypt_sensitive_data(encrypted_data TEXT, key TEXT)
RETURNS TEXT AS $$
BEGIN
    RETURN convert_from(decrypt(decode(encrypted_data, 'base64'), key::bytea, 'aes'), 'UTF8');
END;
$$ LANGUAGE plpgsql;

-- 触发器：自动加密内容
CREATE OR REPLACE FUNCTION encrypt_content_trigger()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.is_private = TRUE THEN
        NEW.content_encrypted = encrypt_sensitive_data(NEW.content, 'user_encryption_key');
        NEW.content = '[ENCRYPTED]';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER encrypt_content_before_insert
    BEFORE INSERT OR UPDATE ON contents
    FOR EACH ROW
    EXECUTE FUNCTION encrypt_content_trigger();
```

### 2. 行级安全策略 (RLS)
```sql
-- 启用行级安全
ALTER TABLE contents ENABLE ROW LEVEL SECURITY;
ALTER TABLE relationships ENABLE ROW LEVEL SECURITY;
ALTER TABLE reminders ENABLE ROW LEVEL SECURITY;
ALTER TABLE ai_interactions ENABLE ROW LEVEL SECURITY;

-- 创建安全策略
CREATE POLICY user_content_policy ON contents
    FOR ALL TO authenticated_users
    USING (user_id = current_user_id());

CREATE POLICY user_relationships_policy ON relationships
    FOR ALL TO authenticated_users
    USING (user_id = current_user_id());

-- 创建安全函数
CREATE OR REPLACE FUNCTION current_user_id()
RETURNS UUID AS $$
BEGIN
    RETURN current_setting('app.current_user_id')::UUID;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;
```

## 📈 性能优化策略

### 1. 分区管理
```sql
-- 自动创建分区的函数
CREATE OR REPLACE FUNCTION create_monthly_partitions(table_name TEXT, start_date DATE, end_date DATE)
RETURNS VOID AS $$
DECLARE
    current_date DATE := start_date;
    next_date DATE;
    partition_name TEXT;
BEGIN
    WHILE current_date < end_date LOOP
        next_date := current_date + INTERVAL '1 month';
        partition_name := table_name || '_' || to_char(current_date, 'YYYY_MM');

        EXECUTE format('CREATE TABLE IF NOT EXISTS %I PARTITION OF %I
                       FOR VALUES FROM (%L) TO (%L)',
                       partition_name, table_name, current_date, next_date);

        current_date := next_date;
    END LOOP;
END;
$$ LANGUAGE plpgsql;

-- 创建未来6个月的分区
SELECT create_monthly_partitions('contents', CURRENT_DATE, CURRENT_DATE + INTERVAL '6 months');
SELECT create_monthly_partitions('ai_interactions', CURRENT_DATE, CURRENT_DATE + INTERVAL '6 months');
```

### 2. 索引优化
```sql
-- 复合索引
CREATE INDEX idx_contents_user_type_date ON contents(user_id, type, created_at DESC);
CREATE INDEX idx_contents_user_mood_date ON contents(user_id, mood, created_at DESC);

-- 部分索引
CREATE INDEX idx_contents_active_recent ON contents(user_id, created_at DESC)
    WHERE status = 'active' AND created_at > CURRENT_DATE - INTERVAL '30 days';

-- 表达式索引
CREATE INDEX idx_contents_search ON contents USING gin(to_tsvector('chinese', title || ' ' || content))
    WHERE status = 'active';

-- 覆盖索引
CREATE INDEX idx_contents_list_cover ON contents(user_id, type, created_at DESC)
    INCLUDE (title, mood, word_count, tags);
```

### 3. 查询优化
```sql
-- 创建物化视图
CREATE MATERIALIZED VIEW user_monthly_stats AS
SELECT
    user_id,
    DATE_TRUNC('month', created_at) as month,
    COUNT(*) as content_count,
    AVG(mood) as avg_mood,
    SUM(word_count) as total_words,
    COUNT(DISTINCT type) as content_types
FROM contents
WHERE status = 'active'
GROUP BY user_id, DATE_TRUNC('month', created_at);

-- 创建唯一索引
CREATE UNIQUE INDEX idx_user_monthly_stats ON user_monthly_stats(user_id, month);

-- 定期刷新物化视图
CREATE OR REPLACE FUNCTION refresh_monthly_stats()
RETURNS VOID AS $$
BEGIN
    REFRESH MATERIALIZED VIEW CONCURRENTLY user_monthly_stats;
END;
$$ LANGUAGE plpgsql;
```

## 🔄 数据迁移和版本控制

### 1. 数据库版本管理
```sql
-- 版本控制表
CREATE TABLE schema_migrations (
    version VARCHAR(20) PRIMARY KEY,
    description TEXT,
    applied_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    rollback_sql TEXT
);

-- 迁移示例
INSERT INTO schema_migrations (version, description, rollback_sql) VALUES
('2024.03.001', '创建基础表结构', 'DROP TABLE IF EXISTS users, contents, relationships CASCADE;'),
('2024.03.002', '添加AI配置表', 'DROP TABLE IF EXISTS ai_settings CASCADE;'),
('2024.03.003', '添加成就系统', 'DROP TABLE IF EXISTS achievements, user_achievements CASCADE;');
```

### 2. 数据迁移脚本
```sql
-- 从旧版本迁移数据
CREATE OR REPLACE FUNCTION migrate_from_v1()
RETURNS VOID AS $$
BEGIN
    -- 迁移用户数据
    INSERT INTO users (id, email, username, display_name, created_at)
    SELECT
        gen_random_uuid(),
        email,
        COALESCE(username, split_part(email, '@', 1)),
        COALESCE(name, split_part(email, '@', 1)),
        created_at
    FROM old_users
    ON CONFLICT (email) DO NOTHING;

    -- 迁移内容数据
    INSERT INTO contents (user_id, type, title, content, mood, created_at)
    SELECT
        u.id,
        CASE
            WHEN oc.category = 'diary' THEN 'diary'
            WHEN oc.category = 'note' THEN 'note'
            ELSE 'diary'
        END,
        oc.title,
        oc.content,
        oc.mood,
        oc.created_at
    FROM old_contents oc
    JOIN users u ON u.email = oc.user_email;

    -- 更新用户统计
    PERFORM update_user_stats(id) FROM users;
END;
$$ LANGUAGE plpgsql;
```

## 🔍 监控和维护

### 1. 性能监控
```sql
-- 慢查询监控视图
CREATE VIEW slow_queries AS
SELECT
    query,
    calls,
    total_time,
    mean_time,
    rows,
    100.0 * shared_blks_hit / nullif(shared_blks_hit + shared_blks_read, 0) AS hit_percent
FROM pg_stat_statements
WHERE mean_time > 100  -- 超过100ms的查询
ORDER BY mean_time DESC;

-- 表大小监控
CREATE VIEW table_sizes AS
SELECT
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) as size,
    pg_total_relation_size(schemaname||'.'||tablename) as size_bytes
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY size_bytes DESC;

-- 索引使用情况
CREATE VIEW index_usage AS
SELECT
    schemaname,
    tablename,
    indexname,
    idx_tup_read,
    idx_tup_fetch,
    idx_scan,
    pg_size_pretty(pg_relation_size(indexrelid)) as size
FROM pg_stat_user_indexes
ORDER BY idx_scan DESC;
```

### 2. 自动维护任务
```sql
-- 自动清理和优化
CREATE OR REPLACE FUNCTION auto_maintenance()
RETURNS VOID AS $$
BEGIN
    -- 清理过期数据
    DELETE FROM system_logs WHERE created_at < CURRENT_TIMESTAMP - INTERVAL '30 days';
    DELETE FROM ai_interactions WHERE created_at < CURRENT_TIMESTAMP - INTERVAL '90 days';

    -- 更新表统计信息
    ANALYZE;

    -- 重建索引（如果需要）
    REINDEX INDEX CONCURRENTLY idx_contents_user_type_date;

    -- 清理无用的AI交互记录
    DELETE FROM ai_interactions
    WHERE user_rating IS NULL
    AND created_at < CURRENT_TIMESTAMP - INTERVAL '7 days'
    AND interaction_type = 'chat';

    -- 压缩表空间
    VACUUM (ANALYZE, VERBOSE);
END;
$$ LANGUAGE plpgsql;
```

## 🔐 备份和恢复策略

### 1. 备份策略
```bash
#!/bin/bash
# 数据库备份脚本

DB_NAME="ai_diary_app"
BACKUP_DIR="/var/backups/postgresql"
DATE=$(date +%Y%m%d_%H%M%S)

# 全量备份
pg_dump -h localhost -U postgres -d $DB_NAME \
    --format=custom \
    --compress=9 \
    --file="$BACKUP_DIR/full_backup_$DATE.dump"

# 仅数据备份
pg_dump -h localhost -U postgres -d $DB_NAME \
    --data-only \
    --format=custom \
    --compress=9 \
    --file="$BACKUP_DIR/data_backup_$DATE.dump"

# 仅结构备份
pg_dump -h localhost -U postgres -d $DB_NAME \
    --schema-only \
    --format=custom \
    --file="$BACKUP_DIR/schema_backup_$DATE.dump"

# 清理7天前的备份
find $BACKUP_DIR -name "*.dump" -mtime +7 -delete
```

### 2. 恢复策略
```bash
#!/bin/bash
# 数据库恢复脚本

DB_NAME="ai_diary_app"
BACKUP_FILE=$1

if [ -z "$BACKUP_FILE" ]; then
    echo "Usage: $0 <backup_file>"
    exit 1
fi

# 创建恢复数据库
createdb -h localhost -U postgres "${DB_NAME}_restore"

# 恢复数据
pg_restore -h localhost -U postgres -d "${DB_NAME}_restore" \
    --verbose \
    --clean \
    --if-exists \
    "$BACKUP_FILE"

echo "Database restored to ${DB_NAME}_restore"
```

## 📊 数据分析查询示例

### 1. 用户行为分析
```sql
-- 用户活跃度分析
WITH user_activity_stats AS (
    SELECT
        user_id,
        COUNT(*) as total_days,
        AVG(activity_level) as avg_activity,
        MAX(activity_level) as max_activity,
        COUNT(*) FILTER (WHERE activity_level > 0) as active_days
    FROM user_activity
    WHERE activity_date >= CURRENT_DATE - INTERVAL '30 days'
    GROUP BY user_id
)
SELECT
    u.display_name,
    uas.total_days,
    uas.active_days,
    ROUND(uas.avg_activity, 2) as avg_activity,
    ROUND(100.0 * uas.active_days / uas.total_days, 1) as activity_rate
FROM user_activity_stats uas
JOIN users u ON u.id = uas.user_id
ORDER BY activity_rate DESC;

-- 内容类型偏好分析
SELECT
    u.display_name,
    c.type,
    COUNT(*) as count,
    AVG(c.mood) as avg_mood,
    SUM(c.word_count) as total_words
FROM contents c
JOIN users u ON u.id = c.user_id
WHERE c.created_at >= CURRENT_DATE - INTERVAL '30 days'
    AND c.status = 'active'
GROUP BY u.id, u.display_name, c.type
ORDER BY u.display_name, count DESC;
```

### 2. AI使用情况分析
```sql
-- AI功能使用统计
SELECT
    interaction_type,
    COUNT(*) as usage_count,
    AVG(user_rating) as avg_rating,
    COUNT(*) FILTER (WHERE user_rating >= 4) as positive_ratings,
    AVG(processing_time_ms) as avg_processing_time
FROM ai_interactions
WHERE created_at >= CURRENT_DATE - INTERVAL '7 days'
GROUP BY interaction_type
ORDER BY usage_count DESC;

-- 用户AI依赖度分析
WITH user_ai_usage AS (
    SELECT
        user_id,
        COUNT(*) as ai_interactions,
        COUNT(DISTINCT DATE(created_at)) as ai_active_days
    FROM ai_interactions
    WHERE created_at >= CURRENT_DATE - INTERVAL '30 days'
    GROUP BY user_id
),
user_content_stats AS (
    SELECT
        user_id,
        COUNT(*) as content_created,
        AVG(ai_help_count) as avg_ai_help
    FROM contents
    WHERE created_at >= CURRENT_DATE - INTERVAL '30 days'
        AND status = 'active'
    GROUP BY user_id
)
SELECT
    u.display_name,
    COALESCE(uau.ai_interactions, 0) as ai_interactions,
    COALESCE(ucs.content_created, 0) as content_created,
    COALESCE(ucs.avg_ai_help, 0) as avg_ai_help_per_content,
    CASE
        WHEN ucs.content_created > 0 THEN
            ROUND(100.0 * uau.ai_interactions / ucs.content_created, 1)
        ELSE 0
    END as ai_dependency_ratio
FROM users u
LEFT JOIN user_ai_usage uau ON u.id = uau.user_id
LEFT JOIN user_content_stats ucs ON u.id = ucs.user_id
WHERE u.status = 'active'
ORDER BY ai_dependency_ratio DESC;
```

---

**文档版本**: v1.0
**最后更新**: 2024年3月
**维护者**: AI日记App数据库团队

## 📞 技术支持

### 数据库管理联系方式
- **数据库架构师**: [联系邮箱]
- **DBA团队**: [联系邮箱]
- **数据安全专家**: [联系邮箱]

### 相关资源
- **数据库监控面板**: [监控链接]
- **备份状态检查**: [备份链接]
- **性能分析工具**: [分析链接]
- **数据字典**: [字典链接]

### 紧急联系
- **24/7技术支持**: [紧急电话]
- **数据恢复专线**: [恢复电话]
- **安全事件响应**: [安全电话]

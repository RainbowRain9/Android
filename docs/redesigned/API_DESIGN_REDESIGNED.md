# AI日记App - 重新设计版本 API设计文档

## 📋 API概述

### 基本信息
- **API版本**: v2.0 (重新设计版本)
- **基础URL**: `https://api.ai-diary.app/v2`
- **认证方式**: JWT Token
- **数据格式**: JSON
- **字符编码**: UTF-8

### 设计原则
- **RESTful设计**: 遵循REST架构风格
- **统一响应格式**: 所有API返回统一的响应结构
- **版本控制**: 通过URL路径进行版本控制
- **错误处理**: 标准化的错误码和错误信息
- **安全性**: 所有敏感操作需要认证和授权

## 🔐 认证系统

### 用户认证
```http
POST /auth/login
Content-Type: application/json

{
    "email": "user@example.com",
    "password": "password123"
}
```

**响应示例**:
```json
{
    "success": true,
    "data": {
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        "refreshToken": "refresh_token_here",
        "expiresIn": 3600,
        "user": {
            "id": "user_001",
            "name": "张小明",
            "email": "user@example.com",
            "avatar": "https://cdn.example.com/avatar.jpg"
        }
    },
    "message": "登录成功"
}
```

### Token刷新
```http
POST /auth/refresh
Authorization: Bearer {refresh_token}
```

## 👤 用户管理API

### 获取用户信息
```http
GET /users/profile
Authorization: Bearer {access_token}
```

**响应示例**:
```json
{
    "success": true,
    "data": {
        "id": "user_001",
        "name": "张小明",
        "email": "user@example.com",
        "avatar": "https://cdn.example.com/avatar.jpg",
        "joinDate": "2024-01-01T00:00:00Z",
        "stats": {
            "totalRecords": 156,
            "consecutiveDays": 23,
            "averageMood": 4.2,
            "totalWords": 45678
        },
        "achievements": [
            {
                "id": "continuous_writer",
                "name": "连续记录者",
                "description": "连续记录21天",
                "unlockedAt": "2024-03-01T00:00:00Z"
            }
        ]
    }
}
```

### 更新用户信息
```http
PUT /users/profile
Authorization: Bearer {access_token}
Content-Type: application/json

{
    "name": "新名字",
    "avatar": "new_avatar_url"
}
```

## 📝 内容管理API

### 获取内容列表
```http
GET /content?type=diary&page=1&limit=20&sort=createdAt&order=desc
Authorization: Bearer {access_token}
```

**查询参数**:
- `type`: 内容类型 (diary, note, task, contact, goal, travel)
- `page`: 页码 (默认: 1)
- `limit`: 每页数量 (默认: 20, 最大: 100)
- `sort`: 排序字段 (createdAt, updatedAt, mood)
- `order`: 排序方向 (asc, desc)
- `search`: 搜索关键词
- `tags`: 标签筛选 (逗号分隔)
- `mood`: 心情筛选 (1-5)

**响应示例**:
```json
{
    "success": true,
    "data": {
        "items": [
            {
                "id": "content_001",
                "type": "diary",
                "title": "春日漫步的思考",
                "content": "今天在公园里走了很久...",
                "mood": 4,
                "tags": ["生活感悟", "自然", "AI推荐"],
                "aiInsights": "这篇日记体现了你对自然的热爱...",
                "createdAt": "2024-03-15T20:30:00Z",
                "updatedAt": "2024-03-15T20:35:00Z",
                "stats": {
                    "wordCount": 156,
                    "readCount": 3,
                    "aiHelps": 2
                }
            }
        ],
        "pagination": {
            "page": 1,
            "limit": 20,
            "total": 156,
            "totalPages": 8
        }
    }
}
```

### 创建内容
```http
POST /content
Authorization: Bearer {access_token}
Content-Type: application/json

{
    "type": "diary",
    "title": "今天的心情",
    "content": "今天心情很好...",
    "mood": 4,
    "tags": ["心情", "生活"],
    "location": {
        "latitude": 39.9042,
        "longitude": 116.4074,
        "address": "北京市朝阳区"
    }
}
```

### 更新内容
```http
PUT /content/{id}
Authorization: Bearer {access_token}
Content-Type: application/json

{
    "title": "更新的标题",
    "content": "更新的内容...",
    "mood": 5,
    "tags": ["更新", "标签"]
}
```

### 删除内容
```http
DELETE /content/{id}
Authorization: Bearer {access_token}
```

## 🤖 AI服务API

### 获取AI写作建议
```http
POST /ai/suggestions
Authorization: Bearer {access_token}
Content-Type: application/json

{
    "type": "diary",
    "mood": 4,
    "context": "今天天气很好",
    "count": 3
}
```

**响应示例**:
```json
{
    "success": true,
    "data": {
        "suggestions": [
            {
                "id": "suggestion_001",
                "title": "🌸 春日感悟",
                "description": "记录春天带给你的美好感受和生活感悟",
                "template": "今天的阳光特别温暖...",
                "tags": ["春天", "感悟", "自然"]
            }
        ]
    }
}
```

### AI内容分析
```http
POST /ai/analyze
Authorization: Bearer {access_token}
Content-Type: application/json

{
    "content": "今天心情很好，阳光明媚，和朋友一起去了公园。",
    "analysisType": ["emotion", "tags", "insights"]
}
```

**响应示例**:
```json
{
    "success": true,
    "data": {
        "emotion": {
            "sentiment": "positive",
            "score": 0.85,
            "emotions": {
                "joy": 0.8,
                "excitement": 0.6,
                "contentment": 0.7
            }
        },
        "tags": ["心情", "朋友", "公园", "阳光"],
        "insights": [
            "你今天的社交活动让你感到快乐",
            "户外活动对你的心情有积极影响",
            "建议多安排类似的活动"
        ]
    }
}
```

### AI聊天对话
```http
POST /ai/chat
Authorization: Bearer {access_token}
Content-Type: application/json

{
    "message": "我今天不知道写什么",
    "context": {
        "personality": "friendly",
        "language": "casual",
        "recentContent": ["昨天去了图书馆", "最近在学习新技能"]
    }
}
```

**响应示例**:
```json
{
    "success": true,
    "data": {
        "response": "😊 没关系呀！有时候灵感需要一点时间。你昨天去图书馆有什么收获吗？或者可以写写你最近学习的新技能，分享一下学习过程中的感受？",
        "suggestions": [
            "写写图书馆的体验",
            "分享学习新技能的心得",
            "记录今天的小确幸"
        ]
    }
}
```

## 📊 数据分析API

### 获取用户洞察
```http
GET /insights/overview?period=30d
Authorization: Bearer {access_token}
```

**查询参数**:
- `period`: 时间范围 (7d, 30d, 90d, 1y)

**响应示例**:
```json
{
    "success": true,
    "data": {
        "summary": {
            "totalRecords": 45,
            "averageMood": 4.2,
            "mostActiveDay": "Sunday",
            "longestStreak": 15,
            "totalWords": 12345
        },
        "moodTrend": [
            {"date": "2024-03-01", "mood": 4.0},
            {"date": "2024-03-02", "mood": 4.5}
        ],
        "activityHeatmap": [
            {"date": "2024-03-01", "level": 3},
            {"date": "2024-03-02", "level": 4}
        ],
        "contentDistribution": {
            "diary": 25,
            "note": 12,
            "task": 8,
            "goal": 5,
            "contact": 3,
            "travel": 2
        },
        "aiInsights": [
            "你的写作频率在周末更高",
            "心情指数在下午时段较好",
            "建议增加目标类型的记录"
        ]
    }
}
```

### 获取活跃度数据
```http
GET /insights/activity?weeks=12
Authorization: Bearer {access_token}
```

**响应示例**:
```json
{
    "success": true,
    "data": {
        "heatmapData": [
            {
                "date": "2024-01-01",
                "level": 0,
                "count": 0,
                "details": []
            },
            {
                "date": "2024-01-02",
                "level": 3,
                "count": 2,
                "details": [
                    {"type": "diary", "title": "新年第一天"},
                    {"type": "goal", "title": "新年计划"}
                ]
            }
        ],
        "stats": {
            "activeDays": 67,
            "longestStreak": 23,
            "averageDaily": 4.2,
            "totalContributions": 156
        }
    }
}
```

## 👥 人际关系API

### 获取联系人列表
```http
GET /relationships?type=friend&sort=intimacy&order=desc
Authorization: Bearer {access_token}
```

**响应示例**:
```json
{
    "success": true,
    "data": {
        "contacts": [
            {
                "id": "contact_001",
                "name": "李雷",
                "avatar": "https://cdn.example.com/avatar1.jpg",
                "type": "friend",
                "intimacy": 85,
                "lastContact": "2024-03-15T14:30:00Z",
                "tags": ["同事", "朋友"],
                "stats": {
                    "totalInteractions": 45,
                    "averageInterval": 3.2
                }
            }
        ],
        "summary": {
            "totalContacts": 23,
            "byType": {
                "family": 5,
                "friend": 12,
                "colleague": 6
            },
            "averageIntimacy": 72
        }
    }
}
```

### 获取关系图谱数据
```http
GET /relationships/network
Authorization: Bearer {access_token}
```

**响应示例**:
```json
{
    "success": true,
    "data": {
        "nodes": [
            {
                "id": "user",
                "name": "我",
                "type": "self",
                "x": 0,
                "y": 0
            },
            {
                "id": "contact_001",
                "name": "李雷",
                "type": "friend",
                "intimacy": 85,
                "x": 100,
                "y": 50
            }
        ],
        "edges": [
            {
                "source": "user",
                "target": "contact_001",
                "strength": 85,
                "lastInteraction": "2024-03-15T14:30:00Z"
            }
        ]
    }
}
```

### 获取提醒事项
```http
GET /relationships/reminders?status=pending&priority=high
Authorization: Bearer {access_token}
```

**响应示例**:
```json
{
    "success": true,
    "data": {
        "reminders": [
            {
                "id": "reminder_001",
                "contactId": "contact_001",
                "contactName": "李雷",
                "type": "birthday",
                "title": "李雷的生日",
                "description": "记得准备生日礼物",
                "dueDate": "2024-03-16T00:00:00Z",
                "priority": "high",
                "status": "pending"
            }
        ]
    }
}
```

## ⚙️ AI配置API

### 获取AI配置
```http
GET /ai/settings
Authorization: Bearer {access_token}
```

**响应示例**:
```json
{
    "success": true,
    "data": {
        "personality": "friendly",
        "voice": "gentle",
        "language": "casual",
        "features": {
            "smartSuggestions": true,
            "emotionAnalysis": true,
            "autoTags": true,
            "smartReminders": false
        },
        "interaction": {
            "responseFrequency": 3,
            "proactiveMode": true
        },
        "privacy": {
            "dataEncryption": true,
            "learningMemory": true,
            "analyticsTracking": false
        }
    }
}
```

### 更新AI配置
```http
PUT /ai/settings
Authorization: Bearer {access_token}
Content-Type: application/json

{
    "personality": "professional",
    "features": {
        "smartSuggestions": true,
        "emotionAnalysis": false
    }
}
```

### 清除AI记忆
```http
DELETE /ai/memory
Authorization: Bearer {access_token}
```

## 📤 数据导出API

### 导出用户数据
```http
POST /export/data
Authorization: Bearer {access_token}
Content-Type: application/json

{
    "format": "json",
    "includeContent": true,
    "includeAnalytics": true,
    "dateRange": {
        "start": "2024-01-01",
        "end": "2024-03-31"
    }
}
```

**响应示例**:
```json
{
    "success": true,
    "data": {
        "exportId": "export_001",
        "status": "processing",
        "estimatedTime": 300,
        "downloadUrl": null
    }
}
```

### 获取导出状态
```http
GET /export/{exportId}/status
Authorization: Bearer {access_token}
```

## 🚨 错误处理

### 标准错误响应格式
```json
{
    "success": false,
    "error": {
        "code": "VALIDATION_ERROR",
        "message": "请求参数验证失败",
        "details": [
            {
                "field": "email",
                "message": "邮箱格式不正确"
            }
        ]
    },
    "timestamp": "2024-03-15T20:30:00Z",
    "requestId": "req_12345"
}
```

### 常见错误码
- `400 BAD_REQUEST`: 请求参数错误
- `401 UNAUTHORIZED`: 未授权访问
- `403 FORBIDDEN`: 权限不足
- `404 NOT_FOUND`: 资源不存在
- `429 RATE_LIMIT_EXCEEDED`: 请求频率超限
- `500 INTERNAL_SERVER_ERROR`: 服务器内部错误

## 📈 API使用限制

### 频率限制
- **认证用户**: 1000 请求/小时
- **AI服务**: 100 请求/小时
- **数据导出**: 5 请求/天

### 数据限制
- **单次内容长度**: 最大 10,000 字符
- **文件上传大小**: 最大 10MB
- **批量操作**: 最大 100 项

---

**文档版本**: v2.0  
**最后更新**: 2024年3月  
**维护者**: AI日记App API团队

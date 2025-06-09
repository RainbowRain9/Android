# AI日记App前后端交互与API设计

本文档旨在详细说明AI日记App中前端与后端之间的交互方式，并定义主要的API接口设计。这将基于先前文档中确定的功能需求、系统架构、数据流和UI设计。

## 一、API设计原则

1.  **RESTful架构:** API将遵循REST原则，使用标准的HTTP方法 (GET, POST, PUT, DELETE) 和状态码。
2.  **JSON数据格式:** 所有请求体和响应体都将使用JSON格式。
3.  **身份认证:** 所有需要用户身份验证的API端点将使用JWT (JSON Web Tokens) 进行保护。Token将在登录后发放，并通过Authorization HTTP头部 (Bearer Token) 在后续请求中传递。
4.  **版本控制:** API将进行版本控制 (例如: `/api/v1/...`)，以便未来的迭代和兼容性。
5.  **一致的响应结构:** 所有API响应将遵循一致的结构，例如:
    ```json
    {
      "success": true, // boolean
      "data": { ... }, // 结果数据，成功时存在
      "message": "Operation successful", // 消息文本，可选
      "error": null // 错误对象，失败时存在
    }
    // 失败时:
    {
      "success": false,
      "data": null,
      "message": "An error occurred",
      "error": {
        "code": "ERROR_CODE",
        "details": "Specific error details..."
      }
    }
    ```
6.  **错误处理:** 使用标准的HTTP状态码来指示请求结果 (例如: 200 OK, 201 Created, 400 Bad Request, 401 Unauthorized, 403 Forbidden, 404 Not Found, 500 Internal Server Error)。

## 二、API端点设计

以下是主要功能模块的API端点定义：

### 1. 认证 (Auth)

*   **`POST /api/v1/auth/register`**
    *   描述: 用户注册。
    *   请求体: `{ "email": "user@example.com", "password": "securepassword123", "nickname": "JohnDoe" }`
    *   响应体 (成功): `{ "success": true, "data": { "user": { ... }, "token": "jwt_token" } }`
*   **`POST /api/v1/auth/login`**
    *   描述: 用户登录。
    *   请求体: `{ "email": "user@example.com", "password": "securepassword123" }`
    *   响应体 (成功): `{ "success": true, "data": { "user": { ... }, "token": "jwt_token" } }`
*   **`POST /api/v1/auth/logout`**
    *   描述: 用户登出 (后端可将token加入黑名单)。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   响应体 (成功): `{ "success": true, "message": "Logged out successfully" }`
*   **`POST /api/v1/auth/refresh-token`**
    *   描述: 刷新JWT。
    *   请求体: `{ "refreshToken": "refresh_token_value" }`
    *   响应体 (成功): `{ "success": true, "data": { "token": "new_jwt_token" } }`
*   **`POST /api/v1/auth/forgot-password`**
    *   描述: 请求密码重置邮件。
    *   请求体: `{ "email": "user@example.com" }`
*   **`POST /api/v1/auth/reset-password`**
    *   描述: 使用重置令牌设置新密码。
    *   请求体: `{ "resetToken": "some_token", "newPassword": "newSecurePassword" }`

### 2. 用户 (Users)

*   **`GET /api/v1/users/me`**
    *   描述: 获取当前登录用户信息。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   响应体 (成功): `{ "success": true, "data": { "user_id": 1, "email": "...", "nickname": "...", "avatar_url": "..." } }`
*   **`PUT /api/v1/users/me`**
    *   描述: 更新当前登录用户信息 (例如昵称、头像)。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   请求体: `{ "nickname": "NewNickname", "avatar_url": "new_url" }`
    *   响应体 (成功): `{ "success": true, "data": { "user": { ... } } }`

### 3. 日记 (Diaries)

*   **`POST /api/v1/diaries`**
    *   描述: 创建新日记条目。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   请求体: `{ "title": "My Day", "content_text": "...", "content_html": "<p>...</p>", "mood": "Happy", "entry_date": "YYYY-MM-DD", "diary_type": "standard_text", "cover_image_attachment_id": 123, "background_style_json": {"type": "color", "value": "#FFFFFF"}, "custom_settings_json": {"ai_prompt_temperature": 0.7}, "sort_index": 0, "tags": ["work", "project"], "attachments": [{"file_url": "...", "file_type": "image/jpeg", "attachment_purpose": "inline_content"}] }`
    *   响应体 (成功): `{ "success": true, "data": { "diary": { "diary_id": 1, "user_id": 1, "title": "My Day", "mood": "Happy", "entry_date": "YYYY-MM-DD", "diary_type": "standard_text", "cover_image_attachment_id": 123, "background_style_json": { ... }, "custom_settings_json": { ... }, "sort_index": 0, ... } } }`
*   **`GET /api/v1/diaries`**
    *   描述: 获取用户日记列表 (支持分页、筛选、排序)。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   查询参数: `?page=1&limit=10&sort_by=entry_date&order=desc&mood=Happy&diary_type=standard_text&tag=work&date_from=YYYY-MM-DD&date_to=YYYY-MM-DD`
    *   响应体 (成功): `{ "success": true, "data": { "diaries": [{ "diary_id": 1, ..., "diary_type": "standard_text", "cover_image_attachment_id": 123, ...}, {...}], "pagination": { ... } } }`
*   **`GET /api/v1/diaries/{diary_id}`**
    *   描述: 获取单个日记条目详情。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   响应体 (成功): `{ "success": true, "data": { "diary": { "diary_id": 1, ..., "diary_type": "standard_text", "cover_image_attachment_id": 123, "background_style_json": { ... }, "custom_settings_json": { ... }, "sort_index": 0, ... } } }`
*   **`PUT /api/v1/diaries/{diary_id}`**
    *   描述: 更新指定日记条目。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   请求体: (同创建，但字段可选, 例如: `{ "title": "An Updated Day", "diary_type": "ai_reflection", "custom_settings_json": {"new_setting": true} }`)
    *   响应体 (成功): `{ "success": true, "data": { "diary": { ...updated_diary_fields... } } }`
*   **`DELETE /api/v1/diaries/{diary_id}`**
    *   描述: 删除指定日记条目。
    *   请求头: `Authorization: Bearer <jwt_token>`

### 4. 清单 (Lists & List Items)

*   **`POST /api/v1/lists`**
    *   描述: 创建新清单。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   请求体: `{ "title": "New Project Tasks", "description": "Tasks for the new project", "list_type": "goal_tracker", "target_progress": 100, "current_progress": 10, "progress_unit": "%", "custom_settings_json": {"display_mode": "compact"}, "tags": ["projectX"] }`
    *   响应体 (成功): `{ "success": true, "data": { "list": { "list_id": 1, ..., "list_type": "goal_tracker", ... } } }`
*   **`GET /api/v1/lists`**
    *   描述: 获取用户清单列表 (支持分页、筛选)。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   查询参数: `?page=1&limit=10&list_type=todo`
    *   响应体 (成功): `{ "success": true, "data": { "lists": [{ "list_id": 1, ..., "list_type": "todo", ... }, {...}], "pagination": { ... } } }`
*   **`GET /api/v1/lists/{list_id}`**
    *   描述: 获取单个清单详情及其项目。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   响应体 (成功): `{ "success": true, "data": { "list": { "list_id": 1, ..., "list_type": "goal_tracker", "target_progress": 100, "current_progress": 10, "progress_unit": "%", "custom_settings_json": { ... }, "items": [{...}] } } }`
*   **`PUT /api/v1/lists/{list_id}`**
    *   描述: 更新指定清单信息 (标题、描述、类型、进度等)。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   请求体: (同创建，但字段可选, 例如: `{ "title": "Updated Project Tasks", "current_progress": 25 }`)
    *   响应体 (成功): `{ "success": true, "data": { "list": { ...updated_list_fields... } } }`
*   **`DELETE /api/v1/lists/{list_id}`**
    *   描述: 删除指定清单及其所有项目。
    *   请求头: `Authorization: Bearer <jwt_token>`
*   **`POST /api/v1/lists/{list_id}/items`**
    *   描述: 向清单添加新项目。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   请求体: `{ "content": "Buy milk", "due_date": "YYYY-MM-DDTHH:mm:ssZ" }`
*   **`PUT /api/v1/lists/{list_id}/items/{item_id}`**
    *   描述: 更新清单项目 (内容、完成状态、截止日期)。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   请求体: `{ "content": "Buy almond milk", "is_completed": true }`
*   **`DELETE /api/v1/lists/{list_id}/items/{item_id}`**
    *   描述: 删除清单项目。
    *   请求头: `Authorization: Bearer <jwt_token>`

### 5. 标签 (Tags)

*   **`POST /api/v1/tags`**
    *   描述: 创建新标签 (用户私有)。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   请求体: `{ "name": "Personal", "color": "#3498DB" }`
*   **`GET /api/v1/tags`**
    *   描述: 获取用户的所有标签。
    *   请求头: `Authorization: Bearer <jwt_token>`
*   *(日记和清单的标签管理通过各自的端点实现，例如 `POST /diaries/{diary_id}/tags`)*

### 6. AI交互 (AI Interaction)

*   **`POST /api/v1/ai/assist/writing`**
    *   描述: 请求AI写作辅助。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   请求体: `{ "model_id": "gpt-3.5-turbo", "task_type": "sentence_completion" / "grammar_correction" / "summarize" / "style_suggestion", "context_text": "Current diary text...", "user_prompt": "Make this sound more reflective.", "related_contact_id": 1 (可选), "related_interaction_id": 5 (可选) }`
    *   响应体 (成功): `{ "success": true, "data": { "suggestion": "AI generated text..." } }`
*   **`POST /api/v1/ai/chat`**
    *   描述: 与AI进行对话式交互 (用于记录日记/清单)。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   请求体: `{ "model_id": "claude-v2", "session_id": "optional_session_id", "messages": [{"role": "user", "content": "Hello AI"}, {"role": "assistant", "content": "Hello! How can I help you today?"}, {"role": "user", "content": "Create a diary entry for today."}], "related_contact_id": 1 (可选), "related_interaction_id": 5 (可选) }`
    *   响应体 (成功): `{ "success": true, "data": { "reply": "Okay, what happened today?", "session_id": "updated_session_id" } }`
*   **`GET /api/v1/ai/models`**
    *   描述: 获取可用的AI模型列表。
    *   响应体 (成功): `{ "success": true, "data": { "models": [{ "model_id": 1, "model_name": "GPT-4", "api_provider": "OpenAI", "description": "...", "requires_user_api_key": false }, ...] } }`
*   **`PUT /api/v1/users/me/ai-config`**
    *   描述: 更新用户的AI模型偏好和API密钥。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   请求体: `{ "preferred_ai_model_id": 1, "api_keys": [{ "model_id": 2, "key": "user_provided_api_key_for_model_2" }] }` (API Key后端需加密存储)

### 7. 媒体附件 (Media Attachments)

*   **`POST /api/v1/attachments/upload`**
    *   描述: 上传媒体文件 (图片、音频)。通常使用 `multipart/form-data`。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   响应体 (成功): `{ "success": true, "data": { "attachment_id": 1, "file_url": "https://cdn.example.com/path/to/file.jpg", "file_type": "image/jpeg" } }`
    *   *注意: 此端点返回包含 `attachment_id` 的附件对象。在创建/更新日记或清单项目时，可以通过 `cover_image_attachment_id` (对于日记封面) 或在 `attachments` 数组中包含 `attachment_id` 及可选的 `attachment_purpose` 来关联预先上传的附件，或者直接在日记/清单创建时提供文件信息由后端处理上传和关联。 `attachment_purpose` 字段（如 'inline_content', 'diary_cover', 'diary_background', 'avatar'）帮助定义附件在不同上下文中的角色。*

### 8. 应用设置 (App Settings)

*   **`GET /api/v1/users/me/settings`**
    *   描述: 获取用户的应用设置。
    *   请求头: `Authorization: Bearer <jwt_token>`
*   **`PUT /api/v1/users/me/settings`**
    *   描述: 更新用户的应用设置。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   请求体: `{ "theme": "dark", "enable_daily_reminder": true, "reminder_time": "20:00" }`

## 三、关键交互流程示例

1.  **用户登录:**
    *   前端: 用户提交邮箱和密码。
    *   前端 -> 后端: `POST /api/v1/auth/login` 带凭据。
    *   后端: 验证凭据，生成JWT。
    *   后端 -> 前端: 返回用户信息和JWT。
    *   前端: 存储JWT，导航到主页。

2.  **创建日记 (带AI辅助):**
    *   前端: 用户打开新日记页面，输入标题和部分内容。
    *   前端: 用户点击"AI补全句子"按钮。
    *   前端 -> 后端: `POST /api/v1/ai/assist/writing` 带当前文本、任务类型、所选AI模型ID。
    *   后端: 获取用户AI配置 (API Key等)，调用第三方AI模型API。
    *   第三方AI -> 后端: 返回建议文本。
    *   后端 -> 前端: 返回AI建议。
    *   前端: 显示建议，用户采纳或修改。
    *   前端: 用户完成编辑，点击保存。
    *   前端 -> 后端: `POST /api/v1/diaries` 带完整日记数据 (包括AI润色后的内容)。
    *   后端: 保存日记到数据库。
    *   后端 -> 前端: 返回成功和新日记数据。

3.  **AI对话记录日记:**
    *   前端: 用户进入AI对话界面，发送消息："帮我记录今天的日记"。
    *   前端 -> 后端: `POST /api/v1/ai/chat` 带消息历史和用户当前消息。
    *   后端: 调用第三方AI模型API进行对话处理。
    *   第三方AI -> 后端: 返回AI的回复 (例如："好的，今天发生了什么特别的事情吗？")。
    *   后端 -> 前端: 返回AI回复。
    *   前端: 显示AI回复，用户继续对话，逐步提供日记内容。
    *   (对话结束) 前端: 用户确认保存，将整理好的对话内容作为日记正文。
    *   前端 -> 后端: `POST /api/v1/diaries` 带日记数据。

4.  **切换AI模型:**
    *   前端: 用户在设置中选择新的AI模型，如果需要，输入该模型的API Key。
    *   前端 -> 后端: `PUT /api/v1/users/me/ai-config` 带选择的模型ID和API Key (如有)。
    *   后端: 验证并加密存储API Key，更新用户偏好模型。
    *   后端 -> 前端: 返回成功消息。
    *   前端: 后续AI请求将使用新配置。

## 四、AI模型API集成细节

*   **后端代理:** 后端服务将作为客户端与第三方AI模型API之间的代理。这有助于：
    *   安全管理API密钥 (不暴露给客户端)。
    *   统一不同AI提供商API的调用方式和响应格式 (通过适配器模式)。
    *   记录和监控AI API使用情况。
*   **API密钥管理:** 后端必须安全存储AI模型的API密钥。用户提供的密钥应加密存储。应用自身的全局密钥应通过环境变量或安全的配置服务管理。
*   **请求与响应适配:** 不同的AI模型API有不同的请求结构和响应格式。后端需要为每个支持的AI模型实现适配器，将内部统一的AI请求转换为特定模型的格式，并将模型的响应转换回内部统一格式。

## 五、新增：人脉关系管理与情商辅助API

以下API端点用于支持新增的人脉关系管理与情商辅助功能。

### 9. 联系人 (Contacts)

*   **`POST /api/v1/contacts`**
    *   描述: 创建新的联系人档案。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   请求体: `{ "name": "李雷", "nickname": "小雷", "avatar_url": "url_to_avatar", "relation_type": "朋友", "gender": "男", "birth_date": "1990-05-15", "contact_info_json": {"phone": "138..."}, "interests_hobbies_text": "喜欢篮球、音乐", "preferences_text": "不吃香菜", "important_dates_json": [{"name": "相识纪念日", "date": "2020-10-01"}], "notes": "大学同学", "tags": ["朋友", "大学"] }`
    *   响应体 (成功): `{ "success": true, "data": { "contact": { "contact_id": 1, "name": "李雷", ... } } }`

*   **`GET /api/v1/contacts`**
    *   描述: 获取当前用户的所有联系人列表 (支持分页、筛选、排序)。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   查询参数: `?page=1&limit=10&sort_by=name&order=asc&relation_type=朋友&tag=工作`
    *   响应体 (成功): `{ "success": true, "data": { "contacts": [{ "contact_id": 1, "name": "李雷", ... }, {...}], "pagination": { "current_page": 1, "per_page": 10, "total_pages": 5, "total_items": 50 } } }`

*   **`GET /api/v1/contacts/{contact_id}`**
    *   描述: 获取单个联系人详情。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   响应体 (成功): `{ "success": true, "data": { "contact": { "contact_id": 1, "name": "李雷", ..., "interactions_summary": [...], "upcoming_reminders": [...], "latest_insights": [...] } } }` (可包含关联信息的摘要)

*   **`PUT /api/v1/contacts/{contact_id}`**
    *   描述: 更新指定联系人信息。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   请求体: (同创建联系人的请求体，但所有字段可选，例如: `{ "nickname": "阿雷" }`)
    *   响应体 (成功): `{ "success": true, "data": { "contact": { "contact_id": 1, "nickname": "阿雷", ... } } }`

*   **`DELETE /api/v1/contacts/{contact_id}`**
    *   描述: 删除指定联系人 (及其关联的互动、提醒、洞察等 - 后端需明确级联删除策略)。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   响应体 (成功): `{ "success": true, "message": "Contact deleted successfully" }`

#### 9.1 联系人互动记录 (Contact Interactions)

*   **`POST /api/v1/contacts/{contact_id}/interactions`**
    *   描述: 为指定联系人添加新的互动记录。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   请求体: `{ "interaction_type": "共同事件", "title": "一起看电影《热辣滚烫》", "description_text": "电影很感人，结束后一起吃了火锅。", "interaction_date": "2024-03-08T19:00:00Z", "location": "万达影城", "monetary_transaction_json": null, "related_diary_id": null }`
    *   响应体 (成功): `{ "success": true, "data": { "interaction": { "interaction_id": 1, "contact_id": 1, ... } } }`

*   **`GET /api/v1/contacts/{contact_id}/interactions`**
    *   描述: 获取指定联系人的互动记录列表 (支持分页、筛选)。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   查询参数: `?page=1&limit=10&interaction_type=对话摘要&sort_by=interaction_date&order=desc`
    *   响应体 (成功): `{ "success": true, "data": { "interactions": [...], "pagination": { ... } } }`

*   **`PUT /api/v1/contacts/{contact_id}/interactions/{interaction_id}`**
    *   描述: 更新指定的互动记录。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   请求体: (同创建互动记录的请求体，字段可选)
    *   响应体 (成功): `{ "success": true, "data": { "interaction": { ...updated_interaction_fields... } } }`

*   **`DELETE /api/v1/contacts/{contact_id}/interactions/{interaction_id}`**
    *   描述: 删除指定的互动记录。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   响应体 (成功): `{ "success": true, "message": "Interaction deleted successfully" }`

#### 9.2 联系人提醒 (Contact Reminders)

*   **`POST /api/v1/contacts/{contact_id}/reminders`**
    *   描述: 为指定联系人创建提醒。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   请求体: `{ "reminder_type": "生日", "reminder_date": "2024-05-15T09:00:00Z", "description": "记得发生日祝福，并准备小礼物", "notification_preferences_json": {"days_before": 3, "time_of_day": "09:00"} }`
    *   响应体 (成功): `{ "success": true, "data": { "reminder": { "reminder_id": 1, "contact_id": 1, ... } } }`

*   **`GET /api/v1/contacts/{contact_id}/reminders`**
    *   描述: 获取指定联系人的提醒列表。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   查询参数: `?is_active=true`
    *   响应体 (成功): `{ "success": true, "data": { "reminders": [...] } }`

*   **`GET /api/v1/reminders`** (全局提醒列表)
    *   描述: 获取当前用户的所有联系人相关提醒 (按时间排序，支持筛选)。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   查询参数: `?page=1&limit=20&sort_by=reminder_date&order=asc&upcoming_days=30` (例如获取未来30天内的提醒)
    *   响应体 (成功): `{ "success": true, "data": { "reminders": [...], "pagination": { ... } } }`

*   **`PUT /api/v1/contacts/{contact_id}/reminders/{reminder_id}`**
    *   描述: 更新指定的提醒。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   请求体: (同创建提醒的请求体，字段可选, 例如: `{ "is_active": false, "description": "已处理" }`)
    *   响应体 (成功): `{ "success": true, "data": { "reminder": { ...updated_reminder_fields... } } }`

*   **`DELETE /api/v1/contacts/{contact_id}/reminders/{reminder_id}`**
    *   描述: 删除指定的提醒。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   响应体 (成功): `{ "success": true, "message": "Reminder deleted successfully" }`

#### 9.3 关系洞察 (Relationship Insights)

*   **`GET /api/v1/contacts/{contact_id}/insights`**
    *   描述: 获取指定联系人的关系洞察分析结果 (例如亲密度、印象关键词)。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   响应体 (成功): `{ "success": true, "data": { "insights": [ {"insight_type": "亲密度评分", "value_json": {"score": 88, "trend": "rising", "last_calculated": "..."}, "generated_at": "..."}, {"insight_type": "印象关键词", "value_json": {"keywords": ["热情", "幽默", "健谈"]}, "generated_at": "..."} ] } }`

*   **`POST /api/v1/contacts/{contact_id}/insights/request-analysis`** (可选, 用于手动触发分析)
    *   描述: 请求对指定联系人进行关系分析。后端异步处理。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   响应体 (成功): `{ "success": true, "message": "Analysis request received. Results will be updated when available." }`

#### 9.4 联系人标签 (Contact Tags)

*   **`POST /api/v1/contacts/{contact_id}/tags`**
    *   描述: 为指定联系人添加标签。如果标签不存在，则为当前用户创建新标签 (scope为contact_related或general)。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   请求体: `{ "tag_name": "同事" }` 或 `{ "tag_id": 5 }` (优先使用tag_id如果提供)
    *   响应体 (成功): `{ "success": true, "data": { "tags": [{ "tag_id": 1, "name": "同事", "color": "..."}, ...] } }` (返回该联系人当前所有标签)

*   **`DELETE /api/v1/contacts/{contact_id}/tags/{tag_id}`**
    *   描述: 从指定联系人移除标签关联 (不删除标签本身)。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   响应体 (成功): `{ "success": true, "data": { "tags": [...] } }` (返回移除后该联系人剩余的标签)

### 对现有AI交互API的修改和补充

在原有的 `### 6. AI交互 (AI Interaction)` 部分，进行如下补充和修改：

*   **`POST /api/v1/ai/assist/writing`**
    *   请求体: (原有基础上增加) `{ ..., "related_contact_id": 1 (可选), "related_interaction_id": 5 (可选) }`

*   **`POST /api/v1/ai/chat`**
    *   请求体: (原有基础上增加) `{ ..., "related_contact_id": 1 (可选), "related_interaction_id": 5 (可选) }`

*   **(新增端点)** **`POST /api/v1/ai/assist/contact-profiling`**
    *   描述: 请求AI从文本中提取联系人相关信息（如兴趣、偏好、重要日期）。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   请求体: `{ "model_id": "(可选) 指定AI模型", "text_to_analyze": "用户输入的关于某人的描述文本或一段对话记录...", "contact_id": "(可选) 如果是针对已存在联系人补充信息" }`
    *   响应体 (成功): `{ "success": true, "data": { "extracted_info": { "name_suggestions": ["张三"], "interests": ["摄影", "旅游"], "preferences": ["喜欢辣的"], "important_dates": [{"name": "周年庆", "date_string": "十月一日"}], "key_events": ["上周一起爬山"] }, "contact_id_if_matched": 12 (如果AI识别并关联到现有联系人) } }`

*   **(新增端点)** **`POST /api/v1/ai/assist/relationship-suggestion`**
    *   描述: 请求AI提供关系维护建议或情商辅助沟通技巧。
    *   请求头: `Authorization: Bearer <jwt_token>`
    *   请求体: `{ "model_id": "(可选) 指定AI模型", "contact_id": 1, "context_description": "这位朋友最近情绪不高，想知道怎么安慰比较好。", "request_type": "maintenance_advice" / "communication_tip" / "gift_suggestion" }`
    *   响应体 (成功): `{ "success": true, "data": { "suggestion_type": "communication_tip", "suggestion_text": "可以尝试先倾听，表达理解，例如说：'听起来你最近不太顺利，我在这里支持你。'...", "follow_up_questions": ["他最近有提到具体什么烦恼吗？"] } }`

这份前后端交互和API设计文档为AI日记App的开发提供了详细的指导。在实际开发中，可能需要根据具体的技术选型和进一步的需求进行微调。


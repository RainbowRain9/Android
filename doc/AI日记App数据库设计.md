# AI日记App数据库设计

基于先前定义的核心功能和数据流，以下是AI日记App的建议数据库表结构设计。我们将主要使用关系型数据库的模式，但某些字段（如富文本内容或AI交互日志）可能适合存储为JSON或文本类型。

## 表结构定义

**0. 从 `nian.db` (旧数据库) 的启示与整合思考 (新增章节)**

分析 `nian.db`（特别是 `DREAM` 表及其 `S_EXT4` JSON配置，以及 `STEP` 表）后，我们发现其设计支持了高度可定制和多功能的"条目"概念，超出了简单日记的范畴。例如，`DREAM.NAME` 字段暗示了如"对话"、"计时本"、"习惯"等多种用途。`S_EXT4` 存储了大量针对特定条目的UI和行为配置。

为了让新的 `AI日记App` 也能支持这种灵活性，并为未来的功能扩展打下基础，我们将在以下表设计中融入这些思考，主要体现在增强 `diaries` 表的通用性和可配置性。

**1. `users` 表 (用户信息)**

*   `user_id` (主键, INT, 自增) - 用户唯一标识符
*   `email` (VARCHAR(255), 唯一, 非空) - 用户邮箱
*   `password_hash` (VARCHAR(255), 非空) - 哈希后的用户密码
*   `nickname` (VARCHAR(100), 可空) - 用户昵称
*   `avatar_url` (VARCHAR(512), 可空) - 用户头像URL
*   `preferred_ai_model_id` (INT, 外键，关联 `ai_models.model_id`, 可空) - 用户偏好的AI模型
*   `created_at` (TIMESTAMP, 默认当前时间) - 用户创建时间
*   `updated_at` (TIMESTAMP, 默认当前时间，更新时自动更新) - 用户信息最后更新时间

**2. `diaries` 表 (日记条目)**

*   `diary_id` (主键, INT, 自增) - 日记唯一标识符
*   `user_id` (外键, INT, 关联 `users.user_id`, 非空) - 所属用户ID
*   `title` (VARCHAR(255), 可空) - 日记标题
*   `content_text` (TEXT, 可空) - 日记的纯文本内容 (用于搜索和AI处理)
*   `content_html` (TEXT, 可空) - 日记的富文本内容 (HTML格式)
*   `mood` (VARCHAR(50), 可空) - 当天心情 (例如: "开心", "平静", "难过", 或更细化的情感标签)
*   `entry_date` (DATE, 非空) - 日记对应的日期
*   `diary_type` (VARCHAR(50), 默认 'standard', 可空) - 日记类型 (例如: "standard_text", "gratitude_journal", "dream_log", "ai_reflection", "dialogue", "goal_update")。用于区分不同格式、模板或用途的日记。
*   `cover_image_attachment_id` (INT, 外键, 关联 `media_attachments.attachment_id`, 可空) - 日记的封面图片ID。
*   `background_style_json` (JSON, 可空) - 定义此条日记的背景样式，例如纯色背景、背景图片ID（关联`media_attachments`）、渐变等。示例: `{"type": "color", "value": "#FFFFFF"}` 或 `{"type": "image", "attachment_id": 123, "blur": 5}`。
*   `custom_settings_json` (JSON, 可空) - 针对此条日记的特定自定义设置 (例如: 特定的AI交互参数、显示选项、关联的习惯追踪器配置等)。灵感来源于`nian.db`中`DREAM`表的`S_EXT4`字段。
*   `created_at` (TIMESTAMP, 默认当前时间) - 条目创建时间
*   `updated_at` (TIMESTAMP, 默认当前时间，更新时自动更新) - 条目最后更新时间
*   `is_archived` (BOOLEAN, 默认 FALSE) - 是否已归档
*   `is_pinned` (BOOLEAN, 默认 FALSE) - 是否置顶
*   `sort_index` (INT, 默认 0) - 用于用户自定义排序，尤其在 `is_pinned` 为 TRUE 时或特定视图下。

**3. `lists` 表 (清单/待办事项列表)**

*   `list_id` (主键, INT, 自增) - 清单唯一标识符
*   `user_id` (外键, INT, 关联 `users.user_id`, 非空) - 所属用户ID
*   `title` (VARCHAR(255), 非空) - 清单标题
*   `description` (TEXT, 可空) - 清单描述
*   `list_type` (VARCHAR(50), 默认 'todo', 可空) - 清单类型 (例如: "todo", "goal_tracker", "checklist")
*   `target_progress` (INT, 可空) - 目标进度值 (例如，如果 `list_type` 是 'goal_tracker')
*   `current_progress` (INT, 可空) - 当前进度值
*   `progress_unit` (VARCHAR(50), 可空) - 进度单位 (例如: "%", "items", "km")
*   `custom_settings_json` (JSON, 可空) - 针对此清单的特定自定义设置。
*   `created_at` (TIMESTAMP, 默认当前时间) - 清单创建时间
*   `updated_at` (TIMESTAMP, 默认当前时间，更新时自动更新) - 清单最后更新时间

**4. `list_items` 表 (清单项目)**

*   `item_id` (主键, INT, 自增) - 清单项目唯一标识符
*   `list_id` (外键, INT, 关联 `lists.list_id`, 非空) - 所属清单ID
*   `content` (TEXT, 非空) - 项目内容
*   `is_completed` (BOOLEAN, 默认 FALSE) - 是否已完成
*   `due_date` (TIMESTAMP, 可空) - 截止日期
*   `order_index` (INT, 默认 0) - 项目在清单中的排序索引
*   `created_at` (TIMESTAMP, 默认当前时间) - 项目创建时间
*   `updated_at` (TIMESTAMP, 默认当前时间，更新时自动更新) - 项目最后更新时间

**5. `tags` 表 (标签)**

*   `tag_id` (主键, INT, 自增) - 标签唯一标识符
*   `user_id` (外键, INT, 关联 `users.user_id`, 非空) - 创建该标签的用户ID
*   `name` (VARCHAR(100), 非空) - 标签名称
*   `color` (VARCHAR(7), 可空) - 标签颜色 (例如: "#FF0000")
*   `tag_scope` (VARCHAR(50), 默认 'general', 可空) - 标签适用范围 (例如: "general", "content_related", "contact_related")。方便区分标签是用于日记/清单，还是用于联系人，或通用。
*   `created_at` (TIMESTAMP, 默认当前时间)
*   约束: (`user_id`, `name`) 组合唯一，确保同一用户下标签名不重复。

**6. `diary_tags` 表 (日记-标签关联表，多对多)**

*   `diary_id` (外键, INT, 关联 `diaries.diary_id`, 非空)
*   `tag_id` (外键, INT, 关联 `tags.tag_id`, 非空)
*   主键: (`diary_id`, `tag_id`)

**7. `list_tags` 表 (清单-标签关联表，多对多)**

*   `list_id` (外键, INT, 关联 `lists.list_id`, 非空)
*   `tag_id` (外键, INT, 关联 `tags.tag_id`, 非空)
*   主键: (`list_id`, `tag_id`)

**8. `media_attachments` 表 (媒体附件)**

*   `attachment_id` (主键, INT, 自增) - 附件唯一标识符
*   `user_id` (外键, INT, 关联 `users.user_id`, 非空) - 上传用户ID
*   `diary_id` (外键, INT, 关联 `diaries.diary_id`, 可空) - 关联的日记ID (如果附件作为日记内容的一部分)
*   `list_item_id` (外键, INT, 关联 `list_items.item_id`, 可空) - 关联的清单项目ID
*   `attachment_purpose` (VARCHAR(50), 可空) - 附件用途，例如 'inline_content', 'diary_cover', 'diary_background', 'avatar'。用于区分附件在不同上下文中的角色。
*   `file_url` (VARCHAR(512), 非空) - 文件在云存储上的URL
*   `file_type` (VARCHAR(50), 非空) - 文件类型 (例如: "image/jpeg", "audio/mp3", "video/mp4")
*   `file_name` (VARCHAR(255), 可空) - 原始文件名
*   `file_size_bytes` (BIGINT, 可空) - 文件大小 (字节)
*   `created_at` (TIMESTAMP, 默认当前时间) - 上传时间

**9. `ai_models` 表 (AI模型信息)**

*   `model_id` (主键, INT, 自增) - AI模型唯一标识符
*   `model_name` (VARCHAR(100), 非空, 唯一) - 模型名称 (例如: "GPT-4", "Claude 3 Sonnet")
*   `api_provider` (VARCHAR(100), 非空) - API提供商 (例如: "OpenAI", "Anthropic")
*   `api_endpoint_identifier` (VARCHAR(255), 非空) - 后端用于识别调用哪个API的标识符
*   `description` (TEXT, 可空) - 模型描述
*   `is_active` (BOOLEAN, 默认 TRUE) - 该模型是否可用
*   `requires_user_api_key` (BOOLEAN, 默认 FALSE) - 是否需要用户提供自己的API Key

**10. `user_ai_model_configs` 表 (用户AI模型配置，例如API Key)**

*   `config_id` (主键, INT, 自增)
*   `user_id` (外键, INT, 关联 `users.user_id`, 非空)
*   `model_id` (外键, INT, 关联 `ai_models.model_id`, 非空)
*   `api_key_encrypted` (VARCHAR(512), 可空) - 用户提供的加密后的API Key (如果 `ai_models.requires_user_api_key` 为 TRUE)
*   `custom_settings_json` (JSON, 可空) - 用户针对该模型的特定配置 (例如温度、最大token等)
*   `created_at` (TIMESTAMP, 默认当前时间)
*   `updated_at` (TIMESTAMP, 默认当前时间，更新时自动更新)
*   约束: (`user_id`, `model_id`) 组合唯一

**11. `ai_interaction_logs` 表 (AI交互日志 - 用于分析和调试)**

*   `log_id` (主键, BIGINT, 自增) - 日志唯一标识符
*   `user_id` (外键, INT, 关联 `users.user_id`, 非空)
*   `model_id` (外键, INT, 关联 `ai_models.model_id`, 非空) - 使用的AI模型
*   `interaction_type` (VARCHAR(100), 非空) - 交互类型 (例如: "diary_assist_completion", "chat_diary_creation", "list_item_generation", "contact_info_extraction", "relationship_analysis", "eq_suggestion")
*   `prompt_text` (TEXT, 可空) - 发送给AI的完整提示
*   `response_text` (TEXT, 可空) - AI返回的原始响应
*   `request_timestamp` (TIMESTAMP, 非空) - 请求AI的时间
*   `response_timestamp` (TIMESTAMP, 可空) -收到AI响应的时间
*   `duration_ms` (INT, 可空) - 交互耗时 (毫秒)
*   `success` (BOOLEAN, 非空) - 交互是否成功
*   `error_message` (TEXT, 可空) - 如果失败，记录错误信息
*   `tokens_used` (INT, 可空) - (如果API提供) 本次交互消耗的token数
*   `related_diary_id` (外键, INT, 关联 `diaries.diary_id`, 可空)
*   `related_list_id` (外键, INT, 关联 `lists.list_id`, 可空)
*   `related_contact_id` (外键, INT, 关联 `contacts.contact_id`, 可空)
*   `related_interaction_id` (外键, INT, 关联 `contact_interactions.interaction_id`, 可空)

**12. `app_settings` 表 (用户应用设置)**

*   `setting_id` (主键, INT, 自增)
*   `user_id` (外键, INT, 关联 `users.user_id`, 非空, 唯一) - 每个用户一条设置记录
*   `theme` (VARCHAR(50), 默认 "light") - 应用主题 ("light", "dark")
*   `default_font_size` (INT, 默认 14)
*   `enable_daily_reminder` (BOOLEAN, 默认 FALSE)
*   `reminder_time` (TIME, 可空) - 提醒时间
*   `enable_cloud_sync` (BOOLEAN, 默认 TRUE)
*   `updated_at` (TIMESTAMP, 默认当前时间，更新时自动更新)

## 关系图 (ERD) 概念

*   `users` 是核心，许多表都通过 `user_id` 与之关联。
*   `diaries` 和 `lists` 是主要的内容实体，都属于某个 `user`。
*   `list_items` 从属于 `lists`。
*   `tags` 由 `user` 创建，并通过中间表 `diary_tags` 和 `list_tags` 与 `diaries` 和 `lists` 建立多对多关系。
*   `media_attachments` 可以关联到 `diaries` 或 `list_items`。
*   `ai_models` 是预定义的模型列表。
*   `user_ai_model_configs` 存储用户针对特定 `ai_models` 的配置 (如API Key)。
*   `ai_interaction_logs` 记录用户与AI的交互历史。
*   `app_settings` 存储用户的个性化应用设置。

## 新增：人脉关系管理与情商辅助功能相关表

为了支持新增的人脉关系管理与情商辅助功能，我们需要引入以下新的数据表：

**13. `contacts` 表 (联系人/人物档案)**

*   `contact_id` (主键, INT, 自增) - 联系人唯一标识符
*   `user_id` (外键, INT, 关联 `users.user_id`, 非空) - 所属用户ID (表示这个联系人是哪个用户记录的)
*   `name` (VARCHAR(255), 非空) - 联系人姓名
*   `nickname` (VARCHAR(100), 可空) - 昵称
*   `avatar_url` (VARCHAR(512), 可空) - 联系人头像URL (可以是用户上传或链接)
*   `relation_type` (VARCHAR(100), 可空) - 关系类型 (例如: "朋友", "家人", "同事", "合作伙伴")
*   `gender` (VARCHAR(20), 可空) - 性别
*   `birth_date` (DATE, 可空) - 生日
*   `contact_info_json` (JSON, 可空) - 其他联系方式 (例如: `{"phone": "...", "email": "...", "social_media": {"wechat": "..."}}`)
*   `interests_hobbies_text` (TEXT, 可空) - 兴趣爱好 (文本描述，AI可分析)
*   `preferences_text` (TEXT, 可空) - 口味偏好、禁忌等 (文本描述，AI可分析)
*   `important_dates_json` (JSON, 可空) - 其他重要日期 (例如: `[{"name": "结婚纪念日", "date": "YYYY-MM-DD", "notes": "..."}, ...]`)
*   `notes` (TEXT, 可空) - 关于此人的其他备注
*   `created_at` (TIMESTAMP, 默认当前时间)
*   `updated_at` (TIMESTAMP, 默认当前时间，更新时自动更新)

**14. `contact_interactions` 表 (与联系人的互动记录)**

*   `interaction_id` (主键, INT, 自增) - 互动记录唯一标识符
*   `user_id` (外键, INT, 关联 `users.user_id`, 非空) - 进行此记录的用户ID
*   `contact_id` (外键, INT, 关联 `contacts.contact_id`, 非空) - 关联的联系人ID
*   `interaction_type` (VARCHAR(100), 非空) - 互动类型 (例如: "对话摘要", "共同事件", "承诺", "金钱往来", "礼物互赠")
*   `title` (VARCHAR(255), 可空) - 互动事件标题
*   `description_text` (TEXT, 可空) - 详细描述 (AI可分析)
*   `interaction_date` (TIMESTAMP, 非空) - 互动发生时间或记录时间
*   `location` (VARCHAR(255), 可空) - 发生地点
*   `sentiment_score` (FLOAT, 可空) - (AI分析) 本次互动的情感得分 (-1 到 1)
*   `key_takeaways_json` (JSON, 可空) - (AI提取) 关键信息摘要
*   `related_diary_id` (外键, INT, 关联 `diaries.diary_id`, 可空) - 如果与某篇日记相关
*   `related_list_id` (外键, INT, 关联 `lists.list_id`, 可空) - 如果与某个清单相关
*   `monetary_transaction_json` (JSON, 可空) - 如果是金钱往来 (例如: `{"amount": 100.00, "currency": "CNY", "direction": "lent_to_contact"}`)
*   `created_at` (TIMESTAMP, 默认当前时间)
*   `updated_at` (TIMESTAMP, 默认当前时间，更新时自动更新)

**15. `contact_reminders` 表 (联系人相关提醒)**

*   `reminder_id` (主键, INT, 自增)
*   `user_id` (外键, INT, 关联 `users.user_id`, 非空)
*   `contact_id` (外键, INT, 关联 `contacts.contact_id`, 非空)
*   `reminder_type` (VARCHAR(100), 非空) - 提醒类型 (例如: "生日", "纪念日", "待办跟进", "AI建议联系")
*   `reminder_date` (TIMESTAMP, 非空) - 提醒触发时间
*   `description` (TEXT, 可空) - 提醒内容
*   `is_active` (BOOLEAN, 默认 TRUE)
*   `notification_preferences_json` (JSON, 可空) - 通知偏好 (例如提前几天提醒)
*   `created_at` (TIMESTAMP, 默认当前时间)
*   `updated_at` (TIMESTAMP, 默认当前时间，更新时自动更新)

**16. `relationship_insights` 表 (关系洞察分析结果)**

*   `insight_id` (主键, INT, 自增)
*   `user_id` (外键, INT, 关联 `users.user_id`, 非空)
*   `contact_id` (外键, INT, 关联 `contacts.contact_id`, 非空)
*   `insight_type` (VARCHAR(100), 非空) - 洞察类型 (例如: "亲密度评分", "印象关键词", "维护建议")
*   `value_json` (JSON, 非空) - 洞察结果 (例如: `{"score": 88, "trend": "rising"}` 或 `{"keywords": ["热情", "幽默"]}` 或 `{"suggestion": "可以聊聊最近共同感兴趣的电影"}`)
*   `generated_at` (TIMESTAMP, 默认当前时间) - 分析结果生成时间
*   `source_interaction_ids_json` (JSON, 可空) - (可选) 用于生成此洞察的互动记录ID列表

**17. `contact_tags` 表 (联系人-标签关联表，多对多)**

*   `contact_id` (外键, INT, 关联 `contacts.contact_id`, 非空)
*   `tag_id` (外键, INT, 关联 `tags.tag_id`, 非空) - 复用现有的 `tags` 表
*   主键: (`contact_id`, `tag_id`)

## 注意事项

*   **索引:** 需要在常用查询字段上创建索引，例如外键、日期字段、用户ID、标签名等，以提高查询性能。
*   **数据类型:** TEXT类型用于存储较长的文本，JSON类型可以灵活存储结构化但模式不固定的数据 (如自定义设置)。
*   **安全性:** `password_hash` 必须使用强哈希算法 (如 bcrypt 或 Argon2)。`api_key_encrypted` 必须进行对称或非对称加密存储，密钥管理是关键。
*   **可扩展性:** 随着功能增加，可能需要添加更多表或修改现有表结构。例如，如果引入社交功能，可能需要 `friends` 表、`shares` 表等。**本次更新已为人脉管理功能添加了 `contacts` 及相关表。**
*   **数据一致性:** 尽可能使用数据库的外键约束来保证数据引用的完整性。

这份数据库设计为AI日记App提供了一个坚实的基础。在实际开发过程中，可能还需要根据具体实现细节进行调整和优化。


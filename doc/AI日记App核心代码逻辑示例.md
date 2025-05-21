# AI日记App核心代码逻辑示例

本文档提供AI日记App中一些核心功能的后端和前端代码逻辑示例或伪代码。这些示例旨在阐明关键模块的实现思路，并与先前设计的API接口、数据库结构保持一致。实际开发中需根据具体技术栈进行调整和完善。

## 一、后端核心逻辑示例 (Python/Flask 风格伪代码)

### 1. 用户认证 - 登录 (`POST /api/v1/auth/login`)

```python
# app/routes/auth.py

from flask import Blueprint, request, jsonify
from werkzeug.security import check_password_hash
import jwt
import datetime
from app.models import User # 假设使用SQLAlchemy等ORM
from app import db, app # Flask app 和 SQLAlchemy db 实例

auth_bp = Blueprint('auth', __name__)

@auth_bp.route('/login', methods=['POST'])
def login():
    data = request.get_json()
    email = data.get('email')
    password = data.get('password')

    if not email or not password:
        return jsonify({"success": False, "error": {"code": "MISSING_CREDENTIALS", "details": "Email and password are required."}}), 400

    user = User.query.filter_by(email=email).first()

    if not user or not check_password_hash(user.password_hash, password):
        return jsonify({"success": False, "error": {"code": "INVALID_CREDENTIALS", "details": "Invalid email or password."}}), 401

    # 生成JWT
    token_payload = {
        'user_id': user.user_id,
        'exp': datetime.datetime.utcnow() + datetime.timedelta(hours=app.config['JWT_EXPIRATION_HOURS'])
    }
    token = jwt.encode(token_payload, app.config['SECRET_KEY'], algorithm='HS256')

    user_data = {
        'user_id': user.user_id,
        'email': user.email,
        'nickname': user.nickname,
        'avatar_url': user.avatar_url
    }

    return jsonify({
        "success": True,
        "data": {
            "user": user_data,
            "token": token
        }
    }), 200

# ... 其他认证路由 (register, refresh_token等)
```

### 2. 日记条目创建 (`POST /api/v1/diaries`)

```python
# app/routes/diaries.py

from flask import Blueprint, request, jsonify
from app.models import Diary, Tag, DiaryTag, MediaAttachment # 假设 Diary, MediaAttachment 模型已更新
from app import db
from app.utils.auth import token_required # 自定义装饰器检查JWT
import datetime
import json # 用于处理 JSON 字符串

diaries_bp = Blueprint('diaries', __name__)

@diaries_bp.route('', methods=['POST'])
@token_required
def create_diary(current_user):
    data = request.get_json()

    title = data.get('title')
    content_text = data.get('content_text')
    content_html = data.get('content_html')
    mood = data.get('mood')
    entry_date_str = data.get('entry_date') # "YYYY-MM-DD"
    
    # 新增字段
    diary_type = data.get('diary_type', 'standard_text') # 默认为 standard_text
    cover_image_attachment_id = data.get('cover_image_attachment_id') # 可能为 None
    background_style_json_str = data.get('background_style_json') # JSON 字符串
    custom_settings_json_str = data.get('custom_settings_json') # JSON 字符串
    sort_index = data.get('sort_index', 0) # 默认为0
    is_pinned = data.get('is_pinned', False) # 默认为False

    tag_names = data.get('tags', []) # list of tag names
    attachments_data = data.get('attachments', []) # list of attachment objects

    if not content_text and not content_html:
        return jsonify({"success": False, "error": {"code": "CONTENT_REQUIRED", "details": "Diary content is required."}}), 400

    try:
        entry_date = datetime.datetime.strptime(entry_date_str, '%Y-%m-%d').date() if entry_date_str else datetime.date.today()
    except ValueError:
        return jsonify({"success": False, "error": {"code": "INVALID_DATE_FORMAT", "details": "Date must be in YYYY-MM-DD format."}}), 400

    # 处理 JSON 字符串字段
    background_style_json = None
    if background_style_json_str:
        try:
            background_style_json = json.loads(background_style_json_str) if isinstance(background_style_json_str, str) else background_style_json_str
        except json.JSONDecodeError:
            return jsonify({"success": False, "error": {"code": "INVALID_JSON_FORMAT", "details": "Invalid format for background_style_json."}}), 400

    custom_settings_json = None
    if custom_settings_json_str:
        try:
            custom_settings_json = json.loads(custom_settings_json_str) if isinstance(custom_settings_json_str, str) else custom_settings_json_str
        except json.JSONDecodeError:
            return jsonify({"success": False, "error": {"code": "INVALID_JSON_FORMAT", "details": "Invalid format for custom_settings_json."}}), 400

    new_diary = Diary(
        user_id=current_user.user_id,
        title=title,
        content_text=content_text,
        content_html=content_html,
        mood=mood,
        entry_date=entry_date,
        diary_type=diary_type,
        cover_image_attachment_id=cover_image_attachment_id,
        background_style_json=background_style_json, # 存储解析后的 dict
        custom_settings_json=custom_settings_json, # 存储解析后的 dict
        sort_index=sort_index,
        is_pinned=is_pinned
    )
    db.session.add(new_diary)
    db.session.flush() # 获取 new_diary.diary_id

    # 处理标签
    for tag_name in tag_names:
        tag = Tag.query.filter_by(user_id=current_user.user_id, name=tag_name).first()
        if not tag:
            tag = Tag(user_id=current_user.user_id, name=tag_name)
            db.session.add(tag)
            db.session.flush() # 获取 tag.tag_id
        diary_tag_link = DiaryTag(diary_id=new_diary.diary_id, tag_id=tag.tag_id)
        db.session.add(diary_tag_link)

    # 处理附件 (MediaAttachment)
    processed_attachments = []
    for att_data in attachments_data:
        attachment = MediaAttachment(
            user_id=current_user.user_id,
            diary_id=new_diary.diary_id, # 关联到当前日记
            list_id=None, # 非清单附件
            file_url=att_data.get('file_url'),
            file_type=att_data.get('file_type'),
            file_size_kb=att_data.get('file_size_kb'),
            original_filename=att_data.get('original_filename'),
            # 新增字段
            attachment_purpose=att_data.get('attachment_purpose', 'general'), # 默认为 general
            # 其他字段
            file_size_kb=att_data.get('file_size_kb'),
            original_filename=att_data.get('original_filename')
        )
        db.session.add(attachment)
        processed_attachments.append(attachment) # 用于后续可能的响应构建

    db.session.commit()

    # 构建响应数据 (此处简化，实际应序列化 new_diary 及其关联数据，包括新字段)
    # 假设 Diary 模型有 to_dict() 方法用于序列化
    diary_response = new_diary.to_dict() # 确保 to_dict() 包含所有新字段
    
    return jsonify({"success": True, "data": {"diary": diary_response }}), 201

# 假设 Diary 模型 (app/models.py) 更新如下：
# class Diary(db.Model):
#     # ... 其他字段 ...
#     diary_type = db.Column(db.String(50), default='standard_text')
#     cover_image_attachment_id = db.Column(db.Integer, db.ForeignKey('media_attachment.attachment_id'), nullable=True)
#     background_style_json = db.Column(db.JSON, nullable=True) # 或 db.Text, 手动json.dumps/loads
#     custom_settings_json = db.Column(db.JSON, nullable=True) # 或 db.Text
#     sort_index = db.Column(db.Integer, default=0)
#     is_pinned = db.Column(db.Boolean, default=False)
#     # ... relationships ...

# 假设 MediaAttachment 模型 (app/models.py) 更新如下：
# class MediaAttachment(db.Model):
#     # ... 其他字段 ...
#     attachment_purpose = db.Column(db.String(50), default='general') # e.g., 'inline_content', 'diary_cover', 'list_item_visual'
#     # ... 其他字段 ...


# 同样需要为清单 (Lists) 创建和更新的路由和模型添加类似字段：
# - list_type
# - target_progress
# - current_progress
# - progress_unit
# - custom_settings_json
# - cover_image_attachment_id
# - background_style_json
# - sort_index
# - is_pinned

# ... 其他日记路由 (GET, PUT, DELETE)
# 在 GET /api/v1/diaries/{diary_id} 和 GET /api/v1/diaries (列表) 的响应中，
# 也需要确保序列化的日记对象包含所有新字段。
# 在 PUT /api/v1/diaries/{diary_id} 中，请求体也应支持更新这些新字段。

```

### 3. AI写作辅助 (`POST /api/v1/ai/assist/writing`)

```python
# app/services/ai_service.py
import requests
import os
from app.models import UserAiModelConfig, AiModel

# 假设有一个AI模型提供商的客户端库或直接HTTP调用
# 例如: from openai import OpenAI

def get_ai_suggestion(user_id, model_identifier, task_type, context_text, user_prompt):
    # 1. 获取用户选择的AI模型和配置 (包括API Key)
    # (简化) 假设 model_identifier 直接是 ai_models.api_endpoint_identifier
    ai_model_db = AiModel.query.filter_by(api_endpoint_identifier=model_identifier).first()
    if not ai_model_db:
        raise ValueError("AI Model not found")

    api_key = None
    if ai_model_db.requires_user_api_key:
        user_config = UserAiModelConfig.query.filter_by(user_id=user_id, model_id=ai_model_db.model_id).first()
        if user_config and user_config.api_key_encrypted:
            api_key = decrypt_api_key(user_config.api_key_encrypted) # 需要解密函数
        else:
            raise ValueError("User API key required but not found for this model")
    else:
        # 使用应用全局API Key
        api_key = os.getenv(f"{ai_model_db.api_provider.upper()}_API_KEY")

    if not api_key:
        raise ValueError("API Key not available for this model")

    # 2. 根据 task_type 和模型构建 prompt
    prompt = f"{context_text}\n\nUser request: {user_prompt}\nTask: {task_type}\n\nAI Suggestion:"
    if task_type == "sentence_completion":
        prompt = f"{context_text}"
    elif task_type == "grammar_correction":
        prompt = f"Correct the grammar of the following text:\n{context_text}"
    # ... 其他任务类型的prompt构建

    # 3. 调用第三方AI API
    # 示例: OpenAI (概念性)
    if ai_model_db.api_provider == "OpenAI":
        # client = OpenAI(api_key=api_key)
        # response = client.completions.create(
        # model=ai_model_db.model_name, # e.g., "text-davinci-003" or chat model
        # prompt=prompt,
        # max_tokens=150
        # )
        # suggestion = response.choices[0].text.strip()
        # 模拟调用
        suggestion = f"[AI Suggestion for '{task_type}' based on '{ai_model_db.model_name}']: This is a sample suggestion."
    elif ai_model_db.api_provider == "Anthropic":
        # ... 调用 Anthropic API
        suggestion = f"[Anthropic Suggestion for '{task_type}']: Sample."
    else:
        raise ValueError("Unsupported AI provider")

    return suggestion

# app/routes/ai.py
from app.services.ai_service import get_ai_suggestion

ai_bp = Blueprint('ai', __name__)

@ai_bp.route('/assist/writing', methods=['POST'])
@token_required
def assist_writing(current_user):
    data = request.get_json()
    model_id = data.get('model_id') # 这是 ai_models.api_endpoint_identifier
    task_type = data.get('task_type')
    context_text = data.get('context_text', '')
    user_prompt = data.get('user_prompt', '')

    if not model_id or not task_type:
        return jsonify({"success": False, "error": {"code": "MISSING_AI_PARAMS", "details": "Model ID and task type are required."}}), 400

    try:
        suggestion = get_ai_suggestion(current_user.user_id, model_id, task_type, context_text, user_prompt)
        return jsonify({"success": True, "data": {"suggestion": suggestion}}), 200
    except ValueError as e:
        return jsonify({"success": False, "error": {"code": "AI_SERVICE_ERROR", "details": str(e)}}), 500
    except Exception as e:
        # Log the full error for debugging
        app.logger.error(f"AI service uncaught exception: {str(e)}")
        return jsonify({"success": False, "error": {"code": "AI_SERVICE_UNEXPECTED_ERROR", "details": "An unexpected error occurred with the AI service."}}), 500
```

### 4. AI模型切换逻辑 (后端部分 - 更新用户配置)

```python
# app/routes/users.py (或 app_settings.py)

@users_bp.route('/me/ai-config', methods=['PUT'])
@token_required
def update_user_ai_config(current_user):
    data = request.get_json()
    preferred_model_id_from_request = data.get('preferred_ai_model_id') # 这是 ai_models.model_id
    api_keys_data = data.get('api_keys', []) # list of { "model_id": X, "key": "..." }

    # 更新偏好模型
    if preferred_model_id_from_request:
        model_exists = AiModel.query.get(preferred_model_id_from_request)
        if not model_exists:
            return jsonify({"success": False, "error": {"code": "INVALID_MODEL_ID", "details": "Preferred AI model ID is invalid."}}), 400
        current_user.preferred_ai_model_id = preferred_model_id_from_request

    # 更新用户提供的API Keys
    for key_info in api_keys_data:
        model_id_for_key = key_info.get('model_id')
        api_key_plain = key_info.get('key')

        if not model_id_for_key or not api_key_plain:
            continue # 或者返回错误

        # 确保该模型确实需要用户API Key
        target_model = AiModel.query.get(model_id_for_key)
        if not target_model or not target_model.requires_user_api_key:
            # Log warning or return error
            continue

        config = UserAiModelConfig.query.filter_by(user_id=current_user.user_id, model_id=model_id_for_key).first()
        if not config:
            config = UserAiModelConfig(user_id=current_user.user_id, model_id=model_id_for_key)
            db.session.add(config)
        
        config.api_key_encrypted = encrypt_api_key(api_key_plain) # 需要加密函数
        config.updated_at = datetime.datetime.utcnow()

    db.session.commit()
    return jsonify({"success": True, "message": "AI configuration updated successfully."}), 200

# Helper for encryption (conceptual)
def encrypt_api_key(plain_key):
    # Use Fernet or similar strong encryption
    # key = os.getenv('API_KEY_ENCRYPTION_SECRET')
    # cipher_suite = Fernet(key)
    # encrypted_text = cipher_suite.encrypt(plain_key.encode())
    # return encrypted_text.decode()
    return f"encrypted_{plain_key}" # Placeholder

def decrypt_api_key(encrypted_key):
    # key = os.getenv('API_KEY_ENCRYPTION_SECRET')
    # cipher_suite = Fernet(key)
    # decrypted_text = cipher_suite.decrypt(encrypted_key.encode())
    # return decrypted_text.decode()
    if encrypted_key.startswith("encrypted_"):
        return encrypted_key[len("encrypted_"):] # Placeholder
    return None
```

## 二、前端核心逻辑示例 (JavaScript/React Native 风格伪代码)

### 1. 用户登录请求

```javascript
// services/authService.js
const API_URL = '/api/v1/auth';

export const loginUser = async (email, password) => {
  try {
    const response = await fetch(`${API_URL}/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ email, password }),
    });
    const data = await response.json();

    if (!response.ok || !data.success) {
      throw new Error(data.error?.details || 'Login failed');
    }
    // Store token (e.g., in AsyncStorage, Redux state, Context)
    // localStorage.setItem('authToken', data.data.token);
    // localStorage.setItem('userData', JSON.stringify(data.data.user));
    return data.data; // { user, token }
  } catch (error) {
    console.error('Login error:', error);
    throw error;
  }
};

// components/LoginForm.js (React example)
// const [email, setEmail] = useState('');
// const [password, setPassword] = useState('');
// const [error, setError] = useState(null);
// const { setCurrentUser, setAuthToken } = useAuth(); // Auth context hook

// const handleLogin = async () => {
//   try {
//     setError(null);
//     const { user, token } = await loginUser(email, password);
//     setAuthToken(token);
//     setCurrentUser(user);
//     // Navigate to home screen
//   } catch (err) {
//     setError(err.message);
//   }
// };
```

### 2. 创建新日记条目 (包含AI辅助概念)

```javascript
// services/diaryService.js
const DIARY_API_URL = '/api/v1/diaries';
const AI_API_URL = '/api/v1/ai';

// Helper to get auth token
// const getAuthToken = () => localStorage.getItem('authToken');

export const createDiaryEntry = async (diaryData) => {
  // const token = getAuthToken();
  const response = await fetch(DIARY_API_URL, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      // 'Authorization': `Bearer ${token}`,
    },
    body: JSON.stringify(diaryData),
  });
  // ... handle response ...
};

export const getAIWritingSuggestion = async (payload) => {
  // const token = getAuthToken();
  const response = await fetch(`${AI_API_URL}/assist/writing`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      // 'Authorization': `Bearer ${token}`,
    },
    body: JSON.stringify(payload), // { model_id, task_type, context_text, user_prompt }
  });
  // ... handle response ...
};

// components/DiaryEditor.js (React example)
// const [title, setTitle] = useState('');
// const [content, setContent] = useState(''); // HTML content from rich text editor
// const [mood, setMood] = useState('');
// const [tags, setTags] = useState([]);
// const [selectedAIModel, setSelectedAIModel] = useState('gpt-3.5-turbo'); // From user settings

// const handleAISuggestion = async (taskType) => {
//   try {
//     const payload = {
//       model_id: selectedAIModel,
//       task_type: taskType,
//       context_text: convertHtmlToText(content), // Need a utility for this
//       user_prompt: "Complete this sentence / Correct grammar etc."
//     };
//     const result = await getAIWritingSuggestion(payload);
//     if (result.success) {
//       // Update content with suggestion, perhaps offering user to accept/reject
//       setContent(content + ' ' + result.data.suggestion);
//     }
//   } catch (error) {
//     console.error('AI suggestion error:', error);
//   }
// };

// const handleSaveDiary = async () => {
//   const diaryData = {
//     title,
//     content_html: content,
//     content_text: convertHtmlToText(content),
//     mood,
//     entry_date: new Date().toISOString().split('T')[0], // YYYY-MM-DD
//     tags: tags.map(tag => tag.name),
//     // attachments: []
//   };
//   await createDiaryEntry(diaryData);
//   // Navigate or show success
// };
```

### 3. 处理AI模型选择 (在设置页面)

```javascript
// services/userService.js
const USER_API_URL = '/api/v1/users/me';

export const updateAIConfiguration = async (configData) => {
  // const token = getAuthToken();
  const response = await fetch(`${USER_API_URL}/ai-config`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      // 'Authorization': `Bearer ${token}`,
    },
    body: JSON.stringify(configData), // { preferred_ai_model_id, api_keys: [{ model_id, key }] }
  });
  // ... handle response ...
};

// components/SettingsScreen.js (React example)
// const [availableModels, setAvailableModels] = useState([]); // Fetched from /api/v1/ai/models
// const [selectedModelId, setSelectedModelId] = useState(currentUser.preferred_ai_model_id);
// const [userApiKeys, setUserApiKeys] = useState({}); // { model_id: key }

// useEffect(() => { fetchAvailableModels(); fetchCurrentUserConfig(); }, []);

// const handleSaveAISettings = async () => {
//   const apiKeysPayload = Object.entries(userApiKeys)
//     .filter(([modelId, key]) => key) // Only send keys that are set
//     .map(([modelId, key]) => ({ model_id: parseInt(modelId), key }));

//   const configData = {
//     preferred_ai_model_id: selectedModelId,
//     api_keys: apiKeysPayload,
//   };
//   try {
//     await updateAIConfiguration(configData);
//     // Show success message, update local user context if needed
//   } catch (error) {
//     console.error('Failed to update AI settings:', error);
//   }
// };
```

## 三、免责声明

以上代码示例仅为概念性的演示，用于说明核心逻辑和交互流程。它们并非生产就绪代码，省略了完整的错误处理、输入验证、安全性加固、UI细节、状态管理以及特定框架的最佳实践。在实际项目中，需要根据选择的技术栈和具体需求进行详细实现和测试。


# Android AI日记应用架构设计

## 项目概述

**项目名称**: Android AI日记应用 (基于redesigned版本设计)
**技术栈**: Android Kotlin + Java 8+
**架构模式**: MVVM + Repository Pattern + Clean Architecture
**目标**: 高性能、离线优先、AI深度集成的智能日记应用

## 技术架构

### 核心技术栈
- **开发语言**: Java 8+ / Kotlin 1.9.22
- **UI框架**: Android Views + Material Design 3
- **本地数据库**: SQLite + Room Database 2.6.1
- **架构模式**: MVVM + Repository Pattern + Clean Architecture
- **依赖注入**: Dagger Hilt (待集成)
- **异步处理**: Kotlin Coroutines + RxJava 3
- **图片加载**: Glide / Coil (待集成)
- **网络请求**: Retrofit 2 + OkHttp 3 (可选AI服务同步)
- **构建工具**: Gradle 8.x + Android Gradle Plugin 8.10.1
- **最低SDK**: API 24 (Android 7.0) / 目标SDK: API 34 (Android 14)

### 设计参考标准
- **主要参考**: `frontend/redesigned` 目录中的前端设计文件
- **开发文档**: `docs/redesigned` 目录中的开发文档
- **设计理念**: 参考Notion App的简洁高效设计
- **AI深度集成**: AI功能贯穿所有模块，不再是独立功能
- **4+3页面架构**: 4个核心Tab + 3个专门功能页面

## 项目结构

### 当前项目结构
```
android-app/
├── app/src/main/java/com/example/android01/
│   ├── MainActivity.java           # 登录界面 (已实现)
│   ├── HomeActivity.java          # 主界面导航 (已实现)
│   ├── HomeFragment.java          # 首页Fragment (已实现)
│   ├── DiaryListActivity.java     # 日记列表 (已实现)
│   ├── DiaryEditorActivity.java   # 日记编辑器 (已声明)
│   ├── AIChatActivity.java        # AI聊天 (已声明)
│   ├── SettingsActivity.java      # 设置页面 (已声明)
│   └── data/                      # 数据层
│       ├── AppDatabase.java       # Room数据库 (已实现)
│       ├── User.java              # 用户实体 (已实现)
│       └── UserDao.java           # 用户DAO (已实现)
```

### 目标架构结构 (基于redesigned版本)
```
android-app/
├── app/src/main/java/com/example/android01/
│   ├── ui/                        # UI层
│   │   ├── activity/              # Activity
│   │   │   ├── MainActivity.java  # 登录界面
│   │   │   ├── HomeActivity.java  # 主界面 (4+3页面架构)
│   │   │   ├── RelationshipsActivity.java  # 人际关系页面
│   │   │   ├── AIWriterActivity.java       # AI创作页面
│   │   │   └── AISettingsActivity.java     # AI配置页面
│   │   ├── fragment/              # Fragment
│   │   │   ├── HomeFragment.java          # 首页 (对应redesigned-home.html)
│   │   │   ├── ContentFragment.java       # 内容管理 (对应redesigned-content.html)
│   │   │   ├── InsightsFragment.java      # 洞察分析 (对应redesigned-insights.html)
│   │   │   └── ProfileFragment.java       # 个人中心 (对应redesigned-profile.html)
│   │   ├── adapter/               # RecyclerView适配器
│   │   └── custom/                # 自定义View
│   ├── viewmodel/                 # ViewModel层
│   ├── repository/                # Repository层
│   ├── database/                  # 数据库相关
│   │   ├── entity/                # Room Entity
│   │   ├── dao/                   # Room DAO
│   │   └── AppDatabase.java       # 数据库类
│   ├── model/                     # 数据模型
│   ├── util/                      # 工具类
│   ├── di/                        # 依赖注入模块
│   └── Application.java           # Application类
```

## 核心功能模块 (基于Redesigned版本4+3页面架构)

### 1. 首页模块 (Home Module) - 对应redesigned-home.html
- **全局搜索**: 跨类型内容搜索功能
- **AI智能建议**: 基于用户习惯的个性化建议
- **快速操作**: 4个常用功能的快速入口
- **今日统计**: 实时数据展示
- **最近内容**: 最新记录的预览
- **浮动AI助手**: 常驻AI聊天面板

### 2. 内容管理模块 (Content Module) - 对应redesigned-content.html
- **统一内容管理**: 日记、笔记、任务、人脉、目标、旅行
- **AI写作助手**: 集成的创作建议系统
- **多视图切换**: 列表、网格、时间轴三种视图
- **智能筛选**: 内容类型筛选 + 智能搜索
- **AI标签系统**: 自动生成的内容标签

### 3. 洞察分析模块 (Insights Module) - 对应redesigned-insights.html
- **数据分析**: 心情分析、活跃度分析、内容分析
- **活跃度热力图**: GitHub风格的12周活跃度可视化
- **AI智能洞察**: 基于数据的个性化分析
- **个性化推荐**: AI驱动的改进建议
- **数据导出**: PDF报告和Excel数据导出

### 4. 个人中心模块 (Profile Module) - 对应redesigned-profile.html
- **个人资料**: 用户信息和核心统计
- **AI助手状态**: AI使用情况和配置入口
- **成就系统**: 游戏化的用户激励
- **功能设置**: 分类的设置选项
- **社交功能**: 社区交流和分享功能

### 5. 人际关系模块 (Relationships Module) - 对应redesigned-relationships.html
- **关系图谱**: 可视化的人际关系网络
- **重要提醒**: 分类的关系管理提醒
- **AI关系洞察**: 社交数据分析
- **联系人管理**: 最近联系的人员管理
- **亲密度可视化**: 关系强度的直观展示

### 6. AI创作模块 (AI Writer Module) - 对应redesigned-ai-writer.html
- **内容类型选择**: 日记、笔记、目标三种类型
- **心情选择器**: 5级心情影响AI建议
- **AI创作建议**: 智能的写作主题推荐
- **富文本编辑**: 完整的编辑工具栏
- **写作统计**: 实时的创作数据统计

### 7. AI配置模块 (AI Settings Module) - 对应redesigned-ai-settings.html
- **AI个性选择**: 4种AI个性类型
- **实时预览**: 根据设置预览AI回应
- **智能功能开关**: 各项AI功能的开关控制
- **交互设置**: 回应频率、语音类型、语言风格
- **隐私安全**: 数据加密、学习记忆控制

## 数据库设计

### 当前数据库状态
- **已实现**: User表 (基础用户认证)
- **需要扩展**: 完整的AI日记数据模型

### 目标数据库结构 (基于redesigned版本)
参考 `docs/redesigned/DATABASE_DESIGN_REDESIGNED.md` 中的设计：
- **users**: 用户信息表
- **contents**: 统一内容表 (日记、笔记、任务、人脉、目标、旅行)
- **ai_interactions**: AI交互记录表
- **tags**: 标签表
- **attachments**: 附件表
- **user_settings**: 用户设置表

## 开发状态

### 已完成功能
- ✅ 基础项目结构和构建配置
- ✅ 用户登录和注册功能
- ✅ 主界面导航框架 (底部导航)
- ✅ 首页Fragment基础结构
- ✅ 日记列表Activity基础结构
- ✅ Room数据库基础配置

### 待实现功能 (按优先级排序)
1. **P0 - 核心功能**
   - 完善数据库设计 (对应redesigned版本数据结构)
   - 实现4+3页面架构的Fragment和Activity
   - AI写作助手核心功能
   - 内容创建和编辑功能

2. **P1 - 重要功能**
   - 数据可视化 (活跃度热力图)
   - AI洞察分析功能
   - 人际关系管理
   - 全局搜索功能

3. **P2 - 增强功能**
   - AI配置和个性化
   - 数据导出功能
   - 主题和外观设置
   - 性能优化

## 技术决策记录

### 架构决策
- **选择MVVM**: 便于数据绑定和测试，符合Android最佳实践
- **使用Room**: 提供类型安全的SQLite抽象层
- **采用Fragment**: 支持4+3页面架构的灵活导航

### 设计决策
- **参考redesigned版本**: 确保UI/UX的现代化和一致性
- **AI深度集成**: AI功能贯穿所有模块，而非独立功能
- **离线优先**: 所有数据本地存储，可选云端同步

### 性能决策
- **使用Kotlin Coroutines**: 现代化的异步处理
- **实现数据缓存**: 提升用户体验
- **优化数据库查询**: 使用索引和查询优化

## 下一步计划

### 短期目标 (1-2周)
1. 完善数据库设计，实现redesigned版本的数据结构
2. 实现4个核心Fragment (Home, Content, Insights, Profile)
3. 创建基础的内容创建和编辑功能
4. 集成AI写作助手的基础功能

### 中期目标 (1个月)
1. 实现3个专门功能页面 (Relationships, AI Writer, AI Settings)
2. 完善数据可视化功能
3. 实现全局搜索和筛选
4. 优化UI/UX，确保与redesigned版本一致

### 长期目标 (2-3个月)
1. 完善AI功能集成
2. 实现数据导出和分享
3. 性能优化和测试
4. 准备发布版本

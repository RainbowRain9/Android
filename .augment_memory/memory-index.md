# 记忆系统索引

## 记忆系统概览

### 系统状态

- **创建时间**: 2025-06-09
- **最后更新**: 2025-06-09
- **版本**: 1.0
- **状态**: 活跃

### 记忆层结构

#### 长期记忆 (Long-Term Memory)

位置: `.augment_memory/core/`

| 文件名 | 内容描述 | 最后更新 | 重要性 |
|--------|----------|----------|--------|
| `architecture.md` | 项目整体架构设计，技术栈，模块划分 | 2025-06-09 | 🔴 高 |
| `tech-stack.md` | 详细技术栈信息，工具配置，命令参考 | 2025-06-09 | 🔴 高 |
| `patterns.md` | 开发模式，代码示例，最佳实践模板 | 2025-06-09 | 🟡 中 |
| `decisions.md` | 架构决策记录，技术选型理由 | 2025-06-09 | 🔴 高 |
| `best-practices.md` | 项目特定最佳实践，代码规范 | 2025-06-09 | 🟡 中 |

#### 工作记忆 (Working Memory)

位置: `.augment_memory/`

| 文件名 | 内容描述 | 最后更新 | 重要性 |
|--------|----------|----------|--------|
| `activeContext.md` | 当前任务上下文，即时目标，工作状态 | 2025-06-09 | 🔴 高 |

#### 短期记忆 (Short-Term Memory)

位置: `.augment_memory/task-logs/`

| 文件名 | 内容描述 | 创建时间 | 状态 |
|--------|----------|----------|------|
| 待创建 | 任务执行日志将在此目录创建 | - | 待创建 |

#### 管理文件

位置: `.augment_memory/`

| 文件名 | 内容描述 | 最后更新 | 重要性 |
|--------|----------|----------|--------|
| `memory-index.md` | 记忆系统索引 (当前文件) | 2025-06-09 | 🟡 中 |
| `session-history.md` | 会话历史记录 | 待创建 | 🟡 中 |

## 项目知识图谱

### 核心概念

#### 技术架构

- **架构模式**: MVVM + Repository Pattern + Clean Architecture
- **数据库**: Room Database + SQLite (离线优先)
- **UI框架**: Android Views + Material Design 3
- **开发语言**: Java 8+ / Kotlin 1.9.22

#### 设计理念

- **参考标准**: `frontend/redesigned` 目录设计文件
- **页面架构**: 4+3页面架构 (4个核心Tab + 3个专门功能页面)
- **AI集成**: AI功能贯穿所有模块，非独立功能
- **隐私保护**: 数据完全本地存储，用户完全控制

#### 功能模块

1. **4个核心Tab**:
   - Home (首页) - 对应redesigned-home.html
   - Content (内容管理) - 对应redesigned-content.html
   - Insights (洞察分析) - 对应redesigned-insights.html
   - Profile (个人中心) - 对应redesigned-profile.html

2. **3个专门页面**:
   - Relationships (人际关系) - 对应redesigned-relationships.html
   - AI Writer (AI创作) - 对应redesigned-ai-writer.html
   - AI Settings (AI配置) - 对应redesigned-ai-settings.html

### 关键决策

#### 技术选型决策

- **ADR-001**: 选择Android原生开发 (性能优势，平台特性)
- **ADR-002**: 选择MVVM架构模式 (数据绑定，测试友好)
- **ADR-003**: 选择Room数据库 (类型安全，官方支持)
- **ADR-004**: 选择离线优先策略 (隐私保护，性能优势)
- **ADR-005**: 选择Material Design 3 (现代化，一致性)

#### 架构决策

- **ADR-006**: 采用4+3页面架构 (设计一致性，功能分离)
- **ADR-007**: 选择Fragment作为主要UI组件 (内存效率，模块化)
- **ADR-008**: 采用Repository模式 (抽象化，测试友好)

#### 数据设计决策

- **ADR-009**: 采用统一内容表设计 (查询效率，扩展性)
- **ADR-010**: 暂时使用自增Long ID (性能考虑，兼容性)

### 开发模式

#### 核心模式

- **MVVM实现模式**: ViewModel + LiveData + Repository
- **Repository模式**: 统一数据访问接口，支持多数据源
- **Fragment生命周期管理**: 懒加载，内存优化
- **RecyclerView优化**: DiffUtil，ViewHolder缓存
- **异步处理**: ExecutorService (当前) → Kotlin Coroutines (计划)

#### 最佳实践

- **代码规范**: Google Java/Kotlin Style Guide
- **命名规范**: CamelCase类名，camelCase方法名，snake_case资源名
- **错误处理**: 统一异常处理，用户友好提示
- **性能优化**: 内存管理，图片优化，数据库优化
- **测试策略**: 单元测试，集成测试，UI测试

## 知识关联

### 文件间关联关系

```
architecture.md
├── 引用 tech-stack.md (技术栈详情)
├── 引用 decisions.md (架构决策)
└── 引用 patterns.md (实现模式)

tech-stack.md
├── 被 architecture.md 引用
├── 引用 best-practices.md (开发规范)
└── 提供 patterns.md 的技术基础

patterns.md
├── 被 architecture.md 引用
├── 基于 tech-stack.md 的技术栈
├── 实现 decisions.md 的决策
└── 遵循 best-practices.md 的规范

decisions.md
├── 被 architecture.md 引用
├── 影响 tech-stack.md 的选择
├── 指导 patterns.md 的实现
└── 制定 best-practices.md 的标准

best-practices.md
├── 被 tech-stack.md 引用
├── 基于 decisions.md 的决策
├── 指导 patterns.md 的实现
└── 补充 architecture.md 的规范

activeContext.md
├── 引用所有core文件
├── 记录当前工作状态
└── 指导下一步行动
```

### 外部资源关联

#### 设计参考

- `frontend/redesigned/` - UI设计参考
- `docs/redesigned/` - 开发文档参考
- `docs/original/` - 原始需求文档

#### 项目文件

- `build.gradle.kts` - 构建配置
- `app/build.gradle.kts` - 应用配置
- `app/src/main/` - 源代码目录

## 搜索索引

### 关键词索引

#### 技术关键词

- **Android**: architecture.md, tech-stack.md, patterns.md, best-practices.md
- **MVVM**: architecture.md, patterns.md, decisions.md
- **Room**: architecture.md, tech-stack.md, patterns.md, decisions.md
- **Material Design**: architecture.md, tech-stack.md, decisions.md
- **Kotlin**: architecture.md, tech-stack.md, patterns.md, best-practices.md

#### 功能关键词

- **AI日记**: architecture.md, activeContext.md
- **redesigned**: architecture.md, decisions.md, activeContext.md
- **4+3页面架构**: architecture.md, decisions.md, activeContext.md
- **离线优先**: architecture.md, decisions.md
- **隐私保护**: architecture.md, decisions.md

#### 开发关键词

- **最佳实践**: best-practices.md, patterns.md
- **代码规范**: best-practices.md, tech-stack.md
- **性能优化**: best-practices.md, patterns.md
- **测试**: best-practices.md, patterns.md, tech-stack.md

### 快速查找

#### 常用查询

- **如何实现MVVM?** → patterns.md (MVVM模式实现)
- **数据库如何设计?** → architecture.md (数据库设计) + patterns.md (Room实体设计)
- **为什么选择这个技术栈?** → decisions.md (技术选型决策)
- **代码规范是什么?** → best-practices.md (代码规范)
- **当前在做什么?** → activeContext.md (当前工作上下文)

#### 问题解决

- **构建失败** → tech-stack.md (故障排除)
- **性能问题** → best-practices.md (性能优化)
- **架构疑问** → architecture.md + decisions.md
- **实现模式** → patterns.md + best-practices.md

## 维护信息

### 更新策略

- **长期记忆**: 重大架构变更时更新
- **工作记忆**: 每个任务开始和完成时更新
- **短期记忆**: 每个任务完成后创建日志
- **索引文件**: 记忆结构变化时更新

### 质量保证

- **一致性检查**: 定期检查文件间引用的一致性
- **完整性验证**: 确保所有重要决策都有记录
- **可访问性**: 保持索引的准确性和可用性
- **版本控制**: 重要变更时记录版本信息

### 备份策略

- **Git版本控制**: 所有记忆文件纳入版本控制
- **定期备份**: 重要节点创建备份
- **恢复机制**: 提供记忆系统恢复方案

## 使用指南

### 新会话开始时

1. 阅读 `activeContext.md` 了解当前状态
2. 查看 `memory-index.md` 了解记忆结构
3. 根据需要查阅相关的长期记忆文件

### 任务执行时

1. 更新 `activeContext.md` 记录当前任务
2. 参考相关的模式和最佳实践
3. 记录重要决策和发现

### 任务完成时

1. 在 `task-logs/` 创建任务日志
2. 更新相关的长期记忆文件
3. 更新 `activeContext.md` 状态
4. 必要时更新索引文件

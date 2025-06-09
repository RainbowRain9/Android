# Android AI日记应用技术栈

## 核心技术

### 开发环境
- **IDE**: Android Studio (最新稳定版)
- **构建工具**: Gradle 8.x + Android Gradle Plugin 8.10.1
- **版本控制**: Git
- **最低SDK**: API 24 (Android 7.0)
- **目标SDK**: API 34 (Android 14)
- **编译SDK**: API 34

### 编程语言
- **主要语言**: Java 8+ / Kotlin 1.9.22
- **Java版本**: Java 11 (sourceCompatibility & targetCompatibility)
- **Kotlin JVM目标**: JVM 11

### UI框架和组件
- **UI框架**: Android Views (传统View系统)
- **设计系统**: Material Design 3
- **核心UI组件**:
  - `androidx.appcompat:appcompat:1.6.1`
  - `com.google.android.material:material:1.11.0`
  - `androidx.constraintlayout:constraintlayout:2.1.4`
  - `androidx.fragment:fragment-ktx:1.6.2`
  - `androidx.recyclerview:recyclerview:1.3.2`
  - `androidx.swiperefreshlayout:swiperefreshlayout:1.1.0`

### 架构组件
- **架构模式**: MVVM + Repository Pattern + Clean Architecture
- **生命周期组件**:
  - `androidx.lifecycle:lifecycle-viewmodel:2.7.0`
  - `androidx.lifecycle:lifecycle-livedata:2.7.0`
  - `androidx.lifecycle:lifecycle-runtime:2.7.0`
- **导航组件**: 计划集成 Navigation Component
- **依赖注入**: 计划集成 Dagger Hilt

### 数据存储
- **本地数据库**: Room Database
  - `androidx.room:room-runtime:2.6.1`
  - `androidx.room:room-compiler:2.6.1` (kapt)
- **底层数据库**: SQLite
- **数据持久化**: SharedPreferences (用户设置)
- **文件存储**: 内部存储 + 外部存储 (媒体文件)

### 异步处理
- **主要方案**: Kotlin Coroutines (计划)
- **当前方案**: Java并发工具 (ExecutorService, AsyncTask替代)
- **备选方案**: RxJava 3 (如需要)

### 网络和API
- **HTTP客户端**: 计划使用 Retrofit 2 + OkHttp 3
- **JSON解析**: 计划使用 Gson / Moshi
- **AI服务集成**: 可选的云端AI API调用

### 图片和媒体
- **图片加载**: 计划集成 Glide / Coil
- **图片处理**: Android原生 Bitmap API
- **媒体播放**: MediaPlayer / ExoPlayer (如需要)

### 测试框架
- **单元测试**: JUnit 4.13.2
- **Android测试**: 
  - `androidx.test.ext:junit:1.1.5`
  - `androidx.test.espresso:espresso-core:3.5.1`
- **模拟框架**: 计划集成 Mockito

## 项目配置

### Gradle配置
```kotlin
// 项目级 build.gradle.kts
buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.10.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.22")
    }
}

plugins {
    id("com.android.application") version "8.10.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("org.jetbrains.kotlin.kapt") version "1.9.22" apply false
}
```

```kotlin
// 应用级 build.gradle.kts
android {
    namespace = "com.example.android01"
    compileSdk = 34
    
    defaultConfig {
        applicationId = "com.example.android01"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    kotlinOptions {
        jvmTarget = "11"
    }
}
```

### 依赖管理策略
- **使用版本目录**: `gradle/libs.versions.toml` (计划)
- **依赖更新**: 定期检查和更新依赖版本
- **安全扫描**: 使用Gradle依赖检查工具

## 开发工具和最佳实践

### 代码质量
- **代码风格**: Google Java Style Guide / Kotlin Coding Conventions
- **静态分析**: 计划集成 Lint + Detekt (Kotlin)
- **代码格式化**: Android Studio内置格式化工具

### 版本控制
- **Git工作流**: Feature Branch + Pull Request
- **提交规范**: Conventional Commits
- **分支策略**: main (生产) + develop (开发) + feature/* (功能)

### 构建和部署
- **构建变体**: debug / release
- **代码混淆**: ProGuard / R8 (release版本)
- **签名配置**: 生产环境密钥管理

## 性能优化

### 内存管理
- **避免内存泄漏**: 正确管理Activity/Fragment生命周期
- **图片优化**: 使用适当的图片格式和尺寸
- **数据库优化**: 使用索引和查询优化

### 启动优化
- **延迟初始化**: 非关键组件延迟加载
- **启动画面**: 使用SplashScreen API
- **预加载**: 关键数据预加载策略

### UI性能
- **布局优化**: 减少布局层级，使用ConstraintLayout
- **RecyclerView优化**: ViewHolder模式，数据分页
- **动画优化**: 使用硬件加速，避免过度绘制

## 安全和隐私

### 数据安全
- **本地加密**: Room数据库加密 (SQLCipher)
- **网络安全**: HTTPS + Certificate Pinning
- **敏感数据**: 使用Android Keystore

### 隐私保护
- **数据最小化**: 只收集必要的用户数据
- **本地优先**: 数据主要存储在本地
- **用户控制**: 提供数据导出和删除功能

## 常用命令

### 构建命令
```bash
# 清理项目
./gradlew clean

# 构建Debug版本
./gradlew assembleDebug

# 构建Release版本
./gradlew assembleRelease

# 运行单元测试
./gradlew test

# 运行Android测试
./gradlew connectedAndroidTest

# 生成APK
./gradlew build
```

### 开发命令
```bash
# 安装Debug版本到设备
./gradlew installDebug

# 卸载应用
adb uninstall com.example.android01

# 查看日志
adb logcat | grep "Android01"

# 清除应用数据
adb shell pm clear com.example.android01
```

## 技术栈演进计划

### 短期计划 (1-2个月)
1. **集成Dagger Hilt**: 实现依赖注入
2. **添加Navigation Component**: 改进导航架构
3. **集成Retrofit**: 实现网络请求功能
4. **添加Glide**: 实现图片加载功能

### 中期计划 (3-6个月)
1. **迁移到Kotlin**: 逐步将Java代码迁移到Kotlin
2. **集成Jetpack Compose**: 部分UI组件使用Compose
3. **添加WorkManager**: 实现后台任务管理
4. **集成Room数据库迁移**: 支持数据库版本升级

### 长期计划 (6个月以上)
1. **完全Compose化**: 将所有UI迁移到Jetpack Compose
2. **模块化架构**: 将应用拆分为多个模块
3. **性能监控**: 集成Firebase Performance Monitoring
4. **CI/CD集成**: 自动化构建和部署流程

## 故障排除

### 常见问题
1. **构建失败**: 检查Gradle版本和依赖冲突
2. **Room编译错误**: 确保kapt配置正确
3. **内存不足**: 增加Gradle堆内存设置
4. **设备兼容性**: 检查最低SDK版本要求

### 调试工具
- **Android Studio Debugger**: 断点调试
- **Layout Inspector**: UI布局检查
- **Memory Profiler**: 内存使用分析
- **Network Inspector**: 网络请求监控

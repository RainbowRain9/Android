# Android应用开发项目

## 项目概述

这是一个Android应用开发项目，实现了用户登录和主界面导航功能。应用采用现代Android开发技术和架构，提供了流畅的用户体验和直观的界面设计。

## 主要功能

### 登录功能
- 用户可以通过用户名和密码进行登录
- 支持用户注册新账号
- 使用Room数据库存储用户信息
- 登录成功后自动跳转到主界面

### 主界面
- 采用底部导航栏设计，便于用户快速切换不同功能
- 包含三个主要标签页：首页、消息和个人中心
- 每个标签页都有对应的图标和文字说明
- 在用户登录状态保持期间记住最后访问的标签页

## 技术特点

- **架构**：采用Fragment + Activity的组合架构
- **UI组件**：使用Material Design组件库
- **数据存储**：使用Room持久化库进行本地数据存储
- **并发处理**：使用Java并发工具进行异步操作
- **导航**：使用BottomNavigationView实现底部导航

## 项目结构

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/android01/
│   │   │   ├── MainActivity.java        # 登录界面
│   │   │   ├── HomeActivity.java        # 主界面（包含底部导航）
│   │   │   ├── HomeFragment.java        # 首页标签页
│   │   │   ├── MessageFragment.java     # 消息标签页
│   │   │   ├── ProfileFragment.java     # 个人中心标签页
│   │   │   └── data/                    # 数据相关类
│   │   │       ├── AppDatabase.java     # 数据库类
│   │   │       ├── User.java            # 用户实体类
│   │   │       └── UserDao.java         # 数据访问对象
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── login.xml            # 登录界面布局
│   │   │   │   ├── activity_home.xml    # 主界面布局
│   │   │   │   ├── fragment_home.xml    # 首页标签页布局
│   │   │   │   ├── fragment_message.xml # 消息标签页布局
│   │   │   │   └── fragment_profile.xml # 个人中心标签页布局
│   │   │   └── menu/
│   │   │       └── bottom_nav_menu.xml  # 底部导航栏菜单
│   │   └── AndroidManifest.xml          # 应用清单文件
```

## 如何运行

1. 克隆项目到本地
2. 使用Android Studio打开项目
3. 构建并运行应用
4. 在登录界面输入用户名和密码（首次使用需要先注册）
5. 登录成功后，即可体验应用的各项功能

## 未来计划

- 添加记住密码功能
- 实现用户头像上传功能
- 添加主题切换功能
- 优化应用性能和用户体验
- 添加更多实用功能模块


# 财大优选 — 项目开发文档

> 📋 文档版本：v1.0 | 最后更新：2026-06-22

## 项目概述

云南财经大学网页设计课程作品，校园品质生活购物平台。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring Boot 4.0 + MyBatis-Plus 3.5 |
| 数据库 | MySQL 8.x |
| PC 前端 | HTML5 + CSS3 + Vanilla JS |
| 移动端管理 | uni-app (Vue.js) |
| AI 服务 | DeepSeek Chat API |

## 项目结构

```
caida/
├── src/main/java/com/caida/    # 后端源码
│   ├── controller/              # REST 控制器
│   ├── service/                 # 业务逻辑层
│   ├── mapper/                  # MyBatis-Plus 数据访问层
│   ├── entity/                  # 实体类
│   ├── config/                  # 配置类
│   └── interceptor/             # 拦截器
├── caida-qianduan/              # PC 前端
├── caida-admin-mobile/          # 移动端管理后台
└── src/main/resources/          # 配置文件与数据库脚本
```

## 代码规范

### 后端
- 包名：全小写，按层分包（controller/service/mapper/entity/config）
- 类名：大驼峰命名
- REST 路径：`/api/模块/操作` 格式
- 统一返回体：Result<T>（code + message + data）
- 数据库字段：下划线命名，实体类映射使用 @TableField

### 前端
- HTML: 语义化标签，页面内联关键样式
- CSS: CSS 自定义属性（`--main-red` 等），BEM 风格命名
- JS: ES6+ 语法，async/await 异步，fetch API 通信
- 响应式断点：1240px / 900px / 768px / 600px / 480px

### 移动端
- uni-app 规范：pages.json 统一路由配置
- 组件：单文件组件（.vue），scoped 样式

## Git 工作流

- 主分支：main
- 提交信息格式：`feat(模块): 简述`
- 推送前检查：确保代码可编译运行

## 启动说明

```bash
# 后端
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# PC 前端（Nginx 代理或直接访问后端静态资源）
# 移动端（HBuilderX 打开 caida-admin-mobile 运行）
```

## 团队成员分工

| 成员 | 负责模块 |
|------|----------|
| 成员A | 移动端整体适配、UI设计还原、交互体验优化 |
| 成员B | 核心业务功能开发、测试、bug修复 |
| 成员C | 期末演示、汇报材料、PC端功能与布局质量保障 |
| 成员D | 代码规范、Git版本管理、文档输出、团队协作统筹 |

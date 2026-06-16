# 财大优选管理端 - HBuilderX 快速启动指南

## 前置要求

1. 下载并安装 [HBuilderX](https://www.dcloud.io/hbuilderx.html)
2. 确保已安装 uni-app 编译插件（HBuilderX 内置）

## 启动步骤

### 方法一：使用 HBuilderX（推荐）

1. **打开项目**
   - 启动 HBuilderX
   - 点击菜单：文件 -> 导入 -> 从本地目录导入
   - 选择项目目录：`D:\itfan\caida\caida-admin-mobile`
   - 点击导入

2. **运行到浏览器（H5）**
   - 在 HBuilderX 中打开任意 `.vue` 文件
   - 点击工具栏的 "运行" 按钮
   - 选择 "运行到浏览器" -> "Chrome" 或其他浏览器
   - 应用将自动在浏览器中打开

3. **运行到手机模拟器**
   - 安装 Android 模拟器（如 MuMu、夜神等）
   - 点击工具栏的 "运行" 按钮
   - 选择 "运行到手机或模拟器" -> "Android App基座"
   - 等待编译完成后，应用将在模拟器中运行

4. **运行到微信开发者工具**
   - 确保已安装 [微信开发者工具](https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html)
   - 在 HBuilderX 中点击 "运行" -> "运行到小程序模拟器" -> "微信开发者工具"
   - 微信开发者工具将自动打开并加载项目

### 方法二：使用 CLI（命令行）

如果您更喜欢使用命令行，可以安装 uni-app CLI：

```bash
# 安装 Vue CLI
npm install -g @vue/cli

# 进入项目目录
cd D:\itfan\caida\caida-admin-mobile

# 运行 H5
npm run dev:h5

# 运行微信小程序
npm run dev:mp-weixin

# 运行 App
npm run dev:app-plus
```

## 项目说明

### 技术栈
- **框架**: uni-app (Vue.js)
- **UI**: 自定义样式 + 表情符号图标
- **状态管理**: 本地存储 (uni.storage)
- **网络请求**: uni.request 封装

### 功能模块
1. **登录模块** - 管理员登录
2. **首页** - 数据统计和快捷功能
3. **商品管理** - 商品列表、添加、编辑、上下架
4. **用户管理** - 用户列表、启用/禁用
5. **员工管理** - 员工列表、启用/禁用
6. **订单管理** - 订单列表、发货管理

### 默认账号
- 用户名：`admin`
- 密码：`123456`

## 注意事项

### 1. 后端API配置
当前项目使用模拟数据，如需连接真实后端：

编辑 `utils/config.js` 文件：
```javascript
const API_BASE_URL = 'http://localhost:8080/api'
```

确保后端服务已启动在 8080 端口。

### 2. 跨域问题
如果在浏览器中运行时遇到跨域问题：

**开发环境解决方案：**
- 在 `manifest.json` 中已配置代理
- H5 运行时自动使用代理转发请求

**生产环境解决方案：**
- 在后端配置 CORS
- 或使用 Nginx 反向代理

### 3. 图片资源
当前版本使用表情符号作为图标，无需额外图片文件。

如需添加自定义图片：
- Logo: `static/logo.png` (200x200px)
- 默认头像: `static/default-avatar.png` (100x100px)
- 默认商品图: `static/default-product.png` (200x200px)

### 4. TabBar 图标
当前使用纯文本 TabBar，不依赖图标文件。

如需添加图标，在 `static/tabbar/` 目录添加对应 PNG 文件，并在 `pages.json` 中配置。

## 常见问题

### Q1: 运行时报错 "找不到页面"
**解决方案：**
- 检查 `pages.json` 中的页面路径是否正确
- 确保所有页面文件都存在

### Q2: 样式显示异常
**解决方案：**
- 清除浏览器缓存
- 重启 HBuilderX
- 检查是否缺少必要的样式文件

### Q3: 网络请求失败
**解决方案：**
- 检查后端服务是否启动
- 检查 `utils/config.js` 中的 API 地址配置
- 查看浏览器控制台的网络请求详情

### Q4: 无法在真机调试
**解决方案：**
- 确保手机和电脑在同一局域网
- 在 HBuilderX 中配置真机运行证书
- 参考 uni-app 官方文档进行真机调试配置

## 开发建议

### 代码规范
- 使用 ES6+ 语法
- 组件命名采用 PascalCase
- 文件命名采用 kebab-case
- 保持代码缩进一致（2空格）

### 性能优化
- 合理使用 `v-if` 和 `v-show`
- 列表渲染使用 `:key`
- 避免在模板中使用复杂表达式
- 图片使用懒加载

### 调试技巧
- 使用 `console.log()` 输出调试信息
- 使用 HBuilderX 的调试工具
- 使用 uni-app 开发者工具查看页面结构
- 使用 Network 面板监控网络请求

## 打包发布

### H5 打包
1. 点击 HBuilderX 菜单：发行 -> 网站-H5手机版
2. 配置网站域名和标题
3. 点击发行，生成 dist 目录
4. 将 dist 目录部署到 Web 服务器

### 小程序打包
1. 点击 HBuilderX 菜单：发行 -> 小程序-微信
2. 填写小程序 AppID
3. 发行后自动生成小程序代码包
4. 使用微信开发者工具上传代码

### App 打包
1. 点击 HBuilderX 菜单：发行 -> 原生App-云打包
2. 选择 Android 或 iOS
3. 配置应用信息和证书
4. 等待云打包完成，下载安装包

## 技术支持

- uni-app 官方文档：https://uniapp.dcloud.net.cn/
- HBuilderX 文档：https://hx.dcloud.net.cn/
- Vue.js 文档：https://cn.vuejs.org/

## 更新日志

### v1.0.0 (2024-01-15)
- ✅ 完成基础框架搭建
- ✅ 实现登录功能
- ✅ 实现商品管理
- ✅ 实现用户管理
- ✅ 实现员工管理
- ✅ 实现订单管理
- ✅ 使用表情符号替代图片图标
- ✅ 优化移动端适配

---

**祝您使用愉快！** 🎉

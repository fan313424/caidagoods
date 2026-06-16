# 财大优选管理端移动端应用

## 项目说明

这是一个基于 uni-app 开发的管理端移动端应用，用于管理"财大优选"校园电商平台。

## 功能模块

### 1. 登录模块
- 管理员账号登录
- 登录状态保持
- 自动登录验证

### 2. 首页仪表盘
- 数据统计展示（用户数、商品数、订单数、员工数）
- 快捷功能入口
- 最近动态展示

### 3. 商品管理
- 商品列表查看
- 商品搜索筛选
- 添加新商品
- 编辑商品信息
- 商品上下架
- 删除商品

### 4. 用户管理
- 用户列表查看
- 用户搜索筛选
- 用户状态管理（启用/禁用）

### 5. 员工管理
- 员工列表查看
- 员工搜索筛选
- 员工状态管理（启用/禁用）

### 6. 订单管理
- 订单列表查看
- 订单搜索筛选
- 订单状态筛选
- 订单发货操作

## 技术栈

- **框架**: uni-app
- **开发工具**: HBuilderX
- **UI设计**: 自定义组件 + 渐变色设计
- **数据交互**: 模拟数据（可对接后端API）

## 开发指南

### 1. 安装 HBuilderX

下载地址: https://www.dcloud.io/hbuilderx.html

### 2. 导入项目

1. 打开 HBuilderX
2. 选择 `文件` -> `导入` -> `从本地目录导入`
3. 选择 `caida-admin-mobile` 文件夹
4. 点击 `选择` 完成导入

### 3. 运行项目

#### 方式一：浏览器运行（推荐用于调试）

1. 在 HBuilderX 中打开项目
2. 点击菜单栏 `运行` -> `运行到浏览器` -> `选择浏览器`
3. 项目会自动编译并在浏览器中打开
4. 支持热更新，修改代码后自动刷新

#### 方式二：微信开发者工具运行

1. 需要先安装微信开发者工具
2. 在 HBuilderX 中点击 `运行` -> `运行到小程序模拟器` -> `微信开发者工具`
3. 首次运行需要配置微信开发者工具路径

#### 方式三：真机运行

1. 手机连接电脑并开启USB调试
2. 在 HBuilderX 中点击 `运行` -> `运行到手机或模拟器` -> `选择设备`
3. 项目会自动安装到手机上

### 4. 调试技巧

#### 浏览器调试
- 打开浏览器开发者工具（F12）
- 使用 Console 查看日志
- 使用 Network 查看网络请求
- 使用 Elements 查看页面结构

#### 真机调试
- 使用 HBuilderX 内置调试工具
- 查看控制台输出
- 使用 vconsole 插件（需安装）

## 项目结构

```
caida-admin-mobile/
├── pages/                  # 页面文件夹
│   ├── login/             # 登录页面
│   │   └── login.vue
│   ├── index/             # 首页
│   │   └── index.vue
│   ├── product/           # 商品管理
│   │   ├── list.vue       # 商品列表
│   │   ├── add.vue        # 添加商品
│   │   └── edit.vue       # 编辑商品
│   ├── user/              # 用户管理
│   │   └── list.vue       # 用户列表
│   ├── employee/          # 员工管理
│   │   └── list.vue       # 员工列表
│   └── order/             # 订单管理
│       └── list.vue       # 订单列表
├── utils/                 # 工具类
│   └── config.js          # 配置文件
├── common/                # 公共资源
│   └── uni.css            # 通用样式
├── static/                # 静态资源
│   ├── default-avatar.png # 默认头像
│   └── default-product.png# 默认商品图
├── App.vue                # 应用配置
├── main.js                # 应用入口
├── pages.json             # 页面配置
├── manifest.json          # 应用配置
└── README.md              # 说明文档
```

## 配置说明

### API 配置

在 `utils/config.js` 中修改 API 地址：

```javascript
const API_BASE_URL = 'http://localhost:8080/api'
```

### 页面配置

在 `pages.json` 中可以配置：
- 页面路径
- 页面标题
- 导航栏样式
- 底部 TabBar

### 应用配置

在 `manifest.json` 中可以配置：
- 应用名称
- 应用图标
- 应用版本
- AppID（发布时需要）

## 后端对接说明

当前项目使用模拟数据，对接后端时需要：

### 1. 修改 API 地址

在 `utils/config.js` 中设置正确的后端地址：

```javascript
const API_BASE_URL = 'http://your-server:port/api'
```

### 2. 启用真实 API 调用

在各页面的方法中，注释掉模拟数据代码，启用真实 API 调用代码。

### 3. 后端接口要求

#### 登录接口
- URL: `/admin/login`
- Method: POST
- 参数: `{ username, password }`
- 返回: `{ code: 200, data: { token, adminInfo } }`

#### 商品管理接口
- 列表: `GET /admin/products`
- 添加: `POST /admin/products`
- 编辑: `PUT /admin/products/:id`
- 删除: `DELETE /admin/products/:id`
- 状态: `PUT /admin/products/:id/status`

#### 用户管理接口
- 列表: `GET /admin/users`
- 状态: `PUT /admin/users/:id/status`

#### 员工管理接口
- 列表: `GET /admin/employees`
- 状态: `PUT /admin/employees/:id/status`

#### 订单管理接口
- 列表: `GET /admin/orders`
- 发货: `PUT /admin/orders/:id/ship`

#### 统计数据接口
- URL: `GET /admin/stats`
- 返回: `{ code: 200, data: { userCount, productCount, orderCount, employeeCount } }`

## 发布说明

### 微信小程序发布

1. 在 HBuilderX 中点击 `发行` -> `小程序-微信`
2. 输入微信小程序的 AppID
3. 点击发行，等待编译完成
4. 在微信开发者工具中上传代码
5. 在微信公众平台提交审核

### App 发布

1. 在 HBuilderX 中点击 `发行` -> `原生App-云打包`
2. 选择打包平台（Android/iOS）
3. 配置证书信息
4. 点击打包，等待完成
5. 下载安装包进行分发

## 注意事项

1. **开发环境**: 建议使用浏览器进行开发和调试，效率最高
2. **真机测试**: 发布前务必在真机上进行测试
3. **网络请求**: 真机测试时需要配置正确的后端地址
4. **图片上传**: 需要后端提供文件上传接口
5. **权限验证**: 所有管理接口需要验证 token

## 常见问题

### 1. 运行报错：找不到页面
- 检查 `pages.json` 中是否正确配置了页面路径
- 检查页面文件是否存在

### 2. 网络请求失败
- 检查后端服务是否启动
- 检查 API 地址是否正确
- 检查网络权限配置

### 3. 样式不生效
- 检查样式是否使用了 scoped
- 检查样式单位是否正确（使用 rpx）

### 4. 图片不显示
- 检查图片路径是否正确
- 检查图片格式是否支持
- 使用网络图片时检查域名白名单配置

## 技术支持

如有问题，请联系开发团队。

---

**开发团队**: 财大优选技术部  
**创建时间**: 2024年1月  
**版本**: v1.0.0

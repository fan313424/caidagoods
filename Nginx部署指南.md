# Nginx部署指南 - 财大优选购物系统

## 📋 目录结构
```
D:/itfan/caida/
├── caida-qianduan/          # 前端静态资源目录
│   ├── index.html
│   ├── login.html
│   ├── register.html
│   ├── script.js
│   ├── style.css
│   └── images/              # 图片资源
├── src/                     # 后端Spring Boot项目
├── nginx.conf               # Nginx配置文件
└── pom.xml
```

## 🚀 部署步骤

### 1. 安装Nginx

#### Windows系统：
1. 下载Nginx: http://nginx.org/en/download.html (选择Stable version)
2. 解压到任意目录，例如: `C:\nginx`
3. 将项目中的 `nginx.conf` 复制到 `C:\nginx\conf\nginx.conf`

#### Linux系统：
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install nginx

# CentOS/RHEL
sudo yum install epel-release
sudo yum install nginx
```

### 2. 配置Nginx

#### Windows系统：
- 配置文件位置: `C:\nginx\conf\nginx.conf`
- 已为您生成配置文件: `D:/itfan/caida/nginx.conf`
- 复制配置文件内容到Nginx配置目录

#### Linux系统：
```bash
# 复制配置文件
sudo cp D:/itfan/caida/nginx.conf /etc/nginx/nginx.conf

# 测试配置是否正确
sudo nginx -t

# 重启Nginx
sudo systemctl restart nginx
```

### 3. 启动后端服务

```bash
# 进入项目根目录
cd D:/itfan/caida

# 方式1: 使用Maven启动
mvn spring-boot:run

# 方式2: 打包后启动
mvn clean package
java -jar target/caida-0.0.1-SNAPSHOT.jar

# 方式3: 使用提供的批处理文件
启动后端.bat
```

**重要**: 确保后端服务运行在 `http://localhost:8080`

### 4. 启动Nginx

#### Windows系统：
```bash
# 进入Nginx目录
cd C:\nginx

# 启动Nginx
start nginx

# 或使用Windows服务方式启动
nginx.exe
```

#### Linux系统：
```bash
# 启动Nginx
sudo systemctl start nginx

# 设置开机自启
sudo systemctl enable nginx

# 查看状态
sudo systemctl status nginx
```

### 5. 访问应用

打开浏览器，访问：
- 主页: http://localhost
- 登录页: http://localhost/login.html
- 注册页: http://localhost/register.html

## 🔧 配置说明

### Nginx配置详解

```nginx
# 前端静态资源映射
location / {
    root D:/itfan/caida/caida-qianduan;
    index index.html;
}

# 后端API代理
location /api/ {
    proxy_pass http://localhost:8080/api/;
}
```

### 前端API地址配置

前端代码中已配置API基础地址：
```javascript
const API_BASE_URL = 'http://localhost:8080/api';
```

**使用Nginx后，可以修改为**：
```javascript
const API_BASE_URL = '/api';  // 使用相对路径，通过Nginx代理
```

这样前端请求会通过Nginx转发到后端，避免跨域问题。

## 📝 常用命令

### Windows系统：
```bash
# 启动
nginx.exe

# 停止
nginx.exe -s stop

# 重启
nginx.exe -s reload

# 测试配置
nginx.exe -t
```

### Linux系统：
```bash
# 启动
sudo systemctl start nginx

# 停止
sudo systemctl stop nginx

# 重启
sudo systemctl restart nginx

# 重新加载配置
sudo systemctl reload nginx

# 测试配置
sudo nginx -t
```

## 🔍 故障排查

### 1. 无法访问前端页面
- 检查Nginx是否启动成功
- 检查配置文件中的root路径是否正确
- 查看Nginx错误日志: `C:\nginx\logs\error.log` (Windows) 或 `/var/log/nginx/error.log` (Linux)

### 2. API请求失败
- 确认后端服务是否正常运行在8080端口
- 检查防火墙是否开放80端口
- 查看浏览器控制台Network选项卡，检查请求状态

### 3. 跨域问题
- Nginx已配置反向代理，理论上不存在跨域问题
- 如果仍有跨域问题，检查后端CORS配置

### 4. 修改前端代码后不生效
- 清除浏览器缓存
- 重启Nginx: `nginx.exe -s reload` (Windows) 或 `sudo systemctl reload nginx` (Linux)

## 🌟 生产环境建议

### 1. 安全加固
```nginx
# 隐藏Nginx版本号
server_tokens off;

# 限制请求大小
client_max_body_size 10M;

# 防止点击劫持
add_header X-Frame-Options "SAMEORIGIN";
```

### 2. 性能优化
```nginx
# 开启文件缓存
open_file_cache max=1000 inactive=20s;

# 增加并发连接数
worker_processes auto;
worker_connections 2048;
```

### 3. HTTPS配置（推荐）
```nginx
server {
    listen 443 ssl;
    server_name yourdomain.com;
    
    ssl_certificate /path/to/cert.pem;
    ssl_certificate_key /path/to/key.pem;
    
    # 其他配置...
}
```

## 📊 监控与日志

### 日志位置
- Windows: `C:\nginx\logs\`
- Linux: `/var/log/nginx/`

### 日志文件
- `access.log`: 访问日志
- `error.log`: 错误日志

### 查看实时日志
```bash
# Linux
sudo tail -f /var/log/nginx/access.log

# Windows (使用PowerShell)
Get-Content C:\nginx\logs\access.log -Wait
```

## 🎯 下一步

1. ✅ Nginx配置文件已创建
2. ⏭️ 安装并启动Nginx服务
3. ⏭️ 启动Spring Boot后端服务
4. ⏭️ 访问 http://localhost 测试系统

## 💡 提示

- 前端静态资源已配置缓存，修改后需要清除浏览器缓存
- 后端API通过Nginx代理，无需担心跨域问题
- 建议在生产环境中使用HTTPS加密传输

# Nginx快速启动指南

## 🚀 快速开始（3步完成）

### 第一步：启动后端服务

双击运行项目根目录下的 `启动后端.bat` 文件，或在命令行中执行：
```bash
mvn spring-boot:run
```

等待看到 "Started CaidaApplication" 字样，表示后端启动成功（端口8080）。

### 第二步：启动Nginx

#### Windows系统：
1. 下载Nginx: http://nginx.org/en/download.html
2. 解压到 `C:\nginx`
3. 将项目中的 `nginx.conf` 文件内容复制到 `C:\nginx\conf\nginx.conf`
4. 双击 `nginx.exe` 或在命令行执行：
   ```bash
   cd C:\nginx
   start nginx
   ```

#### Linux系统：
```bash
# 复制配置文件
sudo cp nginx.conf /etc/nginx/nginx.conf

# 测试配置
sudo nginx -t

# 启动Nginx
sudo systemctl start nginx
```

### 第三步：访问应用

打开浏览器，访问：http://localhost

- 主页: http://localhost
- 登录: http://localhost/login.html
- 注册: http://localhost/register.html

## ✅ 验证部署成功

1. **检查后端**: 访问 http://localhost:8080/api/product/list 应返回商品数据
2. **检查前端**: 访问 http://localhost 应显示财大优选首页
3. **检查代理**: 在前端页面进行登录操作，查看浏览器控制台Network选项卡，API请求应成功

## 🛠️ 常用命令

### Windows:
```bash
# 重启Nginx（修改配置后）
nginx -s reload

# 停止Nginx
nginx -s stop

# 测试配置是否正确
nginx -t
```

### Linux:
```bash
# 重启Nginx
sudo systemctl reload nginx

# 停止Nginx
sudo systemctl stop nginx

# 查看Nginx状态
sudo systemctl status nginx
```

## ⚠️ 注意事项

1. **端口冲突**: 确保80端口未被其他程序占用（如IIS、Apache等）
2. **防火墙**: Windows可能需要允许nginx通过防火墙
3. **路径问题**: nginx.conf中的路径使用正斜杠 `/` 或双反斜杠 `\\`
4. **缓存问题**: 修改前端代码后，清除浏览器缓存或使用Ctrl+F5强制刷新

## 📝 已完成的配置

✅ 创建了 `nginx.conf` 配置文件  
✅ 创建了详细的 `Nginx部署指南.md`  
✅ 修改了前端API地址为相对路径 `/api`  
✅ 配置了静态资源缓存和压缩  
✅ 配置了反向代理避免跨域问题  

## 🎯 下一步

现在您只需要：
1. 安装Nginx（如果还没有）
2. 复制配置文件
3. 启动后端和Nginx
4. 访问 http://localhost

就这么简单！🎉
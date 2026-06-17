@echo off
chcp 65001 >nul
cd /d "%~dp0"

echo ========================================
echo   财大优选 - 后端启动
echo ========================================
echo.

echo [步骤1] 检查 Java 环境...
java -version 2>nul
if errorlevel 1 (
    echo [错误] 未找到 Java！请安装 JDK 17+
    pause
    exit /b 1
)

echo.
echo [步骤2] 确保 MySQL 已启动，数据库 caida_db 已创建
echo.

echo [步骤3] 启动后端服务（local 配置，含 API Key）...
echo   首次运行需下载依赖，请耐心等待...
echo.

call mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local

echo.
echo 后端服务已停止
pause

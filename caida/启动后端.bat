@echo off
chcp 65001 >nul
echo ========================================
echo   财大优选 - 购物车功能快速启动
echo ========================================
echo.

echo [步骤1] 检查MySQL是否运行...
echo 请确保MySQL服务已启动，并已执行以下操作：
echo   1. 创建数据库 caida_db
echo   2. 执行 src/main/resources/db/schema.sql 脚本
echo   3. 修改 application.properties 中的数据库密码
echo.
pause

echo.
echo [步骤2] 启动后端服务...
echo 正在启动Spring Boot应用...
echo.

cd /d "%~dp0"
call mvn spring-boot:run

echo.
echo 后端服务已停止
pause

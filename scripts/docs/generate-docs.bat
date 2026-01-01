@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

REM 切换到脚本所在目录
cd /d "%~dp0"

echo ========================================
echo   SwiftFrame API 文档自动生成工具
echo ========================================
echo.

set BASE_URL=http://localhost:8081/swift
set GENERATOR_VERSION=6.6.0
set GENERATOR_JAR=openapi-generator-cli.jar
set GENERATOR_URL=https://repo1.maven.org/maven2/org/openapitools/openapi-generator-cli/%GENERATOR_VERSION%/openapi-generator-cli-%GENERATOR_VERSION%.jar

REM 清理临时文件
if exist openapi-*.json del /q openapi-*.json
if exist openapi.json del /q openapi.json

echo [1/7] 检查应用是否运行...
curl -s -o nul %BASE_URL%/v3/api-docs
if errorlevel 1 (
    echo ❌ 错误：无法连接到应用
    echo 请确保应用已启动，并且运行在 %BASE_URL%
    echo.
    echo 启动应用命令：
    echo   cd swift-start
    echo   mvn spring-boot:run
    pause
    exit /b 1
)
echo ✓ 应用运行正常
echo.

echo [2/7] 导出完整 OpenAPI 规范...
curl -s %BASE_URL%/v3/api-docs -o openapi-full.json
if errorlevel 1 (
    echo ❌ 导出 OpenAPI 规范失败
    pause
    exit /b 1
)
echo ✓ OpenAPI 规范已导出到 openapi-full.json
echo.

echo [3/7] 检查 openapi-generator-cli.jar...
if not exist "%GENERATOR_JAR%" (
    echo ⚠ 未找到 %GENERATOR_JAR%，正在下载...
    echo   下载地址：%GENERATOR_URL%
    echo.
    powershell -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Write-Host '  正在下载...'; $ProgressPreference = 'SilentlyContinue'; Invoke-WebRequest -Uri '%GENERATOR_URL%' -OutFile '%GENERATOR_JAR%'; $size = (Get-Item '%GENERATOR_JAR%').Length; Write-Host \"  下载完成，文件大小：$size 字节\"; if ($size -lt 1000000) { Write-Host '  错误：文件太小，可能下载失败'; Remove-Item '%GENERATOR_JAR%'; exit 1 } }"
    if errorlevel 1 (
        echo ❌ 下载失败或文件不完整
        echo   请手动下载：%GENERATOR_URL%
        pause
        exit /b 1
    )
    echo ✓ 下载完成
) else (
    echo ✓ %GENERATOR_JAR% 已存在
)
echo.

echo [4/7] 清理旧文档...
if exist "..\..\docs\api-docs" (
    rmdir /s /q "..\..\docs\api-docs"
)
mkdir "..\..\docs\api-docs"
echo ✓ 文档目录已清理
echo.

REM 定义模块列表
set MODULES=鉴权管理 用户管理 角色管理

echo [5/7] 生成模块文档...

REM 使用 Python 脚本拆分 OpenAPI 规范
python split-openapi.py

if errorlevel 1 (
    echo ❌ 拆分 OpenAPI 规范失败
    echo   请确保已安装 Python 3
) else (
    echo ✓ OpenAPI 规范拆分完成
    
    echo.
    echo   开始生成模块 HTML 文档...
    echo.
    
    for %%M in (%MODULES%) do (
        echo   模块: %%M
        
        if exist "openapi-%%M.json" (
            echo   正在生成 %%M 的 HTML 文档...
            java -jar "%GENERATOR_JAR%" generate -i openapi-%%M.json -g html -o ..\..\docs\api-docs\%%M --skip-validate-spec
            
            if errorlevel 1 (
                echo   ❌ 生成 %%M 的 HTML 文档失败
            ) else (
                echo   ✓ %%M 的 HTML 文档已生成
            )
        ) else (
            echo   ❌ openapi-%%M.json 不存在
        )
        echo.
    )
)

echo.
echo [6/7] 生成总览文档...
java -jar "%GENERATOR_JAR%" generate -i openapi-full.json -g html -o ..\..\docs\api-docs\总览 --skip-validate-spec
if errorlevel 1 (
    echo ❌ 生成总览文档失败
) else (
    echo ✓ 总览文档已生成
)
echo.

echo [7/7] 为HTML文档添加UTF-8编码声明...
echo   检查Python环境...
python --version >nul 2>&1
if errorlevel 1 (
    echo   ⚠ 警告: 未找到Python，跳过UTF-8编码修复
    echo   请安装Python 3并添加到PATH环境变量
) else (
    echo   ✓ Python环境正常
    echo.
    
    for /d %%d in ("..\..\docs\api-docs\*") do (
        if exist "%%d\index.html" (
            echo   处理: %%~nxd
            python "%~dp0fix_encoding.py" "%%d\index.html"
            if !errorlevel! equ 0 (
                echo   ✓ UTF-8编码已添加
            ) else (
                echo   ❌ UTF-8编码添加失败
            )
            echo.
        )
    )
)
echo.

echo ========================================
echo   生成完成！
echo ========================================
echo.
echo 📚 模块文档位置：
for %%M in (%MODULES%) do (
    echo   - %%M: ..\..\docs\api-docs\%%M\index.html
)
echo   - 总览: ..\..\docs\api-docs\总览\index.html
echo.
echo 🌐 在线文档：http://localhost:8081/swift/swagger-ui/index.html
echo 📋 OpenAPI JSON：%BASE_URL%/v3/api-docs
echo.
echo 请用浏览器打开 ..\..\docs\api-docs\总览\index.html 查看完整文档
echo 或打开各模块目录查看独立文档
echo.
pause

@echo off
chcp 65001 >nul
echo ========================================
echo   SwiftFrame API 文档自动生成工具
echo ========================================
echo.

set API_URL=http://localhost:8081/swift/v3/api-docs
set GENERATOR_VERSION=6.6.0
set GENERATOR_JAR=openapi-generator-cli.jar
set GENERATOR_URL=https://repo1.maven.org/maven2/org/openapitools/openapi-generator-cli/%GENERATOR_VERSION%/openapi-generator-cli-%GENERATOR_VERSION%.jar

echo [1/4] 检查应用是否运行...
curl -s -o nul %API_URL%
if errorlevel 1 (
    echo ❌ 错误：无法连接到应用
    echo 请确保应用已启动，并且运行在 %API_URL%
    echo.
    echo 启动应用命令：
    echo   cd swift-start
    echo   mvn spring-boot:run
    pause
    exit /b 1
)
echo ✓ 应用运行正常
echo.

echo [2/4] 导出 OpenAPI 规范...
curl -s %API_URL% -o openapi.json
if errorlevel 1 (
    echo ❌ 导出 OpenAPI 规范失败
    pause
    exit /b 1
)
echo ✓ OpenAPI 规范已导出到 openapi.json
echo.

echo [3/4] 检查 openapi-generator-cli.jar...
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

echo [4/4] 生成 HTML 文档...
if exist "docs\api-docs" (
    echo 正在清理旧文档...
    rmdir /s /q "docs\api-docs"
)

java -jar "%GENERATOR_JAR%" generate -i openapi.json -g html -o docs\api-docs --skip-validate-spec
if errorlevel 1 (
    echo ❌ 文档生成失败
    pause
    exit /b 1
)
echo ✓ 文档生成成功
echo.

echo ========================================
echo   生成完成！
echo ========================================
echo.
echo 📄 文档位置：docs\api-docs\index.html
echo 🌐 在线文档：http://localhost:8081/swift/swagger-ui/index.html
echo 📋 OpenAPI JSON：%API_URL%
echo.
echo 请用浏览器打开 docs\api-docs\index.html 查看文档
echo.
pause

#!/bin/bash

echo "========================================"
echo "  SwiftFrame API 文档自动生成工具"
echo "========================================"
echo

API_URL="http://localhost:8081/swift/v3/api-docs"
GENERATOR_VERSION="7.0.1"
GENERATOR_JAR="openapi-generator-cli.jar"
GENERATOR_URL="https://repo1.maven.org/maven2/org/openapitools/openapi-generator-cli/${GENERATOR_VERSION}/${GENERATOR_JAR}"

# 检查应用是否运行
echo "[1/4] 检查应用是否运行..."
if ! curl -s -f "$API_URL" > /dev/null; then
    echo "❌ 错误：无法连接到应用"
    echo "请确保应用已启动，并且运行在 $API_URL"
    echo
    echo "启动应用命令："
    echo "  cd swift-start"
    echo "  mvn spring-boot:run"
    exit 1
fi
echo "✓ 应用运行正常"
echo

# 导出 OpenAPI 规范
echo "[2/4] 导出 OpenAPI 规范..."
if ! curl -s "$API_URL" -o openapi.json; then
    echo "❌ 导出 OpenAPI 规范失败"
    exit 1
fi
echo "✓ OpenAPI 规范已导出到 openapi.json"
echo

# 检查 openapi-generator-cli.jar
echo "[3/4] 检查 openapi-generator-cli.jar..."
if [ ! -f "$GENERATOR_JAR" ]; then
    echo "⚠ 未找到 $GENERATOR_JAR，正在下载..."
    if ! curl -L -o "$GENERATOR_JAR" "$GENERATOR_URL"; then
        echo "❌ 下载失败，请手动下载："
        echo "  $GENERATOR_URL"
        exit 1
    fi
    echo "✓ 下载完成"
else
    echo "✓ $GENERATOR_JAR 已存在"
fi
echo

# 生成 HTML 文档
echo "[4/4] 生成 HTML 文档..."
if [ -d "docs/api-docs" ]; then
    echo "正在清理旧文档..."
    rm -rf "docs/api-docs"
fi

if ! java -jar "$GENERATOR_JAR" generate -i openapi.json -g html2 -o docs/api-docs; then
    echo "❌ 文档生成失败"
    exit 1
fi
echo "✓ 文档生成成功"
echo

echo "========================================"
echo "  生成完成！"
echo "========================================"
echo
echo "📄 文档位置：docs/api-docs/index.html"
echo "🌐 在线文档：http://localhost:8081/swift/swagger-ui/index.html"
echo "📋 OpenAPI JSON：$API_URL"
echo
echo "请用浏览器打开 docs/api-docs/index.html 查看文档"
echo

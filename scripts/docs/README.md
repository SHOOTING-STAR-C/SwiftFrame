# API 文档生成工具

本目录包含 SwiftFrame 项目 API 离线文档的生成工具。

## 文件说明

- `generate-docs.bat` - Windows 批处理脚本，用于生成 API 文档
- `split-openapi.py` - Python 脚本，用于按模块拆分 OpenAPI 规范
- `fix_encoding.py` - Python 脚本，为 HTML 文档自动添加 UTF-8 编码声明
- `openapi-generator-cli.jar` - OpenAPI 文档生成器（首次运行时自动下载）
- `openapi-*.json` - 生成的 OpenAPI JSON 规范文件

## 使用方法

### 1. 确保应用已启动

```bash
cd swift-start
mvn spring-boot:run
```

### 2. 确保已安装 Python 3

如果尚未安装，请从 Python 官网下载安装：https://www.python.org/downloads/

### 3. 运行文档生成脚本

```bash
cd scripts\docs
generate-docs.bat
```

## 生成的文档位置

文档将生成在项目根目录的 `docs/api-docs/` 目录下：

```
docs/api-docs/
├── 鉴权管理/
│   └── index.html
├── 用户管理/
│   └── index.html
├── 角色管理/
│   └── index.html
└── 总览/
    └── index.html
```

## 功能特点

1. **模块化文档**：每个模块生成独立的 HTML 文档
2. **总览文档**：包含所有模块的完整文档
3. **自动分类**：根据 Controller 的 @Tag 注解自动分类接口
4. **自动下载**：首次运行时自动下载文档生成器
5. **UTF-8 编码自动修复**：自动为生成的 HTML 文档添加 UTF-8 编码声明，解决中文乱码问题

## 在线文档

应用启动后，也可以通过 Swagger UI 查看在线文档：

http://localhost:8081/swift/swagger-ui/index.html

## 注意事项

- 确保 Python 3 已安装并添加到系统 PATH
- 确保应用正在运行（端口 8081）
- 生成的文档是静态 HTML 文件，可以直接在浏览器中打开
- 如果 Python 未安装，文档仍会生成，但不会自动添加 UTF-8 编码声明

## UTF-8 编码修复说明

生成的 HTML 文档会自动添加 UTF-8 编码声明（`<meta charset="UTF-8">`），确保中文字符正确显示。

### 手动修复编码

如果需要手动修复已有文档的编码问题，可以运行：

```bash
cd scripts\docs
python fix_encoding.py "路径\to\文档\index.html"
```

### 支持的编码

脚本会自动检测并支持以下编码：
- UTF-8
- GBK
- GB2312
- GB18030
- Latin-1

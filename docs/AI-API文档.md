# AI模块API文档

## 概述

AI模块提供了完整的AI聊天功能，包括供应商管理、模型管理、会话管理和消息管理等功能。

## 基础信息

- **基础URL**: `/api/ai`
- **认证方式**: Bearer Token (需要在请求头中添加 `Authorization: Bearer <token>`)
- **Content-Type**: `application/json`

## 目录

1. [AI聊天接口](#ai聊天接口)
2. [AI消息接口](#ai消息接口)
3. [AI会话接口](#ai会话接口)
4. [AI模型接口](#ai模型接口)
5. [AI供应商接口](#ai供应商接口)
6. [数据模型](#数据模型)

## AI聊天接口

### 发送聊天消息

**接口**: `POST /api/ai/chat`

**功能描述**: 向AI模型发送消息并获取回复，支持新会话和已有会话。如果是新会话，会自动创建一个默认会话。

**请求参数**:

```json
{
  "sessionId": "1723456789012345678",  // 可选，会话ID，不传则创建新会话
  "modelId": 1,                          // 必填，模型ID
  "message": "你好，请问你是谁？",       // 必填，消息内容
  "stream": false                        // 可选，是否流式返回，默认false
}
```

**响应参数**:

```json
{
  "code": "PUB-200-00",
  "msg": "操作成功",
  "data": {
    "sessionId": "1723456789012345678",
    "messageId": 1,
    "role": "assistant",
    "content": "你好！我是AI助手。",
    "tokensUsed": 10,
    "createdAt": "2024-01-04T15:30:00"
  }
}
```

**错误码**:
- `400`: 参数验证失败
- `404`: 模型不存在或会话不存在

### 获取聊天历史

**接口**: `GET /api/ai/chat/history?sessionId={sessionId}`

**功能描述**: 获取指定会话的所有聊天消息历史

**请求参数**:
- `sessionId` (必填): 会话ID

**响应参数**:

```json
{
  "code": "PUB-200-00",
  "msg": "操作成功",
  "data": [
    {
      "sessionId": "1723456789012345678",
      "messageId": 1,
      "role": "user",
      "content": "你好",
      "tokensUsed": 5,
      "createdAt": "2024-01-04T15:30:00"
    },
    {
      "sessionId": "1723456789012345678",
      "messageId": 2,
      "role": "assistant",
      "content": "你好！我是AI助手。",
      "tokensUsed": 10,
      "createdAt": "2024-01-04T15:30:05"
    }
  ]
}
```

**错误码**:
- `404`: 会话不存在

## AI消息接口

### 获取会话的所有消息

**接口**: `GET /api/ai/messages?sessionId={sessionId}`

**功能描述**: 获取指定会话的所有聊天消息，按时间顺序返回

**请求参数**:
- `sessionId` (必填): 会话ID

**响应参数**:

```json
{
  "code": "PUB-200-00",
  "msg": "操作成功",
  "data": [
    {
      "id": 1,
      "sessionId": "1723456789012345678",
      "role": "user",
      "content": "你好",
      "tokensUsed": 5,
      "createdAt": "2024-01-04T15:30:00"
    }
  ]
}
```

## AI会话接口

### 创建会话

**接口**: `POST /api/ai/sessions`

**功能描述**: 创建一个新的AI聊天会话

**请求参数**:
- `userId` (必填): 用户ID
- `modelId` (必填): 模型ID
- `title` (可选): 会话标题

**响应参数**:

```json
{
  "code": "PUB-200-00",
  "msg": "操作成功",
  "data": {
    "id": 1,
    "sessionId": "1723456789012345678",
    "userId": "user123",
    "title": "新会话",
    "modelId": 1,
    "modelName": "GPT-3.5 Turbo",
    "createdAt": "2024-01-04T15:30:00",
    "updatedAt": "2024-01-04T15:30:00"
  }
}
```

### 更新会话

**接口**: `PUT /api/ai/sessions/{sessionId}?title={title}`

**功能描述**: 更新指定会话的标题

**请求参数**:
- `sessionId` (路径参数, 必填): 会话ID
- `title` (查询参数, 必填): 新的会话标题

**响应参数**:

```json
{
  "code": "PUB-200-00",
  "msg": "操作成功",
  "data": null
}
```

### 删除会话

**接口**: `DELETE /api/ai/sessions/{sessionId}`

**功能描述**: 删除指定会话及其所有消息

**请求参数**:
- `sessionId` (路径参数, 必填): 会话ID

**响应参数**:

```json
{
  "code": "PUB-200-00",
  "msg": "操作成功",
  "data": null
}
```

### 获取会话详情

**接口**: `GET /api/ai/sessions/{sessionId}`

**功能描述**: 根据会话ID获取会话详细信息

**请求参数**:
- `sessionId` (路径参数, 必填): 会话ID

**响应参数**:

```json
{
  "code": "PUB-200-00",
  "msg": "操作成功",
  "data": {
    "id": 1,
    "sessionId": "1723456789012345678",
    "userId": "user123",
    "title": "新会话",
    "modelId": 1,
    "modelName": "GPT-3.5 Turbo",
    "createdAt": "2024-01-04T15:30:00",
    "updatedAt": "2024-01-04T15:30:00"
  }
}
```

### 获取用户的会话列表（分页）

**接口**: `GET /api/ai/sessions?userId={userId}&page={page}&size={size}`

**功能描述**: 分页获取指定用户的所有会话

**请求参数**:
- `userId` (必填): 用户ID
- `page` (可选): 页码，默认1
- `size` (可选): 每页大小，默认10

**响应参数**:

```json
{
  "code": "PUB-200-00",
  "msg": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "sessionId": "1723456789012345678",
        "userId": "user123",
        "title": "新会话",
        "modelId": 1,
        "modelName": "GPT-3.5 Turbo",
        "createdAt": "2024-01-04T15:30:00",
        "updatedAt": "2024-01-04T15:30:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

## AI模型接口

### 创建模型

**接口**: `POST /api/ai/models`

**功能描述**: 创建一个新的AI模型配置

**请求参数**:

```json
{
  "modelCode": "gpt-3.5-turbo",
  "modelName": "GPT-3.5 Turbo",
  "providerId": 1,
  "enabled": true,
  "maxTokens": 4096,
  "contextLength": 16384,
  "description": "OpenAI 经典的 3.5 模型"
}
```

**响应参数**:

```json
{
  "code": "PUB-200-00",
  "msg": "操作成功",
  "data": {
    "id": 1,
    "modelCode": "gpt-3.5-turbo",
    "modelName": "GPT-3.5 Turbo",
    "providerId": 1,
    "enabled": true,
    "maxTokens": 4096,
    "contextLength": 16384,
    "description": "OpenAI 经典的 3.5 模型",
    "createdAt": "2024-01-04T15:30:00",
    "updatedAt": "2024-01-04T15:30:00"
  }
}
```

### 更新模型

**接口**: `PUT /api/ai/models/{id}`

**功能描述**: 更新指定ID的模型信息

**请求参数**:
- `id` (路径参数, 必填): 模型ID
- `body` (请求体): 模型信息

**响应参数**: 同创建模型

### 删除模型

**接口**: `DELETE /api/ai/models/{id}`

**功能描述**: 删除指定ID的模型

**请求参数**:
- `id` (路径参数, 必填): 模型ID

**响应参数**:

```json
{
  "code": "PUB-200-00",
  "msg": "操作成功",
  "data": null
}
```

### 获取模型详情

**接口**: `GET /api/ai/models/{id}`

**功能描述**: 根据ID获取模型详细信息

**请求参数**:
- `id` (路径参数, 必填): 模型ID

**响应参数**:

```json
{
  "code": "PUB-200-00",
  "msg": "操作成功",
  "data": {
    "id": 1,
    "modelCode": "gpt-3.5-turbo",
    "modelName": "GPT-3.5 Turbo",
    "providerId": 1,
    "providerName": "OpenAI",
    "enabled": true,
    "maxTokens": 4096,
    "contextLength": 16384,
    "description": "OpenAI 经典的 3.5 模型",
    "createdAt": "2024-01-04T15:30:00"
  }
}
```

### 获取模型列表（分页）

**接口**: `GET /api/ai/models?page={page}&size={size}&providerId={providerId}&enabled={enabled}`

**功能描述**: 分页获取模型列表，支持按供应商ID和启用状态筛选

**请求参数**:
- `page` (可选): 页码，默认1
- `size` (可选): 每页大小，默认10
- `providerId` (可选): 供应商ID
- `enabled` (可选): 是否启用

**响应参数**:

```json
{
  "code": "PUB-200-00",
  "msg": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "modelCode": "gpt-3.5-turbo",
        "modelName": "GPT-3.5 Turbo",
        "providerId": 1,
        "providerName": "OpenAI",
        "enabled": true,
        "maxTokens": 4096,
        "contextLength": 16384,
        "description": "OpenAI 经典的 3.5 模型",
        "createdAt": "2024-01-04T15:30:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

### 测试模型连接

**接口**: `POST /api/ai/models/{id}/test`

**功能描述**: 测试指定模型的API连接是否正常

**请求参数**:
- `id` (路径参数, 必填): 模型ID

**响应参数**:

```json
{
  "code": "PUB-200-00",
  "msg": "操作成功",
  "data": {
    "success": true,
    "message": "连接成功",
    "latency": 150
  }
}
```

## AI供应商接口

### 创建供应商

**接口**: `POST /api/ai/providers`

**功能描述**: 创建一个新的AI供应商配置

**请求参数**:

```json
{
  "providerCode": "openai",
  "providerName": "OpenAI",
  "baseUrl": "https://api.openai.com/v1",
  "enabled": true,
  "priority": 1,
  "description": "OpenAI 官方供应商"
}
```

**响应参数**:

```json
{
  "code": "PUB-200-00",
  "msg": "操作成功",
  "data": {
    "id": 1,
    "providerCode": "openai",
    "providerName": "OpenAI",
    "baseUrl": "https://api.openai.com/v1",
    "enabled": true,
    "priority": 1,
    "description": "OpenAI 官方供应商",
    "healthy": true,
    "createdAt": "2024-01-04T15:30:00",
    "updatedAt": "2024-01-04T15:30:00"
  }
}
```

### 更新供应商

**接口**: `PUT /api/ai/providers/{id}`

**功能描述**: 更新指定ID的供应商信息

**请求参数**:
- `id` (路径参数, 必填): 供应商ID
- `body` (请求体): 供应商信息

**响应参数**: 同创建供应商

### 删除供应商

**接口**: `DELETE /api/ai/providers/{id}`

**功能描述**: 删除指定ID的供应商

**请求参数**:
- `id` (路径参数, 必填): 供应商ID

**响应参数**:

```json
{
  "code": "PUB-200-00",
  "msg": "操作成功",
  "data": null
}
```

### 获取供应商详情

**接口**: `GET /api/ai/providers/{id}`

**功能描述**: 根据ID获取供应商详细信息

**请求参数**:
- `id` (路径参数, 必填): 供应商ID

**响应参数**:

```json
{
  "code": "PUB-200-00",
  "msg": "操作成功",
  "data": {
    "id": 1,
    "providerCode": "openai",
    "providerName": "OpenAI",
    "baseUrl": "https://api.openai.com/v1",
    "enabled": true,
    "priority": 1,
    "description": "OpenAI 官方供应商",
    "healthy": true,
    "createdAt": "2024-01-04T15:30:00"
  }
}
```

### 获取供应商列表（分页）

**接口**: `GET /api/ai/providers?page={page}&size={size}&enabled={enabled}`

**功能描述**: 分页获取供应商列表，支持按启用状态筛选

**请求参数**:
- `page` (可选): 页码，默认1
- `size` (可选): 每页大小，默认10
- `enabled` (可选): 是否启用

**响应参数**:

```json
{
  "code": "PUB-200-00",
  "msg": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "providerCode": "openai",
        "providerName": "OpenAI",
        "baseUrl": "https://api.openai.com/v1",
        "enabled": true,
        "priority": 1,
        "description": "OpenAI 官方供应商",
        "healthy": true,
        "createdAt": "2024-01-04T15:30:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

### 测试供应商连接

**接口**: `POST /api/ai/providers/{id}/test`

**功能描述**: 测试指定供应商的API连接是否正常

**请求参数**:
- `id` (路径参数, 必填): 供应商ID

**响应参数**:

```json
{
  "code": "PUB-200-00",
  "msg": "操作成功",
  "data": {
    "success": true,
    "message": "连接成功",
    "latency": 100
  }
}
```

## 数据模型

### ChatRequestDTO

| 字段名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| sessionId | String | 否 | 会话ID，不传则创建新会话 | "1723456789012345678" |
| modelId | Long | 是 | 模型ID | 1 |
| message | String | 是 | 消息内容 | "你好，请问你是谁？" |
| stream | Boolean | 否 | 是否流式返回，默认false | false |

### ChatResponseDTO

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| sessionId | String | 会话ID | "1723456789012345678" |
| messageId | Long | 消息ID | 1 |
| role | String | 角色（user/assistant） | "assistant" |
| content | String | 消息内容 | "你好！我是AI助手。" |
| tokensUsed | Integer | 使用的token数 | 10 |
| createdAt | LocalDateTime | 创建时间 | "2024-01-04T15:30:00" |

### MessageDTO

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| id | Long | 主键ID | 1 |
| sessionId | String | 会话ID | "1723456789012345678" |
| role | String | 角色（user/assistant/system） | "user" |
| content | String | 消息内容 | "你好" |
| tokensUsed | Integer | 使用的token数 | 5 |
| createdAt | LocalDateTime | 创建时间 | "2024-01-04T15:30:00" |

### ModelDTO

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| id | Long | 主键ID | 1 |
| modelCode | String | 模型代码 | "gpt-3.5-turbo" |
| modelName | String | 模型名称 | "GPT-3.5 Turbo" |
| providerId | Long | 供应商ID | 1 |
| providerName | String | 供应商名称 | "OpenAI" |
| enabled | Boolean | 是否启用 | true |
| maxTokens | Integer | 最大token数 | 4096 |
| contextLength | Integer | 上下文长度 | 16384 |
| description | String | 描述 | "OpenAI 经典的 3.5 模型" |
| createdAt | LocalDateTime | 创建时间 | "2024-01-04T15:30:00" |

### ProviderDTO

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| id | Long | 主键ID | 1 |
| providerCode | String | 供应商代码 | "openai" |
| providerName | String | 供应商名称 | "OpenAI" |
| baseUrl | String | API基础URL | "https://api.openai.com/v1" |
| enabled | Boolean | 是否启用 | true |
| priority | Integer | 优先级 | 1 |
| description | String | 描述 | "OpenAI 官方供应商" |
| healthy | Boolean | 健康状态 | true |
| createdAt | LocalDateTime | 创建时间 | "2024-01-04T15:30:00" |

### SessionDTO

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| id | Long | 主键ID | 1 |
| sessionId | String | 会话ID | "1723456789012345678" |
| userId | String | 用户ID | "user123" |
| title | String | 会话标题 | "新会话" |
| modelId | Long | 模型ID | 1 |
| modelName | String | 模型名称 | "GPT-3.5 Turbo" |
| createdAt | LocalDateTime | 创建时间 | "2024-01-04T15:30:00" |
| updatedAt | LocalDateTime | 更新时间 | "2024-01-04T15:30:00" |

### TestResultDTO

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| success | boolean | 是否成功 | true |
| message | String | 消息 | "连接成功" |
| latency | long | 延迟（毫秒） | 150 |

## 通用响应格式

所有接口的响应都遵循以下格式：

```json
{
  "code": "PUB-200-00",
  "msg": "操作成功",
  "data": {}
}
```

**字段说明**:
- `code`: 状态码，PUB-200-00表示成功
- `msg`: 消息描述
- `data`: 数据内容，类型根据接口不同而变化

## 错误码说明

项目使用自定义状态码格式：`XXX-XXX-XX`

### 成功状态码
| 状态码 | 描述 |
|--------|------|
| PUB-200-00 | 操作成功 |

### 程序异常 (PUB-5XX-XX)
| 状态码 | 描述 |
|--------|------|
| PUB-500-00 | 服务器内部错误 |
| PUB-503-00 | 服务不可用 |
| PUB-500-01 | 数据库操作异常 |
| PUB-500-02 | 加解密异常 |
| PUB-505-00 | 网络通信异常 |
| PUB-404-00 | 资源不存在 |

### 业务异常 (BUS-4XX-XX)
| 状态码 | 描述 |
|--------|------|
| BUS-400-00 | 请求参数错误 |
| BUS-401-00 | 未授权访问 |
| BUS-403-00 | 禁止访问 |
| BUS-404-00 | 资源不存在 |
| BUS-405-00 | 请求方法不允许 |
| BUS-405-00 | 业务不允许 |

## 使用示例

### 1. 发送聊天消息

```bash
curl -X POST "http://localhost:8080/api/ai/chat" \
  -H "Authorization: Bearer your-token" \
  -H "Content-Type: application/json" \
  -d '{
    "modelId": 1,
    "message": "你好，请问你是谁？"
  }'
```

### 2. 获取会话历史

```bash
curl -X GET "http://localhost:8080/api/ai/chat/history?sessionId=1723456789012345678" \
  -H "Authorization: Bearer your-token"
```

### 3. 创建新会话

```bash
curl -X POST "http://localhost:8080/api/ai/sessions?userId=user123&modelId=1&title=新会话" \
  -H "Authorization: Bearer your-token"
```

### 4. 获取模型列表

```bash
curl -X GET "http://localhost:8080/api/ai/models?page=1&size=10" \
  -H "Authorization: Bearer your-token"
```

### 5. 测试模型连接

```bash
curl -X POST "http://localhost:8080/api/ai/models/1/test" \
  -H "Authorization: Bearer your-token"
```

## 注意事项

1. 所有接口都需要身份认证，请在请求头中添加 `Authorization: Bearer <token>`
2. 时间字段使用ISO 8601格式：`yyyy-MM-dd'T'HH:mm:ss`
3. 分页参数 `page` 从1开始
4. 删除操作会同时删除关联的数据（如删除会话会删除所有消息）
5. 当前版本为模拟实现，实际AI回复需要集成真实的AI模型API

## 版本信息

- **当前版本**: v1.0.0
- **最后更新**: 2024-01-04
- **作者**: SHOOTING_STAR_C

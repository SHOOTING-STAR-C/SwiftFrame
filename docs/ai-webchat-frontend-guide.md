# AI WebChat 前端对接文档

## API 基础信息

- **Base URL**: `http://localhost:8080/api/ai`
- **Content-Type**: `application/json`
- **响应格式**: 统一使用 `PubResult` 包装

```typescript
interface PubResult<T> {
  code: number;
  message: string;
  data: T;
}
```

## 1. 供应商管理

### 获取供应商列表

```http
GET /providers?page=1&size=10&enabled=true
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "providerCode": "deepseek",
        "providerName": "DeepSeek",
        "baseUrl": "https://api.deepseek.com",
        "enabled": true,
        "priority": 1
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1
  }
}
```

### 创建供应商

```http
POST /providers
Content-Type: application/json

{
  "providerCode": "deepseek",
  "providerName": "DeepSeek",
  "baseUrl": "https://api.deepseek.com",
  "apiKey": "your-api-key",
  "enabled": true,
  "priority": 1,
  "description": "DeepSeek AI服务"
}
```

### 更新供应商

```http
PUT /providers/{id}
Content-Type: application/json

{
  "providerCode": "deepseek",
  "providerName": "DeepSeek",
  "baseUrl": "https://api.deepseek.com",
  "apiKey": "new-api-key",
  "enabled": true
}
```

### 删除供应商

```http
DELETE /providers/{id}
```

## 2. 模型管理

### 获取模型列表

```http
GET /models?page=1&size=10&providerId=1&enabled=true
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "providerId": 1,
        "providerName": "DeepSeek",
        "modelCode": "deepseek-chat",
        "modelName": "DeepSeek Chat",
        "enabled": true,
        "maxTokens": 4096
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1
  }
}
```

### 创建模型

```http
POST /models
Content-Type: application/json

{
  "providerId": 1,
  "modelCode": "deepseek-chat",
  "modelName": "DeepSeek Chat",
  "enabled": true,
  "maxTokens": 4096,
  "description": "DeepSeek对话模型"
}
```

### 测试模型连接

```http
POST /models/{id}/test
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "success": true,
    "message": "连接成功",
    "latency": 234
  }
}
```

### 更新模型

```http
PUT /models/{id}
Content-Type: application/json

{
  "providerId": 1,
  "modelCode": "deepseek-chat",
  "modelName": "DeepSeek Chat",
  "enabled": true,
  "maxTokens": 8192
}
```

### 删除模型

```http
DELETE /models/{id}
```

## 3. 会话管理

### 创建会话

```http
POST /sessions
Content-Type: application/json

{
  "userId": 1,
  "modelId": 1,
  "title": "新对话"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "sessionId": "session-xxx",
    "userId": 1,
    "modelId": 1,
    "title": "新对话",
    "createdAt": "2026-01-03T12:00:00"
  }
}
```

### 获取会话列表

```http
GET /sessions?page=1&size=10&userId=1
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "sessionId": "session-xxx",
        "userId": 1,
        "modelId": 1,
        "title": "新对话",
        "createdAt": "2026-01-03T12:00:00"
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1
  }
}
```

### 获取会话详情

```http
GET /sessions/{sessionId}
```

### 删除会话

```http
DELETE /sessions/{sessionId}
```

## 4. 消息管理

### 获取会话消息

```http
GET /sessions/{sessionId}/messages?page=1&size=50
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "sessionId": "session-xxx",
        "role": "user",
        "content": "你好",
        "tokens": 2,
        "createdAt": "2026-01-03T12:00:00"
      },
      {
        "id": 2,
        "sessionId": "session-xxx",
        "role": "assistant",
        "content": "你好！我是AI助手",
        "tokens": 8,
        "createdAt": "2026-01-03T12:00:01"
      }
    ],
    "total": 2,
    "size": 50,
    "current": 1
  }
}
```

### 删除消息

```http
DELETE /messages/{id}
```

## 5. 对话接口

### 发送消息（非流式）

```http
POST /chat
Content-Type: application/json

{
  "sessionId": "session-xxx",
  "modelId": 1,
  "message": "你好，请介绍一下自己",
  "temperature": 0.7,
  "maxTokens": 2000
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "sessionId": "session-xxx",
    "messageId": 456,
    "content": "你好！我是AI助手...",
    "role": "assistant",
    "tokens": 150,
    "finishReason": "stop"
  }
}
```

### 发送消息（流式）

```http
POST /chat/stream
Content-Type: application/json

{
  "sessionId": "session-xxx",
  "modelId": 1,
  "message": "你好，请介绍一下自己",
  "temperature": 0.7,
  "maxTokens": 2000
}
```

**响应格式**: Server-Sent Events (SSE)

每行格式：
```
data: {"content":"你好","role":"assistant","finishReason":null}
data: {"content":"！我是","role":"assistant","finishReason":null}
data: {"content":"AI助手","role":"assistant","finishReason":"stop"}
data: [DONE]
```

## 前端集成示例

### TypeScript 类型定义

```typescript
// 供应商
interface Provider {
  id: number;
  providerCode: string;
  providerName: string;
  baseUrl: string;
  enabled: boolean;
  priority: number;
  description?: string;
}

// 模型
interface Model {
  id: number;
  providerId: number;
  providerName: string;
  modelCode: string;
  modelName: string;
  enabled: boolean;
  maxTokens?: number;
  description?: string;
}

// 会话
interface Session {
  id: number;
  sessionId: string;
  userId: number;
  modelId: number;
  title: string;
  createdAt: string;
  updatedAt: string;
}

// 消息
interface Message {
  id: number;
  sessionId: string;
  role: 'user' | 'assistant' | 'system';
  content: string;
  tokens?: number;
  createdAt: string;
}

// 对话响应
interface ChatResponse {
  sessionId: string;
  messageId: number;
  content: string;
  role: string;
  tokens: number;
  finishReason: string;
}

// 测试结果
interface TestResult {
  success: boolean;
  message: string;
  latency: number;
}
```

### 使用 Fetch API

```typescript
const BASE_URL = 'http://localhost:8080/api/ai';

// 获取供应商列表
async function getProviders(page = 1, size = 10, enabled?: boolean) {
  const params = new URLSearchParams({
    page: page.toString(),
    size: size.toString()
  });
  if (enabled !== undefined) params.append('enabled', enabled.toString());
  
  const response = await fetch(`${BASE_URL}/providers?${params}`);
  const result = await response.json();
  return result.data;
}

// 获取模型列表
async function getModels(providerId?: number, enabled = true) {
  const params = new URLSearchParams({ enabled: enabled.toString() });
  if (providerId) params.append('providerId', providerId.toString());
  
  const response = await fetch(`${BASE_URL}/models?${params}`);
  const result = await response.json();
  return result.data;
}

// 测试模型
async function testModel(modelId: number): Promise<TestResult> {
  const response = await fetch(`${BASE_URL}/models/${modelId}/test`, {
    method: 'POST'
  });
  const result = await response.json();
  return result.data;
}

// 创建会话
async function createSession(modelId: number, title: string): Promise<Session> {
  const response = await fetch(`${BASE_URL}/sessions`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      userId: 1,
      modelId,
      title
    })
  });
  const result = await response.json();
  return result.data;
}

// 获取会话列表
async function getSessions(userId: number, page = 1, size = 10) {
  const params = new URLSearchParams({
    userId: userId.toString(),
    page: page.toString(),
    size: size.toString()
  });
  
  const response = await fetch(`${BASE_URL}/sessions?${params}`);
  const result = await response.json();
  return result.data;
}

// 获取会话消息
async function getMessages(sessionId: string, page = 1, size = 50) {
  const params = new URLSearchParams({
    page: page.toString(),
    size: size.toString()
  });
  
  const response = await fetch(`${BASE_URL}/sessions/${sessionId}/messages?${params}`);
  const result = await response.json();
  return result.data;
}

// 发送消息（非流式）
async function sendMessage(
  sessionId: string,
  modelId: number,
  message: string,
  temperature = 0.7,
  maxTokens = 2000
): Promise<ChatResponse> {
  const response = await fetch(`${BASE_URL}/chat`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      sessionId,
      modelId,
      message,
      temperature,
      maxTokens
    })
  });
  const result = await response.json();
  return result.data;
}

// 流式对话
async function streamChat(
  sessionId: string,
  modelId: number,
  message: string,
  onChunk: (chunk: any) => void,
  onComplete?: () => void,
  onError?: (error: Error) => void
) {
  try {
    const response = await fetch(`${BASE_URL}/chat/stream`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        sessionId,
        modelId,
        message
      })
    });

    const reader = response.body?.getReader();
    const decoder = new TextDecoder();

    if (!reader) {
      throw new Error('无法获取响应流');
    }

    while (true) {
      const { done, value } = await reader.read();
      if (done) {
        onComplete?.();
        break;
      }

      const chunk = decoder.decode(value);
      const lines = chunk.split('\n');

      for (const line of lines) {
        if (line.startsWith('data: ')) {
          const data = line.substring(6);
          if (data === '[DONE]') continue;

          try {
            const parsed = JSON.parse(data);
            onChunk(parsed);
          } catch (e) {
            console.error('解析失败:', e);
          }
        }
      }
    }
  } catch (error) {
    onError?.(error as Error);
  }
}
```

### 使用 Axios

```typescript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api/ai',
  headers: {
    'Content-Type': 'application/json'
  }
});

// 获取供应商列表
export async function getProviders(page = 1, size = 10, enabled?: boolean) {
  const response = await api.get('/providers', {
    params: { page, size, enabled }
  });
  return response.data.data;
}

// 获取模型列表
export async function getModels(providerId?: number, enabled = true) {
  const response = await api.get('/models', {
    params: { providerId, enabled }
  });
  return response.data.data;
}

// 测试模型
export async function testModel(modelId: number): Promise<TestResult> {
  const response = await api.post(`/models/${modelId}/test`);
  return response.data.data;
}

// 创建会话
export async function createSession(modelId: number, title: string): Promise<Session> {
  const response = await api.post('/sessions', {
    userId: 1,
    modelId,
    title
  });
  return response.data.data;
}

// 获取会话列表
export async function getSessions(userId: number, page = 1, size = 10) {
  const response = await api.get('/sessions', {
    params: { userId, page, size }
  });
  return response.data.data;
}

// 获取会话消息
export async function getMessages(sessionId: string, page = 1, size = 50) {
  const response = await api.get(`/sessions/${sessionId}/messages`, {
    params: { page, size }
  });
  return response.data.data;
}

// 发送消息
export async function sendMessage(
  sessionId: string,
  modelId: number,
  message: string,
  temperature = 0.7,
  maxTokens = 2000
): Promise<ChatResponse> {
  const response = await api.post('/chat', {
    sessionId,
    modelId,
    message,
    temperature,
    maxTokens
  });
  return response.data.data;
}
```

### React 组件示例

```typescript
import React, { useState, useEffect, useRef } from 'react';

interface ChatMessage {
  id: number;
  role: 'user' | 'assistant';
  content: string;
}

export default function ChatComponent() {
  const [sessions, setSessions] = useState<Session[]>([]);
  const [currentSession, setCurrentSession] = useState<Session | null>(null);
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  // 加载会话列表
  useEffect(() => {
    loadSessions();
  }, []);

  // 自动滚动到底部
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const loadSessions = async () => {
    try {
      const data = await getSessions(1);
      setSessions(data.records);
    } catch (error) {
      console.error('加载会话失败:', error);
    }
  };

  const loadMessages = async (sessionId: string) => {
    try {
      const data = await getMessages(sessionId);
      setMessages(data.records);
    } catch (error) {
      console.error('加载消息失败:', error);
    }
  };

  const handleCreateSession = async (modelId: number) => {
    try {
      const session = await createSession(modelId, '新对话');
      setSessions([session, ...sessions]);
      setCurrentSession(session);
      setMessages([]);
    } catch (error) {
      console.error('创建会话失败:', error);
    }
  };

  const handleSendMessage = async () => {
    if (!input.trim() || !currentSession || loading) return;

    const userMessage: ChatMessage = {
      id: Date.now(),
      role: 'user',
      content: input
    };

    setMessages([...messages, userMessage]);
    setInput('');
    setLoading(true);

    try {
      // 流式对话
      let assistantContent = '';
      await streamChat(
        currentSession.sessionId,
        currentSession.modelId,
        userMessage.content,
        (chunk) => {
          assistantContent += chunk.content;
          setMessages(prev => {
            const newMessages = [...prev];
            const lastMessage = newMessages[newMessages.length - 1];
            if (lastMessage.role === 'assistant') {
              lastMessage.content = assistantContent;
            } else {
              newMessages.push({
                id: Date.now(),
                role: 'assistant',
                content: assistantContent
              });
            }
            return newMessages;
          });
        },
        () => {
          setLoading(false);
        },
        (error) => {
          console.error('对话失败:', error);
          setLoading(false);
        }
      );
    } catch (error) {
      console.error('发送消息失败:', error);
      setLoading(false);
    }
  };

  return (
    <div className="chat-container">
      {/* 会话列表 */}
      <div className="sessions-panel">
        <button onClick={() => handleCreateSession(1)}>
          新建对话
        </button>
        {sessions.map(session => (
          <div
            key={session.sessionId}
            onClick={() => {
              setCurrentSession(session);
              loadMessages(session.sessionId);
            }}
          >
            {session.title}
          </div>
        ))}
      </div>

      {/* 消息区域 */}
      <div className="messages-panel">
        {messages.map(message => (
          <div key={message.id} className={`message ${message.role}`}>
            {message.content}
          </div>
        ))}
        <div ref={messagesEndRef} />
      </div>

      {/* 输入区域 */}
      <div className="input-panel">
        <textarea
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
              e.preventDefault();
              handleSendMessage();
            }
          }}
          disabled={loading}
        />
        <button onClick={handleSendMessage} disabled={loading}>
          {loading ? '发送中...' : '发送'}
        </button>
      </div>
    </div>
  );
}
```

## 注意事项

1. **错误处理**: 所有API调用都应该进行错误处理
2. **流式响应**: 流式对话使用SSE协议，需要正确处理流式数据
3. **会话管理**: 建议定期刷新会话列表
4. **Token限制**: 注意模型的maxTokens限制
5. **并发控制**: 避免同一会话并发发送消息
6. **加载状态**: 在发送消息时显示加载状态
7. **自动滚动**: 新消息到达时自动滚动到底部

## 常见问题

**Q: 如何测试模型是否可用？**
A: 使用 `POST /models/{id}/test` 接口测试模型连接。

**Q: 流式对话如何实现？**
A: 使用 `POST /chat/stream` 接口，前端需要处理SSE流式响应。

**Q: 如何获取对话历史？**
A: 使用 `GET /sessions/{sessionId}/messages` 接口获取会话的所有消息。

**Q: 支持哪些参数？**
A: 支持temperature、maxTokens、topP、frequencyPenalty、presencePenalty等参数。

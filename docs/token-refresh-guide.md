# Token刷新机制使用指南

## 概述

本系统采用**Token轮换机制**（Token Rotation）来实现短期token的自动刷新，提高系统安全性。

## Token类型

### 1. Access Token（访问令牌）
- **用途**：用于访问受保护的API接口
- **有效期**：默认1小时（可在配置文件中修改）
- **存储位置**：Redis + 前端存储（localStorage或sessionStorage）

### 2. Refresh Token（刷新令牌）
- **用途**：用于获取新的Access Token和Refresh Token
- **有效期**：默认7天（可在配置文件中修改）
- **存储位置**：Redis + 前端存储（推荐使用httpOnly cookie）

## Token刷新流程

```
┌─────────┐                    ┌─────────┐
│ 前端应用 │                    │ 后端API │
└────┬────┘                    └────┬────┘
     │                              │
     │  1. POST /auth/login         │
     │  {username, password}        │
     │─────────────────────────────>│
     │                              │
     │  2. 返回token对              │
     │  {accessToken, refreshToken} │
     │<─────────────────────────────│
     │                              │
     │  3. 使用accessToken访问API   │
     │  Authorization: Bearer xxx   │
     │─────────────────────────────>│
     │                              │
     │  4. accessToken过期          │
     │  返回401 Unauthorized        │
     │<─────────────────────────────│
     │                              │
     │  5. POST /auth/refresh       │
     │  {refreshToken}              │
     │─────────────────────────────>│
     │                              │
     │  6. 验证refreshToken         │
     │  生成新的token对             │
     │  使旧refreshToken失效        │
     │                              │
     │  7. 返回新的token对          │
     │  {accessToken, refreshToken} │
     │<─────────────────────────────│
     │                              │
     │  8. 更新本地存储的token       │
     │  重试原请求                  │
     │─────────────────────────────>│
```

## API接口

### 1. 登录接口
```http
POST /auth/login
Content-Type: application/json

{
  "username": "encrypted_username",
  "password": "encrypted_password"
}
```

**响应**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

### 2. 刷新Token接口
```http
POST /auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**响应**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "accessToken": "new_access_token...",
    "refreshToken": "new_refresh_token..."
  }
}
```

**错误响应**：
```json
{
  "code": 401,
  "message": "Token无效或已过期",
  "data": null
}
```

### 3. 登出接口
```http
POST /auth/logout
Authorization: Bearer {accessToken}
```

## 前端实现建议

### 1. Axios拦截器实现自动刷新

```javascript
import axios from 'axios';

// 创建axios实例
const api = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000
});

// 请求拦截器 - 添加token
api.interceptors.request.use(
  config => {
    const accessToken = localStorage.getItem('accessToken');
    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`;
    }
    return config;
  },
  error => Promise.reject(error)
);

// 响应拦截器 - 处理token刷新
let isRefreshing = false;
let refreshSubscribers = [];

const subscribeTokenRefresh = (callback) => {
  refreshSubscribers.push(callback);
};

const onRefreshed = (token) => {
  refreshSubscribers.forEach(callback => callback(token));
  refreshSubscribers = [];
};

api.interceptors.response.use(
  response => response,
  async error => {
    const originalRequest = error.config;
    
    // 如果是401错误且未重试过
    if (error.response?.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        // 如果正在刷新，将请求加入队列
        return new Promise((resolve) => {
          subscribeTokenRefresh(token => {
            originalRequest.headers.Authorization = `Bearer ${token}`;
            resolve(api(originalRequest));
          });
        });
      }
      
      originalRequest._retry = true;
      isRefreshing = true;
      
      try {
        const refreshToken = localStorage.getItem('refreshToken');
        const response = await axios.post('/auth/refresh', {
          refreshToken: refreshToken
        });
        
        const { accessToken, refreshToken: newRefreshToken } = response.data.data;
        
        // 更新本地存储
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', newRefreshToken);
        
        // 通知所有等待的请求
        onRefreshed(accessToken);
        
        // 重试原请求
        originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        return api(originalRequest);
      } catch (refreshError) {
        // 刷新失败，清除token并跳转登录
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        window.location.href = '/login';
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }
    
    return Promise.reject(error);
  }
);

export default api;
```

### 2. 安全建议

1. **Refresh Token存储**：
   - 推荐使用httpOnly cookie存储refreshToken，防止XSS攻击
   - Access Token可以存储在localStorage或sessionStorage

2. **Token过期时间配置**：
   ```yaml
   # application.yml
   spring:
     security:
       jwt:
         expiration: 3600000        # accessToken: 1小时
         refreshExpiration: 604800000  # refreshToken: 7天
   ```

3. **并发刷新处理**：
   - 使用标志位防止多个请求同时触发刷新
   - 使用队列机制处理并发请求

4. **错误处理**：
   - RefreshToken过期或无效时，清除本地token
   - 跳转到登录页面重新认证

## 安全特性

### Token轮换机制的优势

1. **防止RefreshToken重放攻击**：
   - 每次刷新后，旧的refreshToken立即失效
   - 即使refreshToken泄露，也只能使用一次

2. **缩短风险窗口**：
   - RefreshToken定期轮换，减少长期有效的风险

3. **可追溯性**：
   - 可以追踪token的使用情况
   - 便于发现异常行为

### 注意事项

1. **不要在URL中传递token**：
   - Token应该放在请求头中
   - 避免被日志记录

2. **使用HTTPS**：
   - 确保所有API请求都通过HTTPS
   - 防止中间人攻击

3. **定期更新密钥**：
   - 定期更换JWT签名密钥
   - 提高系统安全性

## 常见问题

### Q1: RefreshToken过期了怎么办？
A: RefreshToken过期后，用户需要重新登录获取新的token对。

### Q2: 多个标签页同时刷新token会怎样？
A: 系统使用队列机制，第一个请求触发刷新，其他请求等待刷新完成后重试。

### Q3: 如何强制用户重新登录？
A: 可以在Redis中删除用户的refreshToken，下次刷新时会失败。

### Q4: Token刷新失败后如何处理？
A: 清除本地存储的token，跳转到登录页面，提示用户重新登录。

## 测试建议

1. **正常刷新流程**：
   - 登录获取token
   - 等待accessToken过期
   - 使用refreshToken刷新
   - 验证新token可用

2. **异常情况测试**：
   - 使用过期的refreshToken
   - 使用无效的refreshToken
   - 用户账户被锁定时刷新
   - 并发刷新测试

3. **安全测试**：
   - 尝试使用已使用的refreshToken
   - 验证旧refreshToken是否失效

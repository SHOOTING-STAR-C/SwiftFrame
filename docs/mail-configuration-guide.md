# 邮件服务配置指南

## 概述

SwiftFrame 使用 Spring Boot 的邮件功能发送验证码等邮件。配置分为两部分：
1. **主配置文件** (`application.yml`)：定义邮件配置的结构和默认值
2. **环境配置文件** (`application-dev.yml`, `application-test.yml`, `application-prod.yml`)：设置具体的环境参数

## 配置文件说明

### 1. 主配置文件 (application.yml)

主配置文件已经包含了邮件配置结构，使用环境变量作为默认值：

```yaml
spring:
  mail:
    host: ${MAIL_HOST:smtp.qq.com}
    port: ${MAIL_PORT:587}
    username: ${MAIL_USERNAME:}
    password: ${MAIL_PASSWORD:}
    protocol: smtp
    default-encoding: UTF-8
    properties:
      mail:
        mime:
          content:
            type:
              unknown: application/octet-stream
        smtp:
          auth: true
          starttls:
            enable: true
            required: true
        contenttypehandler: false
        mime.types: ""
```

### 2. 环境配置文件

您需要在对应的环境配置文件中添加具体的邮件配置。以下以 QQ 邮箱为例：

#### 开发环境 (application-dev.yml)

```yaml
spring:
  mail:
    host: smtp.qq.com
    port: 587
    username: your-email@qq.com
    password: your-authorization-code
```

#### 测试环境 (application-test.yml)

```yaml
spring:
  mail:
    host: smtp.qq.com
    port: 587
    username: test-email@qq.com
    password: test-authorization-code
```

#### 生产环境 (application-prod.yml)

```yaml
spring:
  mail:
    host: smtp.qq.com
    port: 587
    username: production-email@qq.com
    password: production-authorization-code
```

## QQ 邮箱配置步骤

### 1. 开启 SMTP 服务

1. 登录 QQ 邮箱网页版
2. 点击设置 → 账户
3. 找到"POP3/IMAP/SMTP/Exchange/CardDAV/CalDAV服务"
4. 开启"IMAP/SMTP服务"或"POP3/SMTP服务"
5. 按照提示发送短信验证
6. 获得**授权码**（不是邮箱密码）

### 2. 获取授权码

授权码是 16 位字符，格式类似：`abcdefghijklmnop`

**重要**：
- 不要使用 QQ 邮箱登录密码
- 必须使用授权码作为 password
- 授权码可以重新生成

### 3. 配置参数

| 参数 | 值 | 说明 |
|------|-----|------|
| host | smtp.qq.com | QQ 邮箱 SMTP 服务器 |
| port | 587 | SMTP 端口（TLS） |
| username | your-email@qq.com | 您的 QQ 邮箱地址 |
| password | 授权码 | 从 QQ 邮箱获取的 16 位授权码 |

## 其他邮箱服务商配置

### 163 邮箱

```yaml
spring:
  mail:
    host: smtp.163.com
    port: 465
    username: your-email@163.com
    password: 授权码
```

### Gmail

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: 应用专用密码
```

### 企业邮箱

```yaml
spring:
  mail:
    host: smtp.exmail.qq.com
    port: 465
    username: your-email@company.com
    password: 密码或授权码
```

## 使用环境变量（可选）

如果您希望通过环境变量配置，可以设置以下环境变量：

**Windows CMD:**
```cmd
set MAIL_HOST=smtp.qq.com
set MAIL_PORT=587
set MAIL_USERNAME=your-email@qq.com
set MAIL_PASSWORD=your-authorization-code
```

**Windows PowerShell:**
```powershell
$env:MAIL_HOST="smtp.qq.com"
$env:MAIL_PORT="587"
$env:MAIL_USERNAME="your-email@qq.com"
$env:MAIL_PASSWORD="your-authorization-code"
```

**Linux/Mac:**
```bash
export MAIL_HOST=smtp.qq.com
export MAIL_PORT=587
export MAIL_USERNAME=your-email@qq.com
export MAIL_PASSWORD=your-authorization-code
```

## 配置验证

配置完成后，可以通过以下方式验证：

1. 重启应用程序
2. 查看启动日志，确认没有邮件配置相关错误
3. 调用邮件发送接口测试

## 常见问题

### 1. 授权码错误

**错误信息**：`AuthenticationFailedException`

**解决**：检查是否使用了正确的授权码，而不是邮箱登录密码。

### 2. 端口错误

**错误信息**：`ConnectException`

**解决**：确认端口配置正确：
- QQ 邮箱：587（TLS）或 465（SSL）
- 163 邮箱：465（SSL）

### 3. SSL 证书问题

如果遇到 SSL 证书验证问题，可以添加以下配置：

```yaml
spring:
  mail:
    properties:
      mail:
        smtp:
          ssl:
            trust: smtp.qq.com
```

## 安全建议

1. **不要将密码提交到版本控制系统**
   - 使用环境变量
   - 或使用加密配置（如项目中的 ENC()）

2. **使用专用邮箱**
   - 不要使用个人主邮箱
   - 为应用创建专用邮箱

3. **定期更换授权码**
   - 定期更新授权码提高安全性

4. **限制邮箱功能**
   - 限制该邮箱只能用于发送邮件
   - 避免泄露重要信息

## 相关文件

- 主配置：`swift-start/src/main/resources/application.yml`
- 开发环境：`swift-start/src/main/resources/application-dev.yml`
- 测试环境：`swift-start/src/main/resources/application-test.yml`
- 生产环境：`swift-start/src/main/resources/application-prod.yml`
- 邮件服务：`swift-mail/src/main/java/com/star/swiftMail/service/impl/MailServiceImpl.java`

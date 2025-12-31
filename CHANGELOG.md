# 更新日志 (Changelog)

本文档记录SwiftFrame项目的所有重要更改。

---

## [2025-12-30] - Bug修复版本

### Bug修复

1. **JWT刷新令牌过期时间错误修复**
   - **位置**：`swift-security/src/main/java/com/star/swiftSecurity/utils/JwtUtil.java`
   - **问题**：`generateRefreshToken()`方法中错误地使用了访问令牌的过期时间(`jwtProperties.getExpiration()`)来存储刷新令牌
   - **修复**：改为使用正确的刷新令牌过期时间(`jwtProperties.getRefreshExpiration()`)
   - **影响**：修复后刷新令牌将按照正确的过期时间存储到Redis中

2. **AI流式响应资源泄漏修复**
   - **位置**：`swift-ai/src/main/java/com/star/swiftAi/client/OpenAiCompatibleClient.java`
   - **问题**：`streamChat()`方法中的Stream资源未正确关闭，可能导致资源泄漏
   - **修复**：使用try-with-resources语句确保Stream资源被正确关闭
   - **影响**：修复后流式响应的HTTP连接将被正确释放，避免资源泄漏

3. **全局异常处理空字符串检查优化**
   - **位置**：`swift-common/src/main/java/com/star/swiftCommon/handler/GlobalExceptionHandler.java`
   - **问题**：`handleMethodArgumentNotValidException()`方法中仅检查`isEmpty()`，未检查字符串最后一个字符是否为'；'
   - **修复**：改为检查`length() > 0`和最后一个字符是否为'；'，避免误删前缀字符
   - **影响**：修复后异常消息格式更加健壮，避免潜在的字符串操作错误

---

## 版本说明

### 版本号格式
- 主版本号：重大架构变更或API不兼容
- 次版本号：新功能添加，向后兼容
- 修订号：Bug修复和优化

### 更新类型
- **新增**：新功能
- **修复**：Bug修复
- **优化**：性能优化或代码改进
- **变更**：功能变更
- **移除**：移除功能
- **安全**：安全相关修复

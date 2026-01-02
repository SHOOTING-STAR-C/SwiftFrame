# 系统配置管理使用指南

## 概述

系统配置管理模块提供了一个安全的方式来存储和管理系统敏感配置信息，如AI配置、邮件配置等。所有配置值支持加密存储，并在项目启动时自动加载到Redis缓存中。

## 核心特性

1. **加密存储支持**：通过标记控制是否加密
   - `DEC(value)`: 存储时加密value
   - `ENC(value)`: 读取时解密value
   - 无标记: 明文存储和读取

2. **自动缓存**：项目启动时自动将启用的配置加载到Redis

3. **配置分类**：支持按类型管理配置（SYSTEM/AI/MAIL/DATABASE/THIRD_PARTY）

4. **启用/禁用**：可以控制配置是否生效

## 数据库表结构

### sys_config 表

| 字段名 | 类型 | 说明 |
|--------|------|------|
| config_id | BIGSERIAL | 配置ID，主键，自增 |
| config_key | VARCHAR(100) | 配置键，唯一标识 |
| config_value | TEXT | 配置值（加密后） |
| config_type | VARCHAR(50) | 配置类型 |
| description | VARCHAR(255) | 配置描述 |
| is_enabled | BOOLEAN | 是否启用 |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |
| created_by | VARCHAR(50) | 创建人 |
| updated_by | VARCHAR(50) | 更新人 |

## 使用示例

### 1. 保存配置（加密）

```java
@Autowired
private SysConfigService sysConfigService;

// 保存加密的配置
SysConfig config = new SysConfig();
config.setConfigKey("ai.openai.api_key");
config.setConfigValue("DEC(sk-xxxxxxxxxxxxxxxxxxxxx"); // 使用DEC()标记，会自动加密
config.setConfigType("AI");
config.setDescription("OpenAI API密钥");
config.setIsEnabled(true);
config.setCreatedBy("admin");

SysConfig saved = sysConfigService.saveConfig(config);
```

### 2. 保存配置（明文）

```java
// 保存明文配置
SysConfig config = new SysConfig();
config.setConfigKey("ai.openai.model");
config.setConfigValue("gpt-4"); // 无标记，明文存储
config.setConfigType("AI");
config.setDescription("OpenAI模型名称");
config.setIsEnabled(true);
config.setCreatedBy("admin");

SysConfig saved = sysConfigService.saveConfig(config);
```

### 3. 读取配置

```java
// 读取配置值（自动解密）
String apiKey = sysConfigService.getConfigValue("ai.openai.api_key");

// 读取完整配置对象
SysConfig config = sysConfigService.getConfig("ai.openai.api_key");
```

### 4. 按类型获取配置

```java
// 获取所有AI类型的配置
List<SysConfig> aiConfigs = sysConfigService.getConfigsByType("AI");

// 获取配置键值对Map
Map<String, String> aiConfigMap = sysConfigService.getConfigMapByType("AI");
```

### 5. 更新配置

```java
// 更新配置
SysConfig config = sysConfigService.getConfig("ai.openai.api_key");
config.setConfigValue("DEC(new-api-key-xxxxxxxxx"); // 使用DEC()标记，会自动加密
config.setUpdatedBy("admin");
sysConfigService.updateConfig(config);

// 或者直接更新值
sysConfigService.updateConfigValue("ai.openai.api_key", "DEC(new-api-key-xxxxxxxxx", "admin");
```

### 6. 启用/禁用配置

```java
// 禁用配置
sysConfigService.toggleConfig(configId, false, "admin");

// 启用配置
sysConfigService.toggleConfig(configId, true, "admin");
```

### 7. 删除配置

```java
// 根据ID删除
sysConfigService.deleteConfig(configId);

// 根据配置键删除
sysConfigService.deleteConfigByKey("ai.openai.api_key");
```

## API接口

### 基础查询接口

- `GET /api/config/value/{configKey}` - 获取配置值
- `GET /api/config/{configKey}` - 获取配置对象
- `GET /api/config/id/{configId}` - 根据ID获取配置
- `GET /api/config/type/{configType}` - 按类型获取配置列表
- `GET /api/config/enabled` - 获取所有启用的配置
- `GET /api/config/all` - 获取所有配置
- `GET /api/config/exists/{configKey}` - 检查配置键是否存在
- `GET /api/config/map/{configType}` - 按类型获取配置Map
- `GET /api/config/map/enabled` - 获取所有启用的配置Map

### 配置管理接口

- `POST /api/config` - 保存配置
- `PUT /api/config` - 更新配置
- `PUT /api/config/value/{configKey}` - 更新配置值
- `DELETE /api/config/{configId}` - 删除配置
- `DELETE /api/config/key/{configKey}` - 根据键删除配置
- `PUT /api/config/toggle/{configId}` - 启用/禁用配置

### 缓存管理接口

- `POST /api/config/refresh` - 刷新配置缓存

## Redis缓存

### 缓存键格式

```
{应用名}:config:{配置键}
```

例如：`swiftframe:config:ai.openai.api_key`

### 缓存过期时间

默认24小时（86400秒）

### 缓存操作

```java
@Autowired
private ConfigCacheService configCacheService;

// 缓存配置
configCacheService.cacheConfig("ai.openai.api_key", "sk-xxxxxxxx");

// 获取缓存配置
String value = configCacheService.getCachedConfig("ai.openai.api_key");

// 删除缓存配置
configCacheService.removeCachedConfig("ai.openai.api_key");

// 清空所有配置缓存
configCacheService.clearAllConfigs();

// 批量缓存配置
Map<String, String> configMap = new HashMap<>();
configMap.put("key1", "value1");
configMap.put("key2", "value2");
configCacheService.batchCacheConfigs(configMap);
```

## 配置类型说明

| 类型 | 说明 | 示例 |
|------|------|------|
| SYSTEM | 系统配置 | 系统名称、版本号等 |
| AI | AI相关配置 | OpenAI API Key、模型配置等 |
| MAIL | 邮件配置 | SMTP密码、发件人信息等 |
| DATABASE | 数据库配置 | 数据库连接信息等 |
| THIRD_PARTY | 第三方服务配置 | 第三方API密钥等 |

## 启动流程

1. 应用启动
2. `ConfigInitializer` 执行（Order=2）
3. 从数据库加载所有启用的配置
4. 自动解密配置值
5. 批量缓存到Redis
6. 记录加载日志和统计信息

## 注意事项

1. **加密标记**：
   - 存储时使用 `DEC(value)` 标记需要加密的值
   - 读取时使用 `ENC(value)` 标记需要解密的值
   - 无标记的值按明文处理

2. **配置键唯一性**：
   - 配置键必须唯一，重复会抛出异常

3. **缓存同步**：
   - 保存/更新配置时会自动更新Redis缓存
   - 删除配置时会自动删除Redis缓存
   - 禁用配置时会从Redis缓存中移除
   - 启用配置时会添加到Redis缓存

4. **安全性**：
   - 敏感信息（如API密钥、密码）必须使用 `DEC()` 标记加密存储
   - 配置值在数据库中以加密形式存储
   - 配置值在Redis中以解密形式存储（注意Redis安全）

5. **性能优化**：
   - 优先从Redis缓存读取配置
   - 使用批量操作减少数据库访问
   - 合理使用索引提升查询性能

## 常见问题

### Q: 如何判断配置是否需要加密？

A: 任何敏感信息都应该加密，包括：
- API密钥
- 密码
- 访问令牌
- 私钥
- 其他敏感凭证

### Q: 配置修改后如何生效？

A: 
- 通过API修改会自动更新Redis缓存
- 直接修改数据库需要调用刷新接口：`POST /api/config/refresh`

### Q: 如何查看当前缓存的配置？

A: 可以通过Redis客户端查看：
```
redis-cli
KEYS swiftframe:config:*
GET swiftframe:config:ai.openai.api_key
```

### Q: 配置缓存过期后如何处理？

A: 
- 缓存过期后会自动从数据库重新加载
- 也可以手动调用刷新接口：`POST /api/config/refresh`

## 最佳实践

1. **配置命名规范**：
   - 使用点号分隔：`模块.子模块.配置项`
   - 例如：`ai.openai.api_key`、`mail.smtp.password`

2. **配置分类**：
   - 合理使用配置类型进行分类管理
   - 便于批量查询和管理

3. **文档记录**：
   - 为每个配置添加清晰的描述
   - 记录配置的用途和格式要求

4. **定期审查**：
   - 定期检查配置的有效性
   - 及时清理不再使用的配置

5. **备份策略**：
   - 定期备份配置数据
   - 重要配置变更前先备份

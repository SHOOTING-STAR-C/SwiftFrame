# Swift AI 模块

基于模型供应商系统设计文档重构的AI模块，采用Java实现。

## 架构设计

### 核心组件

1. **Provider接口体系**
   - `Provider`: 基础提供商接口，定义LLM对话能力
   - `EmbeddingProvider`: 嵌入提供商接口，定义文本嵌入能力
   - `RerankProvider`: 重排序提供商接口，定义文档重排序能力
   - `AbstractProvider`: 抽象基类，提供通用实现

2. **注册与发现机制**
   - `ProviderRegistry`: 提供商注册表，管理所有已注册的提供商
   - `ProviderAdapter`: 注解，用于自动注册提供商
   - `ProviderAutoConfiguration`: 自动配置类，扫描并注册提供商

3. **工厂模式**
   - `ProviderFactory`: 提供商工厂，负责创建Provider实例

4. **数据模型**
   - `ProviderRequest`: 提供商请求模型
   - `LLMResponse`: LLM响应模型
   - `Conversation`: 对话上下文模型
   - `MessageChain`: 消息链模型
   - `TokenUsage`: Token使用统计
   - `ToolSet`: 工具集模型

5. **客户端层**
   - `ProviderClient`: 基于Provider的AI客户端，提供统一的调用接口

## 数据库设计

### ai_provider 表

```sql
CREATE TABLE IF NOT EXISTS ai_provider (
    id BIGSERIAL PRIMARY KEY,
    provider_type VARCHAR(50) NOT NULL,
    provider_name VARCHAR(100) NOT NULL,
    provider_config JSONB NOT NULL,
    provider_settings JSONB NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    priority INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**字段说明：**
- `provider_type`: 提供商类型（如：openai、anthropic、ollama等）
- `provider_name`: 提供商名称
- `provider_config`: 提供商配置（JSON格式，包含API密钥、基础URL等）
- `provider_settings`: 提供商设置（JSON格式，包含模型名称、温度等参数）
- `enabled`: 是否启用
- `priority`: 优先级

## 使用示例

### 1. 创建自定义提供商

```java
@Slf4j
@ProviderAdapter(
    typeName = "my_provider",
    desc = "我的自定义提供商",
    providerType = ProviderType.LLM,
    displayName = "My Provider"
)
public class MyProvider extends AbstractProvider {

    public MyProvider(Map<String, Object> providerConfig, Map<String, Object> providerSettings) {
        super(providerConfig, providerSettings);
    }

    @Override
    public LLMResponse chat(ProviderRequest request) {
        // 实现对话逻辑
        LLMResponse response = new LLMResponse();
        response.setContent("响应内容");
        return response;
    }

    @Override
    public void streamChat(ProviderRequest request, Consumer<LLMResponse> consumer) {
        // 实现流式对话逻辑
    }

    @Override
    public void test() {
        // 实现连接测试逻辑
    }

    public static Map<String, Object> getDefaultConfigTemplate() {
        return Map.of(
            "api_key", "",
            "base_url", "https://api.example.com/v1"
        );
    }
}
```

### 2. 使用ProviderClient调用AI服务

```java
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ProviderClient providerClient;

    public String chat(Long providerId, String userMessage) {
        // 构建对话上下文
        Conversation conversation = new Conversation();
        MessageChain message = new MessageChain();
        message.setRole("user");
        message.setContent(userMessage);
        conversation.setMessages(List.of(message));

        // 调用AI服务
        LLMResponse response = providerClient.chat(providerId, conversation);
        return response.getContent();
    }

    public void streamChat(Long providerId, String userMessage, Consumer<String> callback) {
        // 构建对话上下文
        Conversation conversation = new Conversation();
        MessageChain message = new MessageChain();
        message.setRole("user");
        message.setContent(userMessage);
        conversation.setMessages(List.of(message));

        // 流式调用AI服务
        providerClient.streamChat(providerId, conversation, response -> {
            if (!response.isFinished()) {
                callback.accept(response.getDelta());
            }
        });
    }
}
```

### 3. 通过REST API管理提供商

#### 获取所有已注册的提供商类型

```bash
GET /ai/providers/types
```

响应：
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "type": "openai",
      "desc": "OpenAI提供商，支持GPT系列模型",
      "providerType": "LLM",
      "displayName": "OpenAI",
      "defaultConfigTmpl": {
        "api_key": "",
        "base_url": "https://api.openai.com/v1",
        "timeout": 60,
        "max_retries": 3
      }
    }
  ]
}
```

#### 创建提供商

```bash
POST /ai/providers
Content-Type: application/json

{
  "providerType": "LLM",
  "providerName": "我的OpenAI",
  "providerConfig": "{\"api_key\":\"sk-xxx\",\"base_url\":\"https://api.openai.com/v1\"}",
  "providerSettings": "{\"model\":\"gpt-4\",\"temperature\":0.7,\"max_tokens\":2000}",
  "enabled": true,
  "priority": 1
}
```

#### 测试提供商连接

```bash
POST /ai/providers/{id}/test
```

响应：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "success": true,
    "message": "连接成功",
    "latency": 123
  }
}
```

## 扩展性

### 支持新的提供商类型

1. 创建实现类，继承`AbstractProvider`或实现`Provider`接口
2. 添加`@ProviderAdapter`注解
3. 实现`getDefaultConfigTemplate`静态方法（可选）
4. 系统会自动扫描并注册

### 支持新的能力

1. 创建新的接口（如`ImageProvider`）
2. 创建对应的抽象基类
3. 在`ProviderFactory`中添加创建方法
4. 实现具体的提供商类

## 注意事项

1. **安全性**: API密钥等敏感信息应加密存储在`provider_config`中
2. **配置验证**: 在创建Provider实例时应验证配置的完整性
3. **错误处理**: 所有API调用都应有完善的错误处理和重试机制
4. **性能优化**: 考虑使用连接池、缓存等技术优化性能
5. **日志记录**: 记录关键操作和错误信息，便于排查问题

## 待实现功能

- [ ] 完善OpenAIProvider的实际API调用
- [ ] 实现更多提供商（Anthropic、Ollama、Azure等）
- [ ] 实现EmbeddingProvider的具体实现
- [ ] 实现RerankProvider的具体实现
- [ ] 添加配置验证机制
- [ ] 添加API密钥加密/解密功能
- [ ] 实现请求重试机制
- [ ] 添加性能监控和统计
- [ ] 完善单元测试和集成测试

## 技术栈

- Java 17+
- Spring Boot 3.x
- MyBatis Plus
- PostgreSQL
- Jackson (JSON处理)
- Lombok

## 作者

SHOOTING_STAR_C

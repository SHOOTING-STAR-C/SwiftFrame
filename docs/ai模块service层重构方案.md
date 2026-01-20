# AI模块Service层重构方案

## 当前问题分析

### 有抽象的服务（接口 + 实现类）
1. `AiSystemPromptService` (接口) + `AiSystemPromptServiceImpl` (实现类)

### 没有抽象的服务（直接@Service类）
1. `AiChatService` - 复杂业务逻辑服务
2. `AiChatSessionService` - 数据库CRUD服务
3. `AiModelService` - 数据库CRUD服务
4. `AiChatMessageService` - 数据库CRUD服务
5. `AiProviderService` - 数据库CRUD服务

## 重构规范

### 分类标准

#### 1. 数据库CRUD服务 - 需要接口 + 实现类
特征：
- 直接操作数据库（继承ServiceImpl）
- 主要是增删改查操作
- 需要接口定义契约

需要重构的服务：
- `AiChatSessionService`
- `AiModelService`
- `AiChatMessageService`
- `AiProviderService`

#### 2. 复杂业务逻辑服务 - 保持当前结构
特征：
- 不直接继承ServiceImpl
- 包含复杂业务逻辑编排
- 依赖多个其他服务
- 不需要接口（使用@Service即可）

保持不变的服务：
- `AiChatService`

### 重构步骤

1. 为CRUD服务创建接口（继承IService）
2. 将现有类重命名为Impl
3. 创建新的接口定义
4. 保持业务逻辑不变

### 命名规范

```
接口：XxxService extends IService<Entity>
实现：XxxServiceImpl extends ServiceImpl<Mapper, Entity> implements XxxService
```

## 重构后的目录结构

```
swift-ai/src/main/java/com/star/swiftAi/service/
├── AiChatService.java (保持不变，复杂业务逻辑)
├── AiChatSessionService.java (接口)
├── AiChatMessageService.java (接口)
├── AiModelService.java (接口)
├── AiProviderService.java (接口)
├── AiSystemPromptService.java (已有接口)
└── impl/
    ├── AiChatSessionServiceImpl.java
    ├── AiChatMessageServiceImpl.java
    ├── AiModelServiceImpl.java
    ├── AiProviderServiceImpl.java
    └── AiSystemPromptServiceImpl.java (已有)
```

## 优势

1. **统一性**：所有CRUD服务都采用相同的架构模式
2. **可测试性**：接口便于单元测试和Mock
3. **可扩展性**：便于后续添加新的实现（如缓存层）
4. **契约明确**：接口明确定义服务能力
5. **符合最佳实践**：遵循DDD和Spring最佳实践

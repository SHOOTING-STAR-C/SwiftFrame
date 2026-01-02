# Mapper 包结构说明

## 包结构

```
com.star.swiftDatasource.mapper
├── mysql/              # MySQL 数据源 Mapper
│   ├── ExampleMysqlMapper.java
│   └── SwiftUserMapper.java
└── postgresql/         # PostgreSQL 数据源 Mapper
    └── ExamplePostgreSqlMapper.java
```

## 使用说明

### MySQL Mapper
所有使用 MySQL 数据源的 Mapper 都应该放在 `com.star.swiftDatasource.mapper.mysql` 包或其子包下。

### PostgreSQL Mapper
所有使用 PostgreSQL 数据源的 Mapper 都应该放在 `com.star.swiftDatasource.mapper.postgresql` 包或其子包下。

## 迁移现有 Mapper

如果需要将现有的 Mapper 从其他模块迁移到新的包结构中，请按照以下步骤：

1. **复制 Mapper 接口**
   - 将原有的 Mapper 接口复制到对应的包（mysql 或 postgresql）
   - 修改包名为新的包名

2. **复制 XML 映射文件**
   - 将对应的 XML 映射文件复制到新的位置
   - 确保命名空间与新的 Mapper 接口包名一致

3. **更新引用**
   - 更新所有引用该 Mapper 的 Service 类
   - 更新 import 语句指向新的包名

## 配置说明

### MySQL MyBatis 配置
- 配置类：`MysqlMybatisConfig`
- 扫描包：`com.star.swiftDatasource.mapper.mysql`
- SqlSessionFactory：`masterSqlSessionFactory`

### PostgreSQL MyBatis 配置
- 配置类：`PostgreSqlMybatisConfig`
- 扫描包：`com.star.swiftDatasource.mapper.postgresql`
- SqlSessionFactory：`pgSqlSessionFactory`

## 注意事项

1. **避免包冲突**：确保同一个 Mapper 不会同时出现在两个不同的包中
2. **保持一致性**：Mapper 接口和 XML 映射文件的命名空间必须一致
3. **条件加载**：配置类会根据数据源是否存在自动加载，无需手动配置

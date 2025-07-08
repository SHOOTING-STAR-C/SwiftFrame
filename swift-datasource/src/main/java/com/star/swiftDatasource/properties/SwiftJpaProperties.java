package com.star.swiftDatasource.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * spring data 配置
 *
 * @author SHOOTING_STAR_C
 */
@Component
@Data
@ConfigurationProperties(prefix = "spring.jpa.properties.hibernate")
public class SwiftJpaProperties {
    private String packagesToScan; //扫描哪些
    private String dialect = null; //指定Hibernate生成特定数据库的SQL（方言）
    private String hbm2ddlAuto = "none"; //DDL 自动生成策略
    private String connectionProviderClass; //连接池提供者
    private boolean allowJdbcMetadataAccess = false; //更新元数据访问设置
    private int jdbcBatchSize = 30; //批量操作大小 (建议值：20-50)
    private boolean orderInserts = true; //插入排序优化
    private boolean orderUpdates = true; //更新排序优化


    /**
     * 将配置属性转换为Hibernate属性Map
     *
     * @return 包含有效Hibernate配置的Map
     */
    public Map<String, Object> toPropertiesMap() {
        Map<String, Object> properties = new HashMap<>();

        // 只添加非空属性
        if (dialect != null) {
            properties.put("hibernate.dialect", dialect);
        }
        if (hbm2ddlAuto != null) {
            properties.put("hibernate.hbm2ddl.auto", hbm2ddlAuto);
        }
        if (connectionProviderClass != null) {
            properties.put("hibernate.connection.provider_class", connectionProviderClass);
        }
        properties.put("hibernate.jdbc.batch_size", jdbcBatchSize);
        properties.put("hibernate.order_inserts", orderInserts);
        properties.put("hibernate.order_updates", orderUpdates);
        properties.put("hibernate.boot.allow_jdbc_metadata_access", allowJdbcMetadataAccess);

        return properties;
    }
}

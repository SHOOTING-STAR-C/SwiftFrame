package com.star.swiftDatasource.routing;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态数据源路由
 *
 * @author SHOOTING_STAR_C
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    private static final ThreadLocal<String> dataSourceKey =
            new InheritableThreadLocal<>(); // 支持线程间传递

    private static final Map<Object, Object> targetDataSources =
            new ConcurrentHashMap<>();

    @Override
    protected Object determineCurrentLookupKey() {
        // 事务上下文中使用绑定数据源
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            String txDataSource = (String) TransactionSynchronizationManager.getResource(DynamicDataSource.class);
            if (txDataSource != null) {
                return txDataSource;
            }
        }
        return dataSourceKey.get();
    }

    public static void setDataSource(String key) {
        dataSourceKey.set(key);
    }

    public static void clearDataSource() {
        dataSourceKey.remove();
    }

    /**
     * 添加数据源
     *
     * @param key 数据源名称
     * @param dataSource 数据源实体
     */
    public void addDataSource(String key, DataSource dataSource) {
        targetDataSources.put(key, dataSource);
        super.setTargetDataSources(targetDataSources);
        super.afterPropertiesSet(); // 刷新数据源映射
    }

    /**
     * 获取当前数据源
     */
    public DataSource getCurrentDataSource() {
        String key = Objects.requireNonNull(determineCurrentLookupKey()).toString();
        return (DataSource) targetDataSources.get(key);
    }
}

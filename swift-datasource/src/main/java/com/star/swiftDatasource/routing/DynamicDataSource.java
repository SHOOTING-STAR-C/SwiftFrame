package com.star.swiftDatasource.routing;

import com.star.swiftDatasource.constants.DataSourceEnum;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态数据源路由
 *
 * @author SHOOTING_STAR_C
 */
@Getter
@Slf4j
public class DynamicDataSource extends AbstractRoutingDataSource {

    private static final Map<Object, Object> targetDataSources =
            new ConcurrentHashMap<>();

    @Override
    protected Object determineCurrentLookupKey() {
        DataSourceEnum ds = DataSourceContextHolder.getDataSource();
        return ds != null ? ds.getName() : null;
    }

    /**
     * 添加数据源
     *
     * @param key        数据源名称
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

    /**
     * 根据数据源key获取对应的数据源
     *
     * @param key 数据源key
     * @return 数据源实例
     */
    public DataSource getDataSource(DataSourceEnum key) {
        log.info(targetDataSources.toString());
        return (DataSource) targetDataSources.get(key.name());
    }
}

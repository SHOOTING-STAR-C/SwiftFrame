package com.star.swiftDatasource.config;


import com.alibaba.druid.pool.DruidDataSource;
import com.star.swiftDatasource.constants.DataSourceEnum;
import com.star.swiftDatasource.properties.DruidProperties;
import com.star.swiftDatasource.routing.DynamicDataSource;
import com.star.swiftDatasource.transaction.MultiDataSourceTransactionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据源配置类
 *
 * @author SHOOTING_STAR_C
 */
@Configuration
@Slf4j
public class DataSourceConfig {
    /**
     * 加载数据库配置
     *
     * @param druidProperties druidProperties
     * @return DataSource
     * @throws SQLException SQLException
     */
    @Bean(name = "masterDataSource")
    public DruidDataSource masterDataSource(DruidProperties druidProperties) throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        return druidProperties.dataSource(dataSource);
    }

    /**
     * 加载连接池
     *
     * @param master master
     * @return DynamicDataSource
     */
    @Bean(name = "dynamicDataSource")
    @Primary
    public DynamicDataSource dynamicDataSource(
            @Qualifier("masterDataSource") DruidDataSource master) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        // 如果有多个数据源，在此处添加
        targetDataSources.put(DataSourceEnum.MASTER.getName(), master);

        DynamicDataSource ds = new DynamicDataSource();
        ds.setTargetDataSources(targetDataSources);
        ds.setDefaultTargetDataSource(master);
        log.info("已加载数据源: {}", targetDataSources.keySet());
        return ds;
    }

    /**
     * 自定义事务管理器
     *
     * @param dataSource dataSource
     * @return PlatformTransactionManager
     */
    @Bean(name = "multiTransactionManager")
    @Primary
    public PlatformTransactionManager multiTransactionManager(DynamicDataSource dataSource) {
        return new MultiDataSourceTransactionManager(dataSource);
    }
}

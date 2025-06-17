package com.star.swiftdatasource.config;


import com.alibaba.druid.pool.DruidDataSource;
import com.star.swiftdatasource.constants.DataSourceEnum;
import com.star.swiftdatasource.properties.DruidProperties;
import com.star.swiftdatasource.routing.DynamicDataSource;
import com.star.swiftdatasource.transaction.MultiDataSourceTransactionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
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
    @Bean(name = "masterDataSource")
    public DataSource masterDataSource(DruidProperties druidProperties) {
        DruidDataSource dataSource = new DruidDataSource();
        return druidProperties.dataSource(dataSource);
    }

    @Bean(name = "dynamicDataSource")
    @Primary
    public DynamicDataSource dynamicDataSource(
            @Qualifier("masterDataSource") DataSource master) {
        log.info("正在加载数据源");
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceEnum.MASTER.getName(), master);

        DynamicDataSource ds = new DynamicDataSource();
        ds.setTargetDataSources(targetDataSources);
        ds.setDefaultTargetDataSource(master);

        log.info("已加载数据源: {}", targetDataSources.keySet());
        return ds;
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(DynamicDataSource dataSource) {
        return new MultiDataSourceTransactionManager(dataSource);
    }
}

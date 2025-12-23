package com.star.swiftDatasource.config;


import com.alibaba.druid.pool.DruidDataSource;
import com.star.swiftDatasource.constants.DataSourceEnum;
import com.star.swiftDatasource.properties.DruidProperties;
import com.star.swiftDatasource.properties.PgDruidProperties;
import com.star.swiftDatasource.routing.DynamicDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
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
     * 加载PG数据源
     *
     * @param pgDruidProperties pgDruidProperties
     * @return DataSource
     * @throws SQLException SQLException
     */
    @Bean(name = "pgDataSource")
    public DruidDataSource pgDataSource(PgDruidProperties pgDruidProperties) throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        return pgDruidProperties.dataSource(dataSource);
    }

    @Value("classpath:schema-pg.sql")
    private Resource pgSchemaScript;

    @Bean
    public DataSourceInitializer pgDataSourceInitializer(@Qualifier("pgDataSource") DataSource pgDataSource) {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(pgDataSource);
        initializer.setDatabasePopulator(pgDatabasePopulator());
        return initializer;
    }

    private DatabasePopulator pgDatabasePopulator() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(pgSchemaScript);
        populator.setContinueOnError(true);
        return populator;
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
            @Qualifier("masterDataSource") DruidDataSource master,
            @Qualifier("pgDataSource") DruidDataSource pg) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        // 如果有多个数据源，在此处添加
        targetDataSources.put(DataSourceEnum.MASTER.getName(), master);
        targetDataSources.put(DataSourceEnum.PG.getName(), pg);

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
        return new org.springframework.jdbc.datasource.DataSourceTransactionManager(dataSource);
    }
}

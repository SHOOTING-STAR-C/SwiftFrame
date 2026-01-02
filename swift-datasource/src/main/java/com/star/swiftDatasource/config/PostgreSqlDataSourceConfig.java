package com.star.swiftDatasource.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.star.swiftDatasource.properties.PgDruidProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * PostgreSQL 数据源配置类
 *
 * @author SHOOTING_STAR_C
 */
@Configuration
@Slf4j
@ConditionalOnProperty(
        prefix = "spring.datasource.druid.pg",
        name = "url"
)
public class PostgreSqlDataSourceConfig {

    @Autowired
    private PgDruidProperties pgDruidProperties;

    /**
     * 加载 PostgreSQL 数据源
     *
     * @return DataSource
     * @throws SQLException SQLException
     */
    @Bean(name = "pgDataSource")
    public DataSource pgDataSource() throws SQLException {
        log.info("初始化 PostgreSQL 数据源...");
        DruidDataSource dataSource = new DruidDataSource();
        return pgDruidProperties.dataSource(dataSource);
    }

    /**
     * PG数据源初始化器（可选）
     *
     * @param pgDataSource PG数据源
     * @return DataSourceInitializer
     */
    @Bean
    @ConditionalOnProperty(
            prefix = "spring.datasource.druid.pg",
            name = "initializeSchema",
            havingValue = "true",
            matchIfMissing = true
    )
    public DataSourceInitializer pgDataSourceInitializer(DataSource pgDataSource) {
        log.info("初始化 PostgreSQL 数据源初始化器...");
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(pgDataSource);
        initializer.setDatabasePopulator(pgDatabasePopulator());
        return initializer;
    }

    /**
     * PG数据库初始化脚本
     *
     * @return DatabasePopulator
     */
    private DatabasePopulator pgDatabasePopulator() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource scriptResource = resourceLoader.getResource(pgDruidProperties.getSchemaScript());
        populator.addScript(scriptResource);
        populator.setContinueOnError(true);
        return populator;
    }
}

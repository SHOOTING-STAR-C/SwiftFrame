package com.star.swiftDatasource.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.star.swiftDatasource.properties.PgDruidProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * PostgreSQL 数据源自动配置类
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@AutoConfiguration
@ConditionalOnProperty(
        prefix = "spring.datasource.druid.pg",
        name = "url"
)
public class PostgreSqlDataSourceConfig {

    private final PgDruidProperties pgDruidProperties;

    public PostgreSqlDataSourceConfig(PgDruidProperties pgDruidProperties) {
        this.pgDruidProperties = pgDruidProperties;
    }

    /**
     * 加载 PostgreSQL 数据源
     *
     * @return DataSource
     * @throws SQLException SQLException
     */
    @Bean(name = "pgDataSource")
    public DataSource pgDataSource() throws SQLException {
        log.info("初始化 PostgreSQL 数据源...");
        DruidDataSource pgDataSource = new DruidDataSource();
        return pgDruidProperties.dataSource(pgDataSource);
    }

    /**
     * PG数据源初始化器（可选）
     *
     * @param pgDataSource PG数据源
     * @return DataSourceInitializer
     */
    @Bean
    @org.springframework.core.annotation.Order(2)
    @ConditionalOnProperty(
            prefix = "spring.datasource.druid.pg",
            name = "initializeSchema",
            havingValue = "true",
            matchIfMissing = true
    )
    public DataSourceInitializer pgDataSourceInitializer(@Qualifier("pgDataSource") DataSource pgDataSource) {
        log.info("初始化 PostgreSQL 数据源初始化器...");
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(pgDataSource);
        initializer.setDatabasePopulator(pgDatabasePopulator());
        return initializer;
    }

    /**
     * PG数据库初始化脚本（支持通配符）
     *
     * @return DatabasePopulator
     */
    private DatabasePopulator pgDatabasePopulator() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        
        try {
            // 加载 SQL 脚本（支持通配符，例如：classpath:sql/postgresql/*.sql）
            if (pgDruidProperties.getSqlScripts() != null) {
                Resource[] resources = resourcePatternResolver.getResources(pgDruidProperties.getSqlScripts());
                for (Resource resource : resources) {
                    log.info("加载 PostgreSQL SQL 脚本: {}", resource.getFilename());
                    populator.addScript(resource);
                }
            }
        } catch (Exception e) {
            log.error("加载 SQL 脚本失败", e);
            throw new RuntimeException("加载 SQL 脚本失败", e);
        }
        
        populator.setContinueOnError(true);
        return populator;
    }
}

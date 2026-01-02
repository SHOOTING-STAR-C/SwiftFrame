package com.star.swiftDatasource.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.star.swiftDatasource.properties.DruidProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * MySQL 数据源自动配置类
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
@AutoConfiguration
@ConditionalOnProperty(
        prefix = "spring.datasource.druid.master",
        name = "url"
)
public class MysqlDataSourceConfig {

    private final DruidProperties druidProperties;

    public MysqlDataSourceConfig(DruidProperties druidProperties) {
        this.druidProperties = druidProperties;
    }

    /**
     * 加载 MySQL 主数据源
     *
     * @return DataSource
     * @throws SQLException SQLException
     */
    @Bean(name = "masterDataSource")
    @Primary
    public DataSource masterDataSource() throws SQLException {
        log.info("初始化 MySQL 主数据源...");
        DruidDataSource dataSource = new DruidDataSource();
        return druidProperties.dataSource(dataSource);
    }

    /**
     * MySQL数据源初始化器
     *
     * @param masterDataSource MySQL主数据源
     * @return DataSourceInitializer
     */
    @Bean
    @org.springframework.core.annotation.Order(1)
    @ConditionalOnProperty(
            prefix = "spring.datasource.druid.master",
            name = "initializeSchema",
            havingValue = "true",
            matchIfMissing = true
    )
    public DataSourceInitializer mysqlDataSourceInitializer(DataSource masterDataSource) {
        log.info("初始化 MySQL 数据源初始化器...");
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(masterDataSource);
        initializer.setDatabasePopulator(mysqlDatabasePopulator());
        return initializer;
    }

    /**
     * MySQL数据库初始化脚本（支持通配符）
     *
     * @return DatabasePopulator
     */
    private DatabasePopulator mysqlDatabasePopulator() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        
        try {
            // 加载 SQL 脚本（支持通配符，例如：classpath:sql/mysql/*.sql）
            if (druidProperties.getSqlScripts() != null) {
                Resource[] resources = resourcePatternResolver.getResources(druidProperties.getSqlScripts());
                for (Resource resource : resources) {
                    log.info("加载 MySQL SQL 脚本: {}", resource.getFilename());
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

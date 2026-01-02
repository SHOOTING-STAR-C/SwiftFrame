package com.star.swiftDatasource.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.star.swiftDatasource.properties.DruidProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * MySQL 数据源配置类
 *
 * @author SHOOTING_STAR_C
 */
@Configuration
@Slf4j
@ConditionalOnProperty(
        prefix = "spring.datasource.druid.master",
        name = "url"
)
public class MysqlDataSourceConfig {

    @Autowired
    private DruidProperties druidProperties;

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
}

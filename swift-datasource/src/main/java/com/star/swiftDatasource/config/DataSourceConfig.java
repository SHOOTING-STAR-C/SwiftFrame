package com.star.swiftDatasource.config;


import com.alibaba.druid.pool.DruidDataSource;
import com.star.swiftDatasource.constants.DataSourceEnum;
import com.star.swiftDatasource.properties.DruidProperties;
import com.star.swiftDatasource.properties.SwiftJpaProperties;
import com.star.swiftDatasource.routing.DynamicDataSource;
import com.star.swiftDatasource.transaction.MultiDataSourceTransactionManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据源配置类
 *
 * @author SHOOTING_STAR_C
 */
@EnableJpaRepositories(basePackages = "com.star.*.repository")
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
    public PlatformTransactionManager multiTransactionManager(DynamicDataSource dataSource) {
        return new MultiDataSourceTransactionManager(dataSource);
    }

    /**
     * jpa的事务管理器
     *
     * @param entityManagerFactory entityManagerFactory
     * @return PlatformTransactionManager
     */
    @Bean(name = "transactionManager")
    @Primary
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

    /**
     * 指定jpa使用的数据源
     *
     * @param dataSource         dataSource
     * @param swiftJpaProperties swiftJpaProperties
     * @return LocalContainerEntityManagerFactoryBean
     */
    @Bean(name = "entityManagerFactory")
    @DependsOn("dynamicDataSource")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier("dynamicDataSource") DynamicDataSource dataSource, SwiftJpaProperties swiftJpaProperties) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan(swiftJpaProperties.getPackagesToScan());
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        em.setJpaPropertyMap(swiftJpaProperties.toPropertiesMap());
        return em;
    }
}

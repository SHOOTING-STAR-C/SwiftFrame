package com.star.swiftDatasource.config;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.star.swiftDatasource.handler.UuidTypeHandler;
import com.star.swiftDatasource.properties.DruidProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * MySQL MyBatis 配置类
 *
 * @author SHOOTING_STAR_C
 */
@Configuration
@Slf4j
@ConditionalOnBean(name = "masterDataSource")
@MapperScan(
    basePackages = {"com.star.**.mapper.mysql"},
    sqlSessionFactoryRef = "masterSqlSessionFactory"
)
public class MysqlMybatisConfig {

    @Autowired
    private DruidProperties druidProperties;

    /**
     * Master 数据源的 SqlSessionFactory (MySQL)
     *
     * @param masterDataSource master数据源
     * @return SqlSessionFactory
     * @throws Exception Exception
     */
    @Bean(name = "masterSqlSessionFactory")
    @Primary
    public SqlSessionFactory masterSqlSessionFactory(
            @Qualifier("masterDataSource") DataSource masterDataSource) throws Exception {
        log.info("初始化 MySQL MyBatis-Plus SqlSessionFactory...");
        MybatisSqlSessionFactoryBean sessionFactory = new MybatisSqlSessionFactoryBean();
        sessionFactory.setDataSource(masterDataSource);
        sessionFactory.setTypeHandlers(new UuidTypeHandler());
        
        // 设置 GlobalConfig，关闭 banner
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setBanner(false);
        sessionFactory.setGlobalConfig(globalConfig);
        
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        // 从 master 数据源配置中读取 mapper-locations
        String location = "classpath*:sqlmapper/mysql/*.xml";
        if (druidProperties.getMapperLocations() != null && !druidProperties.getMapperLocations().isEmpty()) {
            location = druidProperties.getMapperLocations();
        }
        sessionFactory.setMapperLocations(resolver.getResources(location));

        return sessionFactory.getObject();
    }
}

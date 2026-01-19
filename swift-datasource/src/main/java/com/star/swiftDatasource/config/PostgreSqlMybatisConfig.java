package com.star.swiftDatasource.config;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.star.swiftDatasource.handler.MyMetaObjectHandler;
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
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * PostgreSQL MyBatis 配置类
 *
 * @author SHOOTING_STAR_C
 */
@Configuration
@Slf4j
@ConditionalOnBean(name = "pgDataSource")
@MapperScan(
    basePackages = {"com.star.**.mapper.postgresql"},
    sqlSessionFactoryRef = "postgreSqlSqlSessionFactory"
)
public class PostgreSqlMybatisConfig {

    @Autowired
    private DruidProperties druidProperties;

    @Autowired
    private MyMetaObjectHandler myMetaObjectHandler;

    /**
     * PostgreSQL 数据源的 SqlSessionFactory
     *
     * @param pgDataSource PostgreSQL数据源
     * @return SqlSessionFactory
     * @throws Exception Exception
     */
    @Bean(name = "postgreSqlSqlSessionFactory")
    public SqlSessionFactory postgreSqlSqlSessionFactory(
            @Qualifier("pgDataSource") DataSource pgDataSource) throws Exception {
        log.info("初始化 PostgreSQL MyBatis-Plus SqlSessionFactory...");
        MybatisSqlSessionFactoryBean sessionFactory = new MybatisSqlSessionFactoryBean();
        sessionFactory.setDataSource(pgDataSource);
        sessionFactory.setTypeHandlers(new UuidTypeHandler());
        
        // 设置 GlobalConfig，关闭 banner 并配置自动填充
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setBanner(false);
        globalConfig.setMetaObjectHandler(myMetaObjectHandler);
        sessionFactory.setGlobalConfig(globalConfig);
        
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        // 从 PostgreSQL 数据源配置中读取 mapper-locations
        String location = "classpath*:sqlmapper/postgresql/*.xml";
        if (druidProperties.getMapperLocations() != null && !druidProperties.getMapperLocations().isEmpty()) {
            location = druidProperties.getMapperLocations();
        }
        sessionFactory.setMapperLocations(resolver.getResources(location));

        return sessionFactory.getObject();
    }
}

package com.star.swiftDatasource.config;

import com.star.swiftDatasource.handler.UuidTypeHandler;
import com.star.swiftDatasource.properties.MybatisProperties;
import com.star.swiftDatasource.properties.PgDruidProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
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
    sqlSessionFactoryRef = "pgSqlSessionFactory"
)
public class PostgreSqlMybatisConfig {

    @Autowired
    private MybatisProperties mybatisProperties;

    @Autowired
    private PgDruidProperties pgDruidProperties;

    /**
     * PG 数据源的 SqlSessionFactory (PostgreSQL)
     *
     * @param pgDataSource pg数据源
     * @return SqlSessionFactory
     * @throws Exception Exception
     */
    @Bean(name = "pgSqlSessionFactory")
    public SqlSessionFactory pgSqlSessionFactory(
            @Qualifier("pgDataSource") DataSource pgDataSource) throws Exception {
        log.info("初始化 PostgreSQL MyBatis SqlSessionFactory...");
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(pgDataSource);
        sessionFactory.setTypeHandlers(new TypeHandler[]{new UuidTypeHandler()});
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        // 从 pg 数据源配置中读取 mapper-locations
        String location = "classpath*:sqlmapper/postgresql/*.xml";
        if (pgDruidProperties.getMapperLocations() != null && !pgDruidProperties.getMapperLocations().isEmpty()) {
            location = pgDruidProperties.getMapperLocations();
        }
        sessionFactory.setMapperLocations(resolver.getResources(location));

        // 配置 MyBatis 设置
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(mybatisProperties.isMapUnderscoreToCamelCase());
        sessionFactory.setConfiguration(configuration);

        return sessionFactory.getObject();
    }
}

package com.star.swiftDatasource.config;

import com.star.swiftDatasource.routing.DynamicDataSource;
import com.star.swiftDatasource.handler.UuidTypeHandler;
import com.star.swiftDatasource.properties.MybatisProperties;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * MyBatis 自动配置类
 *
 * @author SHOOTING_STAR_C
 */
@AutoConfiguration
@MapperScan(basePackages = "com.star.*.mapper", sqlSessionFactoryRef = "masterSqlSessionFactory")
public class MybatisAutoConfiguration {

    @Autowired
    private MybatisProperties mybatisProperties;

    /**
     * 主sql工厂
     *
     * @param dataSource dataSource
     * @return SqlSessionFactory
     * @throws Exception Exception
     */
    @Bean(name = "masterSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(DynamicDataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setTypeHandlers(new TypeHandler[]{new UuidTypeHandler()});
        org.springframework.core.io.support.PathMatchingResourcePatternResolver resolver = new org.springframework.core.io.support.PathMatchingResourcePatternResolver();

        String location = "classpath*:sqlmapper/*.xml";
        if (mybatisProperties.getMapperLocations() != null && mybatisProperties.getMapperLocations().containsKey("master")) {
            location = mybatisProperties.getMapperLocations().get("master");
        }
        sessionFactory.setMapperLocations(resolver.getResources(location));
        
        // 配置 MyBatis 设置
        Configuration configuration = new Configuration();
        configuration.setMapUnderscoreToCamelCase(mybatisProperties.isMapUnderscoreToCamelCase());
        sessionFactory.setConfiguration(configuration);
        
        return sessionFactory.getObject();
    }
}

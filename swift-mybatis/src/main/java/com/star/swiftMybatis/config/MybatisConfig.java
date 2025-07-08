package com.star.swiftMybatis.config;

import com.star.swiftDatasource.routing.DynamicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置mybatis配置类
 *
 * @author SHOOTING_STAR_C
 */
@Configuration
@MapperScan(basePackages = "com.star.*.mapper", sqlSessionFactoryRef = "masterSqlSessionFactory")
public class MybatisConfig {
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
        return sessionFactory.getObject();
    }
}

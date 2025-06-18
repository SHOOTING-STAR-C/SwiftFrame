package com.star.swiftdatasource.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * 配置mybatis配置类
 *
 * @author SHOOTING_STAR_C
 */
@Configuration
@MapperScan(basePackages = "com.star.*.mapper", sqlSessionFactoryRef = "masterSqlSessionFactory")
public class BaseMyBatisConfig {
}

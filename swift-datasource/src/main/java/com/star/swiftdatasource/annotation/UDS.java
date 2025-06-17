package com.star.swiftdatasource.annotation;

import com.star.swiftdatasource.constants.DataSourceEnum;

import java.lang.annotation.*;

/**
 * 数据库注解
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UDS {
    /**
     * 数据源名称 (对应配置中的spring.datasource.[name])
     * 默认使用主库
     */
    DataSourceEnum value() default DataSourceEnum.MASTER;
}

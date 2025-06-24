package com.star.swiftDatasource.annotation;

import com.star.swiftDatasource.constants.DataSourceEnum;

import java.lang.annotation.*;

/**
 * 切换数据源注解
 *
 * @author SHOOTING_STAR_C
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

package com.star.swiftDatasource.aspect;

import com.star.swiftDatasource.annotation.UDS;
import com.star.swiftDatasource.constants.DataSourceEnum;
import com.star.swiftDatasource.routing.DataSourceContextHolder;
import com.star.swiftDatasource.routing.DynamicDataSource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 切换数据源切面
 *
 * @author SHOOTING_STAR_C
 */
@Aspect
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1) // 最高优先级
public class DataSourceAspect {

    @Around("execution(* *(..)) && @annotation(uds)")
    public Object around(ProceedingJoinPoint point, UDS uds) throws Throwable {
        DataSourceEnum dsEnum = uds.value();

        try {
            DataSourceContextHolder.pushDataSource(dsEnum);
            log.debug("切换数据源到: {}", dsEnum.getName());
            return point.proceed();
        } finally {
            DataSourceContextHolder.popDataSource();
            log.debug("恢复数据源: {}",
                    DataSourceContextHolder.getDataSource() != null ?
                            DataSourceContextHolder.getDataSource().getName() : "默认");
        }
    }
}

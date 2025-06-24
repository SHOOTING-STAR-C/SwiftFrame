package com.star.swiftDatasource.aspect;

import com.star.swiftDatasource.annotation.UDS;
import com.star.swiftDatasource.constants.DataSourceEnum;
import com.star.swiftDatasource.routing.DataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

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

        // 安全检查事务状态
        TransactionStatus txStatus = null;
        try {
            txStatus = TransactionAspectSupport.currentTransactionStatus();
        } catch (Exception e) {
            // 无事务上下文时忽略
            log.debug("无事务方法，已放行：{}", e.getMessage());
        }

        // 存在事务且非新事务时禁止切换
        if (txStatus != null && !txStatus.isNewTransaction()) {
            log.warn("事务内禁止切换数据源: {}", dsEnum.getName());
            return point.proceed();
        }

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

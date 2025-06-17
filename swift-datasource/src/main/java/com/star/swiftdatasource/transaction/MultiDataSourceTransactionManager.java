package com.star.swiftdatasource.transaction;

import com.star.swiftdatasource.constants.DataSourceEnum;
import com.star.swiftdatasource.routing.DataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;

import javax.sql.DataSource;


/**
 * 事务增强
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
public class MultiDataSourceTransactionManager extends DataSourceTransactionManager {

    public MultiDataSourceTransactionManager(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        // 事务开始前锁定数据源
        DataSourceEnum ds = DataSourceContextHolder.getDataSource();
        if (ds == null) {
            ds = DataSourceEnum.MASTER;
            DataSourceContextHolder.pushDataSource(ds);
        }
        log.debug("事务锁定数据源: {}", ds.getName());
        super.doBegin(transaction, definition);
    }

    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        super.doCleanupAfterCompletion(transaction);
        // 事务完成后清理上下文
        DataSourceContextHolder.clear();
    }
}

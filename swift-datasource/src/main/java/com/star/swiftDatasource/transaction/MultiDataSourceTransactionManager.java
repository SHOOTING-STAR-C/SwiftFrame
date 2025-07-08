package com.star.swiftDatasource.transaction;

import com.star.swiftDatasource.constants.DataSourceEnum;
import com.star.swiftDatasource.routing.DataSourceContextHolder;
import com.star.swiftDatasource.routing.DynamicDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;


/**
 * 事务增强
 *
 * @author SHOOTING_STAR_C
 */
@Slf4j
public class MultiDataSourceTransactionManager extends DataSourceTransactionManager {

    private final DynamicDataSource dynamicDataSource;

    public MultiDataSourceTransactionManager(DynamicDataSource dynamicDataSource) {
        super(dynamicDataSource);
        this.dynamicDataSource = dynamicDataSource;
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        // 获取当前线程指定的数据源
        DataSourceEnum ds = DataSourceContextHolder.getDataSource();
        if (ds == null) {
            ds = DataSourceEnum.MASTER;
        }
        String dsKey = ds.name();

        // 绑定到事务资源（关键步骤）
        TransactionSynchronizationManager.bindResource(DynamicDataSource.class, dsKey);

        // 设置真实数据源给父类
        DataSource dataSource = dynamicDataSource.getDataSource(ds);
        super.setDataSource(dataSource);

        // 调用父类方法初始化连接和事务
        super.doBegin(transaction, definition);

        // 绑定数据源到上下文（事务开始时）
        DataSourceContextHolder.pushDataSource(ds);
        log.debug("事务锁定数据源: {}", ds.getName());
    }

    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        try {
            super.doCleanupAfterCompletion(transaction);
        } finally {
            // 解绑事务资源（关键步骤）
            TransactionSynchronizationManager.unbindResource(DynamicDataSource.class);
            // 清理数据源上下文
            DataSourceContextHolder.clear();
        }
    }
}

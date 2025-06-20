package com.star.swiftdatasource.properties;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Druid读取配置文件
 *
 * @author SHOOTING_STAR_C
 */
@Component
@Data
@ConfigurationProperties(prefix = "spring.datasource.druid.master")
public class DruidProperties {
    // 基础连接配置
    private String url;
    private String username;
    private String password;
    private String driverClassName;

    // 连接池配置
    private int initialSize = 5;
    private int minIdle = 5;
    private int maxActive = 20;
    private int maxWait = 60000;
    private int timeBetweenEvictionRunsMillis = 60000;
    private int minEvictableIdleTimeMillis = 300000;
    private String validationQuery = "SELECT 1 from dual";
    private boolean testWhileIdle = true;
    private boolean testOnBorrow = false;
    private boolean testOnReturn = false;

    public DruidDataSource dataSource(DruidDataSource datasource) {
        /* 配置基础连接 */
        datasource.setUrl(url);
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setDriverClassName(driverClassName);

        /* 配置初始化大小、最小、最大 */
        datasource.setInitialSize(initialSize);
        datasource.setMaxActive(maxActive);
        datasource.setMinIdle(minIdle);

        /* 配置获取连接等待超时的时间 */
        datasource.setMaxWait(maxWait);

        /* 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒 */
        datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);

        /* 配置一个连接在池中最小、最大生存的时间，单位是毫秒 */
        datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        //datasource.setMaxEvictableIdleTimeMillis(maxEvictableIdleTimeMillis);

        /*
          用来检测连接是否有效的sql，要求是一个查询语句，常用select 'x'。如果validationQuery为null，testOnBorrow、testOnReturn、testWhileIdle都不会起作用。
         */
        datasource.setValidationQuery(validationQuery);
        /* 建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。 */
        datasource.setTestWhileIdle(testWhileIdle);
        /* 申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。 */
        datasource.setTestOnBorrow(testOnBorrow);
        /* 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。 */
        datasource.setTestOnReturn(testOnReturn);
        return datasource;
    }
}

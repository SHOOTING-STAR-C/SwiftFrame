package com.star.swiftDatasource.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * MyBatis 映射配置属性
 *
 * @author SHOOTING_STAR_C
 */
@Configuration
@ConfigurationProperties(prefix = "swift.mybatis")
public class MybatisProperties {

    /**
     * Mapper 路径配置，key 为数据源/库标识，value 为 mapper xml 路径
     * 例如：
     * swift:
     *   mybatis:
     *     mapper-locations:
     *       master: classpath*:sqlmapper/*.xml
     *       other: classpath*:sqlmapper/other/*.xml
     */
    private Map<String, String> mapperLocations;

    /**
     * 是否开启驼峰命名自动转换
     * 例如：数据库字段 user_id 自动映射到 Java 属性 userId
     */
    private boolean mapUnderscoreToCamelCase = true;

    public Map<String, String> getMapperLocations() {
        return mapperLocations;
    }

    public void setMapperLocations(Map<String, String> mapperLocations) {
        this.mapperLocations = mapperLocations;
    }

    public boolean isMapUnderscoreToCamelCase() {
        return mapUnderscoreToCamelCase;
    }

    public void setMapUnderscoreToCamelCase(boolean mapUnderscoreToCamelCase) {
        this.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
    }
}

package com.star.swiftDatasource.constants;

import lombok.Getter;

/**
 * 数据库常量类
 *
 * @author SHOOTING_STAR_C
 */
public enum DataSourceEnum {

    MASTER("master", "主库", true),
    PG("pg", "PostgreSQL库", true);

    private final String name;   // 数据源名称（对应配置）
    private final String desc;   // 描述信息
    private final boolean writable; // 是否可写

    DataSourceEnum(String name, String desc, boolean writable) {
        this.name = name;
        this.desc = desc;
        this.writable = writable;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public boolean isWritable() {
        return writable;
    }

    /**
     * 按名称查找枚举（避免大小写问题）
     *
     * @param name 数据源名称
     * @return 匹配的枚举
     * @throws IllegalArgumentException 如果找不到匹配项
     */
    public static DataSourceEnum byName(String name) {
        for (DataSourceEnum value : values()) {
            if (value.name.equalsIgnoreCase(name)) {
                return value;
            }
        }
        throw new IllegalArgumentException("未知数据源: " + name);
    }

}

package com.star.swiftStart.mapper;

import org.apache.ibatis.annotations.Select;

public interface TestMapper {
    @Select("select count(1) from sys_config")
    int selectNumber();
}

package com.star.swiftBusiness.mapper;

import com.star.swiftBusiness.entity.Bussarea;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BussareaMapper {
    @Select("SELECT area_id as areaId, parent_id as parentId, area_name as areaName FROM buss_area WHERE parent_id = #{parentId}")
    List<Bussarea> findByParentId(@Param("parentId") Integer parentId);

    @Select("SELECT area_id as areaId, parent_id as parentId, area_name as areaName FROM buss_area")
    List<Bussarea> findAll();
}

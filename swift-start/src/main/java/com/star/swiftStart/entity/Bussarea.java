package com.star.swiftStart.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "buss_area")
public class Bussarea {
    @Id // 必须指定主键
    @Column(name = "area_id")
    private Integer areaId;
    @Column(name = "parent_id")
    private Integer parentId;
    @Column(name = "area_name")
    private String areaName;
}

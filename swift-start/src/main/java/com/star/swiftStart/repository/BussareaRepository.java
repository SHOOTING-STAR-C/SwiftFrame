package com.star.swiftStart.repository;

import com.star.swiftStart.entity.Bussarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BussareaRepository extends JpaRepository<Bussarea, Integer> {
    // 方法1：使用Spring Data JPA命名约定查询
    List<Bussarea> findByParentId(Integer parentId);
}

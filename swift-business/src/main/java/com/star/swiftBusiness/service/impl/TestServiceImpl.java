package com.star.swiftBusiness.service.impl;

import com.star.swiftBusiness.entity.Bussarea;
import com.star.swiftBusiness.mapper.BussareaMapper;
import com.star.swiftBusiness.mapper.TestMapper;
import com.star.swiftBusiness.service.TestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final TestMapper testMapper;

    private final BussareaMapper bussareaMapper;

    @Override
    public int testCount() {
        log.info("testCount");
        return testMapper.selectNumber();
    }

    // 示例1：获取所有区域

    @Override
    public List<Bussarea> getAllAreas() {
        return bussareaMapper.findAll();
    }
}

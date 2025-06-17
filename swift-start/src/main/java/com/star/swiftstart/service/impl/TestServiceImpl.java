package com.star.swiftstart.service.impl;

import com.star.swiftdatasource.annotation.UDS;
import com.star.swiftstart.mapper.TestMapper;
import com.star.swiftstart.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TestServiceImpl implements TestService {
    @Autowired
    private TestMapper testMapper;

    @UDS
    @Override
    public int testCount() {
        log.info("testCount");
        return testMapper.selectNumber();
    }
}

package com.star.swiftStart.service.impl;

import com.star.swiftDatasource.annotation.UDS;
import com.star.swiftStart.mapper.TestMapper;
import com.star.swiftStart.service.TestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final TestMapper testMapper;

    @UDS
    @Override
    public int testCount() {
        log.info("testCount");
        return testMapper.selectNumber();
    }
}

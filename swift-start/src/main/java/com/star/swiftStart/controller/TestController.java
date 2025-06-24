package com.star.swiftStart.controller;

import com.star.swiftCommon.domain.PubResult;
import com.star.swiftStart.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;
    @GetMapping("/count")
    public PubResult<Integer> test(){
        int i = testService.testCount();
        return PubResult.success(i);
    }
}

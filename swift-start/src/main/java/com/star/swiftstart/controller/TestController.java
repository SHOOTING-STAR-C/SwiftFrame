package com.star.swiftstart.controller;

import com.star.swiftcommon.domain.PubResult;
import com.star.swiftstart.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private TestService testService;
    @GetMapping("/count")
    public PubResult<Integer> test(){
        int i = testService.testCount();
        return PubResult.success(i);
    }
}

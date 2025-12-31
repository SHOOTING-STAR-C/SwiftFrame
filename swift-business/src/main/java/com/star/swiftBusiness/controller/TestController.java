package com.star.swiftBusiness.controller;

import com.star.swiftCommon.domain.PubResult;
import com.star.swiftBusiness.entity.Bussarea;
import com.star.swiftBusiness.service.TestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
@Tag(name = "测试管理", description = "系统测试接口")
public class TestController {

    private final TestService testService;

    @GetMapping("/count")
    @Operation(summary = "获取测试计数")
    public PubResult<Integer> test() {
        int i = testService.testCount();
        return PubResult.success(i);
    }

    @GetMapping("/area")
    @Operation(summary = "获取所有区域列表")
    public PubResult<List<Bussarea>> testArea() {
        return PubResult.success(testService.getAllAreas());
    }
}

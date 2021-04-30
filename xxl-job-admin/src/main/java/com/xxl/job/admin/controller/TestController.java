package com.xxl.job.admin.controller;

import com.xxl.job.core.biz.model.ReturnT;
import org.apache.groovy.util.Maps;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * description 测试控制器
 *
 * @author ZY
 * @date 2021年04月29日 22:37
 **/
@RestController
@RequestMapping("/api/user")
public class TestController {

    @GetMapping("/getInfo")
    public ReturnT<Map<String, Object>> getUserInfo(@RequestParam Integer id) {
        System.out.println("入参：" + id);
        Map<String, Object> map = Maps.of("name", "ZY");
        return new ReturnT<>(map);
    }

}

package com.xxl.job.core.register;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.xxl.job.core.util.HttpRequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @program: xxl-job
 * @className: RegisterRunner.java
 * @description:
 * @author: ZY
 * @create: 2020年11月26日 23:52
 **/
@Slf4j
@Component
public class RegisterRunner implements CommandLineRunner {

    public static final String REGISTER_URL = "/auto/register";

    @Override
    public void run(String... args) {
        Map<String, Object> params = new HashMap<>(3);
        params.put("appname", AutoRegisterConfig.appName);
        params.put("title", AutoRegisterConfig.title);
        params.put("addressList", AutoRegisterConfig.address);
        if ("".equals(AutoRegisterConfig.appName) || "".equals(AutoRegisterConfig.address)) {
            log.info("应用已添加xxl-job-core依赖，但未开启执行器自动注册功能");
            return;
        }
        try {
            Map result = HttpRequestUtil.sendPost(AutoRegisterConfig.registryAddress + REGISTER_URL, null, params, Map.class);
            log.info("注册接口：{}，注册参数：{}", AutoRegisterConfig.registryAddress + REGISTER_URL, JSON.toJSONString(params));
            int okStatus = 200;
            if (result != null && Objects.equals(result.get("code"), okStatus)) {
                log.info("应用名称：{}", AutoRegisterConfig.appName);
                log.info("应用标题：{}", AutoRegisterConfig.title);
                log.info("注册成功：{}", JSON.toJSONString(result));
            } else {
                log.error("应用已添加xxl-job-core依赖，已开启执行器自动注册功能，但注册失败\n" + result);
            }
        } catch (Exception e) {
            log.error("应用已添加xxl-job-core依赖，已开启执行器自动注册功能，但注册失败：", e);
        }

    }
}

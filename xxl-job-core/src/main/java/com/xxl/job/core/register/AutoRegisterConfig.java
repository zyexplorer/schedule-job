package com.xxl.job.core.register;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @program: xxl-job
 * @className: AutoRegister.java
 * @description:
 * @author: ZY
 * @create: 2020年11月26日 23:30
 **/

@Configuration
@ConfigurationProperties(prefix ="xxl.job")
public class AutoRegisterConfig {

    public static String registryAddress;

    public static String appName;

    public static String title;

    public static String address;

    public static String ip;

    public static String port;

    @Value("${xxl.job.admin.addresses:}")
    public void setRegistryAddress(String registryAddress) {
        AutoRegisterConfig.registryAddress = registryAddress;
    }

    @Value("${xxl.job.executor.appname:}")
    public void setAppName(String appName) {
        AutoRegisterConfig.appName = appName;
    }

    @Value("${xxl.job.executor.address:}")
    public void setAddress(String address) {
        AutoRegisterConfig.address = address;
    }

    @Value("${xxl.job.executor.title:}")
    public void setTitle(String title) {
        AutoRegisterConfig.title = title;
    }

    @Value("${xxl.job.executor.ip:}")
    public void setIp(String ip) {
        AutoRegisterConfig.ip = ip;
    }

    @Value("${xxl.job.executor.port:}")
    public void setPort(String port) {
        AutoRegisterConfig.port = port;
    }

}

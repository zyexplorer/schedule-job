package com.xxl.job.admin.controller;

import com.google.gson.Gson;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.util.RequestIpUtil;
import com.xxl.job.admin.dao.XxlJobGroupDao;
import com.xxl.job.core.biz.model.ReturnT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * @program: xxl-job
 * @className: AutoRegisterController.java
 * @description:
 * @author: ZY
 * @create: 2020年11月27日 01:14
 **/
@Slf4j
@RestController
@RequestMapping("/auto")
public class AutoRegisterController {

    @Resource
    private XxlJobGroupDao xxlJobGroupDao;

    @PostMapping("/register")
    @ResponseBody
    public ReturnT<String> autoRegister(HttpServletRequest request, @RequestBody Map<String, String> map) {
        log.info("自动注册入参：" + new Gson().toJson(map));
        String ip = RequestIpUtil.getIp(request);
        log.info("请求IP：{}", ip);

        String appName = map.get("appname");
        String title = map.get("title");
        String addressList = map.get("addressList");

        XxlJobGroup entity = xxlJobGroupDao.findByAppName(appName);
        if (entity == null) {
            entity = new XxlJobGroup();
            entity.setAppname(appName);
            entity.setTitle(title);
            entity.setAddressList(addressList);
            entity.setAddressType(0);
            entity.setCreateTime(new Date());
            xxlJobGroupDao.save(entity);
        } else {
            entity.setAppname(appName);
            entity.setTitle(title);
            entity.setAddressList(addressList);
            entity.setAddressType(0);
            entity.setUpdateTime(new Date());
            xxlJobGroupDao.update(entity);
        }
        return ReturnT.SUCCESS;
    }

}

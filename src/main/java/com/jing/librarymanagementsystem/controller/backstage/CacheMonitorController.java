package com.jing.librarymanagementsystem.controller.backstage;

import com.jing.librarymanagementsystem.service.BackstageActuatorMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

// redis缓存监控
@Controller
@RequestMapping("/backstage")
public class CacheMonitorController {

    @Autowired
    BackstageActuatorMonitorService backstageActuatorMonitorService;

    @RequestMapping(value = "/toCacheMonitor")
    public String toCacheMonitor(){

        return "/backstage/cacheMonitor";
    }

    @ResponseBody
    @RequestMapping(value = "/redisInfo",method = RequestMethod.POST)
    public Map<String,Object> redisInfo(){

        return backstageActuatorMonitorService.redisMemoryInfo();
    }
}

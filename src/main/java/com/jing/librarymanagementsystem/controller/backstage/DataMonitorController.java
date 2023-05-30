package com.jing.librarymanagementsystem.controller.backstage;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jing.librarymanagementsystem.bean.ServletLogBean;
import com.jing.librarymanagementsystem.mappers.ServletLogInfoMapper;
import com.jing.librarymanagementsystem.service.BackstageActuatorMonitorService;
import com.jing.librarymanagementsystem.service.BackstageRedisMonitorService;
import com.jing.librarymanagementsystem.timerTask.DruidRequestLogTask;
import com.jing.librarymanagementsystem.util.HttpClientUtil;
import com.jing.librarymanagementsystem.util.UtilMethods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
//sql数据监控
@RequestMapping("/backstage")
public class DataMonitorController {


    @Autowired
    BackstageActuatorMonitorService backstageActuatorMonitorService;
    @RequestMapping("/toDataMonitor")
    public String toDataMonitor(){


        return "/backstage/dataMonitor";
    }


    @ResponseBody // sql执行次数统计
    @RequestMapping(value = "/sqlStats",method = RequestMethod.POST)
    public Map<String,String> sqlStats(){

        return backstageActuatorMonitorService.sqlMonitor();
    }

    @ResponseBody // 表查询次数统计
    @RequestMapping(value = "/tableStats",method = RequestMethod.POST)
    public Map<String,String> tableStats(){

        Map<String, String> stringStringMap = backstageActuatorMonitorService.tableVisitCount();
        System.out.println(stringStringMap);
        return stringStringMap;
    }



}

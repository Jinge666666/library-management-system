package com.jing.librarymanagementsystem.controller.backstage;

import com.jing.librarymanagementsystem.service.BackstageActuatorMonitorService;
import com.jing.librarymanagementsystem.service.BackstageRedisMonitorService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

// 服务监控
@Controller
@RequestMapping("/backstage")
public class ServiceMonitorController {

    @Autowired
    BackstageActuatorMonitorService backstageMonitorService;
    @Autowired
    BackstageRedisMonitorService backstageRedisMonitorService;

    @RequestMapping("/serviceMonitor")
    public String toServiceMonitor(){

        return "/backstage/serviceMonitor";
    }

    @ResponseBody
    @RequestMapping(value = "/serviceCPUMonitor",method = RequestMethod.POST)
    public String getServiceCupData(){

        try {
            // 得到系统内存信息
            Map<String, String> serverBufferInfo = backstageMonitorService.getServerMemoryInfo();
            System.out.println(serverBufferInfo);
            // 得到cpu信息
            Map<String, String> serverCupInfo = backstageMonitorService.getServerCupInfo();
            System.out.println(serverCupInfo);
            // 得到磁盘信息
            Map<String, String> diskInfo = backstageMonitorService.getDiskInfo();
            System.out.println(diskInfo);

            // 统计用户在线个数和访问量
            Map<String, String> activeAndVisitStats = backstageMonitorService.userActiveAndVisitStats();
            System.out.println(activeAndVisitStats);
            // sql频率统计
//            List<Map<String, String>> sqlCountStats = backstageMonitorService.sqlMonitor();
//            System.out.println(sqlCountStats);
            // table select频率统计
//            Map<String, String> tableSelectCount = backstageMonitorService.tableVisitCount();
//            System.out.println(tableSelectCount);


            System.out.println("===========================redis信息==========================================");
            backstageMonitorService.redisMemoryInfo();
        }catch (Exception e){
            e.printStackTrace();
        }
        return "haha";
    }

    // 获取服务器基本信息，ip、运行操作系统、系统架构、服务器名
    @ResponseBody
    @RequestMapping("/serverAndJvmConfigInfo")
    public Map<String,String>getServerConfigInfo() throws UnknownHostException {
        // 得到服务器和jvm信息
        return backstageMonitorService.getServerJvmInfo();
    }

    // jvm内存消耗
    @ResponseBody
    @RequestMapping("/jvmMemory")
    public Map<String,String> jvmMemory(){

        return backstageMonitorService.getServerMemoryInfo();
    }

    // cpu信息
    @ResponseBody
    @RequestMapping("/serverCpuInfo")
    public Map<String,String> cpuInfo(){

        return  backstageMonitorService.getServerCupInfo();
    }

    // 磁盘使用情况
    @ResponseBody
    @RequestMapping("/serverDiskInfo")
    public Map<String,String> serverDiskInfo(){

        return  backstageMonitorService.getDiskInfo();
    }
}

package com.jing.librarymanagementsystem.service;

import org.springframework.stereotype.Service;

import javax.print.DocFlavor;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

/**
 * 后台监控调用数据的service，负责请求druid或actuator拿到数据清洗后返回给前端
 * **/
@Service
public interface BackstageActuatorMonitorService {


    /** 服务监控----------------------------------------------------------------------------------------**/
    // 服务器和jvm信息
    public Map<String,String> getServerJvmInfo() throws UnknownHostException;
    // cpu
    public Map<String,String>getServerCupInfo();
    // 内存
    public Map<String,String>getServerMemoryInfo();
//    // java虚拟机信息
//    public Map<String,String>getServerJvmInfo();
    public Map<String,String>getDiskInfo();










    /** redis缓存监控----------------------------------------------------------------------------------------**/
    public Map<String,Object>redisMemoryInfo();




    /** mysql监控----------------------------------------------------------------------------------------**/
    // 统计sql执行频率柱状图
    // 统计表的查询频率柱状图

    // 德鲁伊sql监控
    public Map<String,String> sqlMonitor();
    // 德鲁伊 防火墙 表访问统计
    public Map<String,String> tableVisitCount();



    /** 资源统计，如ip频率、访问量等---------------------------------------------------------------------    **/
    // 统计在线用户
    public  Map<String, String> userActiveAndVisitStats();

    // 在线量与访问量、借阅 redis已实现

    // 统计request访问随时间变化量的峰值变化
    // 用户ip频率记录折线图，峰值



}

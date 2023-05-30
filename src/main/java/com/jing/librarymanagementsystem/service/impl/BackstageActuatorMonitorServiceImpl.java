package com.jing.librarymanagementsystem.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jing.librarymanagementsystem.bean.ServletLogBean;
import com.jing.librarymanagementsystem.mappers.ServletLogInfoMapper;
import com.jing.librarymanagementsystem.service.BackstageActuatorMonitorService;
import com.jing.librarymanagementsystem.service.BackstageRedisMonitorService;
import com.jing.librarymanagementsystem.util.HttpClientUtil;
import com.jing.librarymanagementsystem.util.UtilMethods;
import org.springframework.stereotype.Service;



import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class BackstageActuatorMonitorServiceImpl implements BackstageActuatorMonitorService {

    @Resource
    HttpClientUtil httpClientUtil;
    @Resource
    UtilMethods utilMethods;
    @Resource
    BackstageRedisMonitorService backstageRedisMonitorService;



    /** 服务监控----------------------------------------------------------------------------------------**/
    // 服务器和jvm基本信息
    @Override
    public Map<String,String> getServerJvmInfo() throws UnknownHostException {

        HashMap<String, String> result = new HashMap<>();
        // 获取服务器ip
        InetAddress localHost = InetAddress.getLocalHost();
        result.put("ip",localHost.getHostAddress());
        // 服务器名
        result.put("hostName",localHost.getHostName());
        // 访问actuator/env拿到操作系统名、系统架构、jvm信息
        String serverUrl="http://localhost:8888/actuator/env";
        JSONObject osInfo = httpClientUtil.sendGetRequest(serverUrl);
        /**
         * JSON对象的取值操作：
         * 1，取[],用getJSONARRAY,取数组某一index的对象，在getJSONARRAY基础上使用getJSONObject(0)接受数字下标
         * 2，取{}，用getJSONObject(),参数是字符串，不是下标了
         * 3，取属性，就用get
         * 4,注意浏览器展示有0时，应该是JSONARRAY，不是JSONObject
         * */
        result.put("port",osInfo.getJSONArray("propertySources").getJSONObject(0).getJSONObject("properties").getJSONObject("local.server.port").get("value").toString());

        // 操作系统名字
        result.put("osName",osInfo.getJSONArray("propertySources").getJSONObject(2).getJSONObject("properties").getJSONObject("os.name").get("value").toString());
        // 系统架构
        result.put("osArch",osInfo.getJSONArray("propertySources").getJSONObject(2).getJSONObject("properties").getJSONObject("os.arch").get("value").toString());


        // java名字
        result.put("jvmName",osInfo.getJSONArray("propertySources").getJSONObject(2).getJSONObject("properties").getJSONObject("java.vm.name").get("value").toString());
        // java版本
        result.put("jvmVersion",osInfo.getJSONArray("propertySources").getJSONObject(2).getJSONObject("properties").getJSONObject("java.runtime.version").get("value").toString());
        // 项目绝对路径
        result.put("projectDir",osInfo.getJSONArray("propertySources").getJSONObject(2).getJSONObject("properties").getJSONObject("user.dir").get("value").toString());
        // java安装路径
        result.put("jvmHome",osInfo.getJSONArray("propertySources").getJSONObject(2).getJSONObject("properties").getJSONObject("java.home").get("value").toString());
        // jvm运行时长
        String metricsUrl1="http://localhost:8888/actuator/metrics/process.uptime";
        JSONObject  jvmStartTime= httpClientUtil.sendGetRequest(metricsUrl1);
        String jvmUpTime=jvmStartTime.getJSONArray("measurements").getJSONObject(0).get("value").toString();
        int time = Integer.parseInt(jvmUpTime.split("\\.")[0]);
        String upTime = utilMethods.secondStrConverter(time);
        result.put("jvmUpTime",upTime);
        return result;
    }
    // 获取cpu信息
    @Override
    public Map<String, String> getServerCupInfo() {

        HashMap<String, String> result = new HashMap<>();
        // cpu总数量
        String cpuNumUrl="http://localhost:8888/actuator/metrics/system.cpu.count";
        JSONObject cpuNumJson = httpClientUtil.sendGetRequest(cpuNumUrl);
        result.put("cpuNum",cpuNumJson.getJSONArray("measurements").getJSONObject(0).get("value").toString());

        // 系统cpu使用量
        String cpuUsedUrl = "http://localhost:8888/actuator/metrics/system.cpu.usage";
        JSONObject cpuUsedJson = httpClientUtil.sendGetRequest(cpuUsedUrl);
        result.put("osCpuUsed",cpuUsedJson.getJSONArray("measurements").getJSONObject(0).get("value").toString());

        // 进程cpu使用量
        String ProcessCpuUrl = "http://localhost:8888/actuator/metrics/process.cpu.usage";
        JSONObject  ProcessCpuJson = httpClientUtil.sendGetRequest(ProcessCpuUrl);
        result.put("processCpuUsed",ProcessCpuJson.getJSONArray("measurements").getJSONObject(0).get("value").toString());
        return result;
    }

    // jvm内存信息 指针饼状图
    @Override
    public Map<String, String> getServerMemoryInfo() {

        // 单位都是byte
        HashMap<String, String> result = new HashMap<String, String>();
        // JVM内存当前使用量
        String jvmMemoryUsedUrl="http://localhost:8888/actuator/metrics/jvm.memory.used";
        JSONObject jvmMemoryUsedJson = httpClientUtil.sendGetRequest(jvmMemoryUsedUrl);
        result.put("jvmMemoryUsed",utilMethods.parseEStr(jvmMemoryUsedJson.getJSONArray("measurements").getJSONObject(0).get("value").toString()));
        // JVM分配的内存总量
        String jvmMemoryTotalUrl="http://localhost:8888/actuator/metrics/jvm.memory.committed";
        JSONObject jvmMemoryTotalJson = httpClientUtil.sendGetRequest(jvmMemoryTotalUrl);
        result.put("jvmMemoryTotal",utilMethods.parseEStr(jvmMemoryTotalJson.getJSONArray("measurements").getJSONObject(0).get("value").toString()));
        return result;
    }

    // 磁盘信息
    @Override
    public Map<String, String> getDiskInfo() {

        HashMap<String, String> result = new HashMap<String, String>();
        String diskFreeUrl="http://localhost:8888/actuator/metrics/disk.free";
        JSONObject diskFreeJson = httpClientUtil.sendGetRequest(diskFreeUrl);
        result.put("diskFree",utilMethods.parseEStr(diskFreeJson.getJSONArray("measurements").getJSONObject(0).get("value").toString()));
        String diskTotalUrl="http://localhost:8888/actuator/metrics/disk.total";
        JSONObject diskTotalJson = httpClientUtil.sendGetRequest(diskTotalUrl);
        result.put("diskTotal",utilMethods.parseEStr(diskTotalJson.getJSONArray("measurements").getJSONObject(0).get("value").toString()));
        return result;
    }

    @Override // redis配置、命令统计、内存信息
    public Map<String, Object> redisMemoryInfo() {

        // 得到redis服务信息
        Map<String, Properties> redisServerInfo = backstageRedisMonitorService.getRedisServerInfo();
        Map<String, Object> result = new HashMap<>();
        result.put("redisVersion",redisServerInfo.get("server").getProperty("redis_version"));
        result.put("osName",redisServerInfo.get("server").getProperty("os"));
        result.put("redisMode",redisServerInfo.get("server").getProperty("redis_mode"));
        result.put("arch",redisServerInfo.get("server").getProperty("arch_bits"));
        result.put("port",redisServerInfo.get("server").getProperty("tcp_port"));
        result.put("uptime",utilMethods.secondStrConverter(Integer.parseInt(redisServerInfo.get("server").getProperty("uptime_in_seconds"))));
        result.put("rdbState",redisServerInfo.get("persistence").getProperty("rdb_last_bgsave_status"));
        result.put("aofState",redisServerInfo.get("persistence").getProperty("aof_enabled"));
        result.put("instantaneousInput",redisServerInfo.get("stats").getProperty("instantaneous_input_kbps"));
        result.put("instantaneousOutput",redisServerInfo.get("stats").getProperty("instantaneous_output_kbps"));
        result.put("customsNum",redisServerInfo.get("customs").getProperty("connected_clients"));

        //缓存信息
        result.put("rssMemory",redisServerInfo.get("memory").getProperty("used_memory_rss"));
        result.put("peakMemory",redisServerInfo.get("memory").getProperty("used_memory_peak"));
        result.put("peakPercMemory",redisServerInfo.get("memory").getProperty("used_memory_peak_perc"));
        result.put("sysMemory",redisServerInfo.get("memory").getProperty("total_system_memory"));

//        System.out.println("================================命令统计================================");
        /**
         * calls: 次数
         * usec: 总时间
         * usec_per_call:平均时间
         * */
        ArrayList<Map<String, String>> commands = new ArrayList<>();
        Properties commandstats = redisServerInfo.get("commandstats");
        Set<String> commandKeys = commandstats.stringPropertyNames();
        for (String commandKey : commandKeys) {
            Map<String, String> command = new HashMap<>();
            String property = commandstats.getProperty(commandKey);
            command.put("name",commandKey.split("_")[1]);
            command.put("value",property.split(",")[0].split("=")[1]);
            commands.add(command);

        }
        result.put("commands",commands);
//        System.out.println("=======================服务器信息=======================--");
//        System.out.println("redis版本:"+redisServerInfo.get("server").getProperty("redis_version"));
//        System.out.println("redis服务器宿主操作系统："+redisServerInfo.get("server").getProperty("os"));
//        System.out.println("运行模式"+redisServerInfo.get("server").getProperty("redis_mode"));
//        System.out.println("架构"+redisServerInfo.get("server").getProperty("arch_bits"));
//        System.out.println("客户端口"+redisServerInfo.get("server").getProperty("tcp_port"));
//        System.out.println("运行时长，单位秒"+redisServerInfo.get("server").getProperty("uptime_in_seconds"));
//        System.out.println("运行时长，单位天"+redisServerInfo.get("server").getProperty("uptime_in_days"));
//
//        System.out.println("=======================缓存信息=======================--");
//        System.out.println("系统分配redis内存,字节"+redisServerInfo.get("memory").getProperty("used_memory_rss"));
//        System.out.println("redis内存消耗峰值，字节"+redisServerInfo.get("memory").getProperty("used_memory_peak"));
//        System.out.println("使用内存达到峰值内存的百分比"+redisServerInfo.get("memory").getProperty("used_memory_peak_perc"));
//        System.out.println("整个系统内存"+redisServerInfo.get("memory").getProperty("total_system_memory"));
//
//        System.out.println("==================RDB 持久化和 AOF 持久化有关信息============================");
//        System.out.println("最近一次创建RDB成功与否"+redisServerInfo.get("persistence").getProperty("rdb_last_bgsave_status"));
//        System.out.println("AOF是否打开"+redisServerInfo.get("persistence").getProperty("aof_enabled"));
//
//        System.out.println("================================网络出入口================================");
//        System.out.println("网络入口速率"+redisServerInfo.get("stats").getProperty("instantaneous_input_kbps"));
//        System.out.println("网络出口速率"+redisServerInfo.get("stats").getProperty("instantaneous_output_kbps"));


        return result;
    }


    /** mysql监控----------------------------------------------------------------------------------------**/


    @Override // sql执行次数统计
    public Map<String,String> sqlMonitor() {
        String url = "http://localhost:8888/druid/sql.json?orderBy=ExecuteCount&orderType=desc&page=1&perPageCount=1000000";
        JSONObject jsonObject = httpClientUtil.sendGetRequest(url);
        JSONArray contents = jsonObject.getJSONArray("Content");
//        List<Map<String, String>> result = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < contents.size(); i++) {
            String sql=contents.getJSONObject(i).get("SQL").toString();
            String ExecuteCount = contents.getJSONObject(i).get("ExecuteCount").toString();
            map.put(sql,ExecuteCount);
//            map.put("statement",sql);
//            map.put("executeCount",ExecuteCount);
        }

        return map;
    }

    @Override // 每个表表select执行次数统计
    public Map<String, String> tableVisitCount() {

        String url="http://localhost:8888/druid/wall.json";
        JSONObject jsonObject = httpClientUtil.sendGetRequest(url);
        JSONArray jsonArray = jsonObject.getJSONObject("Content").getJSONArray("tables");
        Map<String, String> data = new HashMap<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            data.put(jsonArray.getJSONObject(i).get("name").toString(),jsonArray.getJSONObject(i).get("selectCount").toString());
        }

        return data;
    }









    /** 资源统计，如ip频率、访问量等---------------------------------------------------------------------    **/

    @Override // 统计在线用户和访客量
    public Map<String, String> userActiveAndVisitStats() {

        HashMap<String, String> result = new HashMap<>();
        // 利用当前活跃的session数统计用户在线数量
        String url1="http://localhost:8888/actuator/metrics/tomcat.sessions.active.current";
        JSONObject activeUserJson = httpClientUtil.sendGetRequest(url1);
        result.put("userActiveNum",activeUserJson.getJSONArray("measurements").getJSONObject(0).get("value").toString().split("\\.")[0]);
        // 利用session总共注册个数统计访客量
        String url2="http://localhost:8888/actuator/metrics/tomcat.sessions.created";
        JSONObject userVisitJson = httpClientUtil.sendGetRequest(url2);
        result.put("userVisitNum",userVisitJson .getJSONArray("measurements").getJSONObject(0).get("value").toString().split("\\.")[0]);


        return result;
    }


}

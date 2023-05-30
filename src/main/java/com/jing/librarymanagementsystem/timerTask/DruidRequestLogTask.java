package com.jing.librarymanagementsystem.timerTask;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jing.librarymanagementsystem.bean.ServletLogBean;
import com.jing.librarymanagementsystem.mappers.ServletLogInfoMapper;
import com.jing.librarymanagementsystem.util.HttpClientUtil;
import com.jing.librarymanagementsystem.util.ManageSpringBeans;
import com.jing.librarymanagementsystem.util.UtilMethods;
import com.sun.xml.internal.ws.handler.ServerLogicalHandlerTube;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class DruidRequestLogTask extends TimerTask {

//    线程类中无法使用注入
    @Resource
    UtilMethods utilMethods;
    @Resource
    HttpClientUtil httpClientUtil;
    @Resource
    ServletLogInfoMapper servletLogInfoMapper;

    @Override
    public void run() {

//        UtilMethods  utilMethods = (UtilMethods) ManageSpringBeans.getBeans(UtilMethods.class);
//        HttpClientUtil httpClientUtil = (HttpClientUtil) ManageSpringBeans.getBeans(HttpClientUtil.class);
//        ServletLogInfoMapper servletLogInfoMapper = (ServletLogInfoMapper) ManageSpringBeans.getBeans(ServletLogInfoMapper.class);

        // 对德鲁伊获取监控数据
        // 得到请求累计总量
        JSONObject jsonObject = httpClientUtil.sendGetRequest("http://localhost:8888/druid/webapp.json");

        int requestCount = (int) jsonObject.getJSONArray("Content").getJSONObject(0).get("RequestCount");
        // 得到近五分钟内请求增长最高的前三名session和ip等信息
        JSONObject jsonObject1 = httpClientUtil.sendPostRequest("http://localhost:8888/druid/websession.json?orderBy=LastAccessTime&orderType=desc&page=1&perPageCount=1000000");
        JSONArray content = jsonObject1.getJSONArray("Content");

        // 获取当前时间
        Calendar c = Calendar.getInstance();
        String currentTime2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(c.getTime());
        // 当前时间减五分钟
        c.add(Calendar.MINUTE, -5);
        String currentTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(c.getTime());
        // 将大于当前时间减5分钟的记录全部筛选出来
        // 利用treeMap自带的排序，默认按key升序排序
        TreeMap<String, Map<String,String>> result = new TreeMap<>();
        for (int i = 0; i < content.size(); i++) {
            String t1 = (String) content.getJSONObject(i).get("LastAccessTime");
            if(currentTime.compareTo(t1)<0){
                Map<String, String> log = new HashMap<>();
                log.put("RemoteAddress",content.getJSONObject(i).getString("RemoteAddress"));
                log.put("RequestCount",content.getJSONObject(i).getString("RequestCount"));
                log.put("SESSIONID",content.getJSONObject(i).getString("SESSIONID"));
                result.put(log.get("RequestCount"),log);  // 根据请求数排序
            }
        }
        System.out.println("================================================================");
        System.out.println(result.toString());
        StringBuffer ips = new StringBuffer();
        StringBuffer requestCounts = new StringBuffer();
        StringBuffer sessionIds = new StringBuffer();
        int size = result.entrySet().size();
        if(size>3){
            int num = 0;
            for (Map.Entry<String, Map<String, String>> stringMapEntry : result.entrySet()) {
                num++;
                if(3>size-num){
                    Map<String, String> value = stringMapEntry.getValue();
                    ips.insert(0,value.get("RemoteAddress")+",");
                    requestCounts.insert(0,value.get("RequestCount")+",");
                    sessionIds.insert(0,value.get("SESSIONID")+",");
                }
            }
        }else {
            for (Map.Entry<String, Map<String, String>> stringMapEntry : result.entrySet()) {
                Map<String, String> value = stringMapEntry.getValue();
                ips.insert(0,value.get("RemoteAddress")+",");
                requestCounts.insert(0,value.get("RequestCount")+",");
                sessionIds.insert(0,value.get("SESSIONID")+",");
            }
        }

        ServletLogBean servletLogBean = new ServletLogBean();
        servletLogBean.setIps(ips.toString());
        servletLogBean.setRequestNums(requestCounts.toString());
        servletLogBean.setSessionids(sessionIds.toString());
        servletLogBean.setEndTime(currentTime2);
        servletLogBean.setTotal(String.valueOf(requestCount));
        // 调用数据库获取最近一条记录的请求总数
        String currentRequestCount = servletLogInfoMapper.selectCurrentLog();
        String increase="0";
        if(currentRequestCount==null){ // 第一条前没值
            increase=String.valueOf(requestCount);
        }else {
            // 相减得到增长值
            increase = String.valueOf(requestCount-Integer.parseInt(currentRequestCount));
        }
        servletLogBean.setIncrease(increase);
        // 存数据库
        servletLogInfoMapper.saveLog(servletLogBean);

    }
}

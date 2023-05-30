package com.jing.librarymanagementsystem.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/** 自定义工具类，向其他API接口发送Http请求并接收返回结果，得到druid或actuator的监控信息*/
@Component
public class HttpClientUtil {

    /**
     * 向目的URL发送post请求
//     * @param params
     * @param url       目的url
     * @return  AdToutiaoJsonTokenData
     *
     *
     * ----------------------------------------------------------------
     * actuator指标url
     * http://localhost:8888/actuator/metrics/disk.total      磁盘总量
     * http://localhost:8888/actuator/metrics/disk.free       磁盘空闲量
     *
     * http://localhost:8888/actuator/health  直接有磁盘总量和空闲 且有reids版本信息
     *
     * http://localhost:8888/actuator/metrics/http.server.requests    请求总量的时长 还有请求次数统计
     *
     *
     * http://localhost:8888/actuator/metrics/jvm.buffer.memory.used 内存使用量
     * http://localhost:8888/actuator/metrics/jvm.buffer.total.capacity 内存总量
     *
     * http://localhost:8888/actuator/metrics/system.cpu.count    cpu个数
     * http://localhost:8888/actuator/metrics/system.cpu.usage   系统cpu使用量
     * http://localhost:8888/actuator/metrics/process.cpu.usage  进程（用户）cpu使用量
     *
     * http://localhost:8888/actuator/metrics/tomcat.sessions.active.current 该接口是统计项目session数
     * 借此可以统计用户在线个数
     *
     * http://localhost:8888/actuator/metrics/tomcat.sessions.created session创建个数，可统计网站用户访问量
     *
     * http://localhost:8888/actuator/metrics/process.uptime Java 虚拟机的正常运行时长
     * http://localhost:8888/actuator/metrics/process.start.time   自 unix 纪元以来进程的开始时间
     * 							可做jvm启动时间
     * http://localhost:8888/actuator/metrics/jvm.memory.committed  承诺给jvm使用的总内存量
     * http://localhost:8888/actuator/metrics/jvm.memory.max 可用于内存管理的最大内存量（以字节为单位） 包含上面的
     *
     * http://localhost:8888/actuator/metrics/jvm.memory.used  jvm已用内存量 结合上面第二条可得到使用率
     *
     *
     * http://localhost:8888/actuator/env  有jvm名称 版本 项目路径  安装路径 还有redis的host等信息
     * 操作系统版本、架构
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     */
    public JSONObject sendPostRequest(String url){
        RestTemplate client = new RestTemplate();
        //新建Http头，add方法可以添加参数
        HttpHeaders headers = new HttpHeaders();
        Session session = SecurityUtils.getSubject().getSession();
        headers.set("Cookie", "JSESSIONID="+session.getId());
        //设置请求发送方式
        HttpMethod method = HttpMethod.POST;
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //将请求头部和参数合成一个请求
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(headers);
//        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(headers);
        //执行HTTP请求，将返回的结构使用String 类格式化（可设置为对应返回值格式的类）
        ResponseEntity<String> response = client.exchange(url, method, requestEntity,String .class);
        return JSONObject.parseObject(response.getBody());
    }

    /**
     * 向目的URL发送get请求
     * @param url       目的url
//     * @param params    发送的参数
//     * @param headers   发送的http头，可在外部设置好参数后传入
     * @return  String  MultiValueMap<String, String> params
     */
    public JSONObject sendGetRequest(String url){
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        Session session = SecurityUtils.getSubject().getSession();
        headers.set("Cookie", "JSESSIONID="+session.getId());
        HttpMethod method = HttpMethod.GET;
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //将请求头部和参数合成一个请求
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(headers);
        //执行HTTP请求，将返回的结构使用String 类格式化 最后一个参数可指定返回类型
        ResponseEntity<String> response = client.exchange(url, method, requestEntity, String.class);
        // 使用fastjson将字符串转为JSONobject,也可使用TypeReference将JSONobject转为指定类型
//        Map<String, Object> result = JSONObject.parseObject(response.getBody(), new TypeReference<Map<String, Object>>(){});
        // 使用json类型取值最合适，其他转化反而更复杂
        JSONObject result = JSONObject.parseObject(response.getBody());
        return result;
    }
}


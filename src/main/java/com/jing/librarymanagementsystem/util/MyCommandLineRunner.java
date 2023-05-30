package com.jing.librarymanagementsystem.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jing.librarymanagementsystem.bean.ServletLogBean;
import com.jing.librarymanagementsystem.mappers.ServletLogInfoMapper;
import com.jing.librarymanagementsystem.timerTask.DruidRequestLogTask;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 利用springboot一些启动过程，初始化一些操作，比如定时任务
 * 应用启动做一个一次性事情
 */
@Order(2)
@Component//放入容器
public class MyCommandLineRunner implements CommandLineRunner {

    @Resource
    DruidRequestLogTask druidRequestLogTask;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("MyCommandLineRunner....run....");
        // 设置定时5分钟获取一次druid日志
        // 实例化task
//        DruidRequestLogTask druidRequestLogTask = new DruidRequestLogTask();
        // 绑定任务，延迟五分钟执行，周期五分钟
//        Timer timer = new Timer();
//        timer.schedule(druidRequestLogTask,1000*60*2L,1000*60*5L);


    }
}
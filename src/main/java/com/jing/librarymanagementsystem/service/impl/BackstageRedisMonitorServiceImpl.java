package com.jing.librarymanagementsystem.service.impl;

import com.jing.librarymanagementsystem.service.BackstageRedisMonitorService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 获取redis指标信息等
 * */
@Service
public class BackstageRedisMonitorServiceImpl implements BackstageRedisMonitorService {

    @Resource
    StringRedisTemplate stringRedisTemplate;

//    @Resource
//    RedisTemplate redisTemplate;

    @Resource  // 连接工厂
    RedisConnectionFactory redisConnectionFactory;

    @Override
    public Map<String, Properties> getRedisServerInfo() {

        HashMap<String, Properties> info = new HashMap<>();
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        // stringRedisTemplate.getConnectionFactory()==redisConnectionFactory
        Session session = SecurityUtils.getSubject().getSession();
        RedisConnection connection;
        // 必须复用redis客户端，否则达到设置的连接上限后页面会卡住
        if(session.getAttribute("redisConnection")==null) {
            connection = redisConnectionFactory.getConnection();
            session.setAttribute("redisConnection",connection);
        }else {
            connection= (RedisConnection) session.getAttribute("redisConnection");
        }

        //缓存信息
        Properties memory = connection.info("memory");
        //服务信息
        Properties server = connection.info("server");
        // RDB和AOF相关状态
        Properties persistence = connection.info("persistence");
        // cpu状态
        Properties cpu = connection.info("cpu");
        // redis命令统计
        Properties commandstats = connection.info("commandstats");
        // 通用统计数据 有网络入口出口
        Properties stats = connection.info("stats");
        // 客户端信息
        Properties customs = connection.info("Clients");

        info.put("memory", memory);
        info.put("server", server);
        info.put("persistence", persistence);
        info.put("commandstats", commandstats);
        info.put("cpu", cpu);
        info.put("stats",stats);
        info.put("customs",customs);
        return info;
    }
}

package com.jing.librarymanagementsystem.listener;


import com.jing.librarymanagementsystem.bean.User;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * 自定义的session监听器可统计用户在线个数
 * 德鲁伊就是基于此实现的，在德鲁伊的web应用界面直接有session数，
 * 该方法登录页面触发，刷新登录页不会再次触发，只有重新打开浏览器到登录页才会触发
 * 注意该监听器内的执行时机，StringRedisTemplate还来不及初始化，使用其会触发空指针异常，因此这里不适合连接redis
 */
@WebListener
public class SessionListener implements HttpSessionListener {

    @Resource(name = "stringRedisTemplate")
    StringRedisTemplate stringRedisTemplate;

    public static long onlineUserCount = 0;//在线人数

    @Override
    public void sessionCreated(HttpSessionEvent se) {
//        System.out.println("创建----------------------------------");
        // 访客，来到登录页会触发这里，登录后不会再次触发该方法
//        if(!stringRedisTemplate.hasKey("visitorNum")){
//            stringRedisTemplate.opsForValue().set("visitorNum","0");
//        }
//        stringRedisTemplate.opsForValue().increment("visitorNum"); // 每次触发登录，访客+1
//        onlineUserCount++;
        // 由于这里得不到userId，因此存sessionid不能在此做。

    }

    @Override // session销毁
    public void sessionDestroyed(HttpSessionEvent se) {
//        System.out.println("销毁-----------------------------");
//       onlineUserCount=onlineUserCount-1;

    }
}

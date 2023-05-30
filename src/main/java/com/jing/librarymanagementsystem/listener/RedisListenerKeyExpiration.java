package com.jing.librarymanagementsystem.listener;

import com.jing.librarymanagementsystem.bean.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.annotation.WebListener;

/**
 * 自定义监听rediskey失效事件，前提要在RedisConfig配置相关内容
 *基于此，我们实现一个账号只能有一个用户登录，若后来人登录，会把前面人踢下去，让其账号
 * 立马下线
 *
 */

@Component
public class RedisListenerKeyExpiration  extends KeyExpirationEventMessageListener {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    public RedisListenerKeyExpiration(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }
    //此处message就是失效key的值
    @Override
    public void onMessage(Message message, byte[] pattern) {
        System.out.println("haa------------------------------");
        /**
         * 因为key的类型是"user:"+userId:+sessionId,因此在userId相同前提下判断key的sessionId是否和你的sessionId一致，若不是，
         * 那么你就是新的登陆者，若是，则你账号必须得下线，因为若是自然的60分钟销毁的key，那么由于session失效，也会退出登录，
         * 这里主要防是否第二个人同时登录了一个账号情况，因此无论如何都必须得下线
         * */
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        String sessionId = (String) subject.getSession().getId();
        // 1,判断是否是同一个账号
        if(message.toString().contains(user.getUserId())){
            // 2，再鉴别该失效的sessionId是否是你的
            if(message.toString().contains(sessionId)){
                // 那就由于后来者会把前面登录该号的人踢下线的原则，你的账号就得强制下线了
                System.out.println("-==========你的账号已被另一个人登录，你已退出登录================");
                subject.logout();
                subject.getSession().stop();
            }else {
                // 那么你就是新的登录者
                System.out.println("--------------欢迎你，新的登陆者");
            }
        }

    }
}

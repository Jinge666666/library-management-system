package com.jing.librarymanagementsystem.filter;

import com.jing.librarymanagementsystem.bean.User;
import com.jing.librarymanagementsystem.config.UpdateShiroAuthenticationAndAuthorization;
import com.jing.librarymanagementsystem.shiroRealm.UserRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 为解决记住我功能使用后，因为在登陆后有做了很多设置用户上下文的工作，比如设置session等，
 * 如果我们只是设置拦截级别为user，那么再次进入的时候虽然可以访问，但是session是空的，我们的页面必然异常频出。
 * 我们复写登录认证过滤器的isAccessAllowed方法，在该方法将所有系统设置在用户上下文的值在这里再配置一遍。
 *
 *该方法执行时机：来到登录页面就触发了，且刷新登录页会多次执行该方法，因此，只有在记住我的if内的执行是一次的，因此redis存sessioonId
 * 时，可以在记住我这种情况内做
 *
 * // 步骤：
 *  1，继承 FormAuthenticationFilter类
 *  2，在shiroConfig配置该类的bean
 *  3，在shiroConfig，调用该类实例对象放入map，并封装到ShiroFilterFactoryBean实例对象中。
 * */

// OncePerRequestFilter是FormAuthenticationFilter 的父类，而且继承它OncePerRequestFilter，也就是第二种
// 实现方法，也能达到拦截设置上下文的目的。但推荐方式一
public class RememberAuthenticationFilter extends FormAuthenticationFilter {


    @Resource
    UpdateShiroAuthenticationAndAuthorization updateShiroAuthenticationAndAuthorization;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    /**
     * 这个方法决定了是否能让用户登录
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {

        Subject subject = getSubject(request, response);
        //如果 isAuthenticated 为 false 证明不是登录过的，同时 isRememberd 为true 证明是没登陆直接通过记住我功能进来的
        // 这样判断也为了让正常登录不受这里影响。
        if(!subject.isAuthenticated() && subject.isRemembered()){

            //获取session看看是不是空的
            Session session = subject.getSession(true);
            //随便拿session的一个属性来看session当前是否是空的，不为空就不需初始化session
            if(session.getAttribute("ipAddr") == null){

                // 调用刷新subject的方法，内有自动刷新session
                updateShiroAuthenticationAndAuthorization.AuthenticationInfoUpdateUtil();
                // 至此，session在记住我功能下完成了初始化

                /** sessionId存redis，刷新过期时间60分钟，这里只会执行一次
                 * 其余设置点；登录成功处、退出登录处、修改密码处、注销
                 *
                 *stringRedisTemplate.opsForValue().getOperations().getExpire
                 *              * 如果该值有过期时间，就返回相应的过期时间;
                 *              *  * 如果该值没有设置过期时间，就返回-1;
                 *              *  * 如果没有该值(已过期自动删除了)，就返回-2;
                 * */

                User user = (User) subject.getPrincipal();
                String userId = user.getUserId();
                String sessionId = (String) session.getId();
//                 set方法对旧的key也会覆盖，不用删除旧值
                /**
                 * 这样不好做只能有一个账号一个人登录的功能，我们将sessionoId也加到key中，那么
                 * 若key中含有userId的key，则一定属于你强制登录了另一个人的账号，若想让那个人账号下线，必须得删除它的key，让其监听到
                 * */
                // 先删除前登陆者key（key不存在不会报错）
                Set<String> sessionKeys = stringRedisTemplate.keys("user:" + userId + "*");
                for (String  sessionKey: sessionKeys) {
                    // 因为怕多人同时登录一个账号，反正前面的账号对应的key都得删除
                    stringRedisTemplate.delete(sessionKey);
                }
                // 然后再写入key
                stringRedisTemplate.opsForValue().set("user:"+userId+":"+sessionId, sessionId, 60, TimeUnit.MINUTES);
                // 统计访问量
                stringRedisTemplate.opsForValue().increment("visitorNum");

            }
        }

        System.out.println("是否认证："+subject.isAuthenticated());
        System.out.println("是否记住我"+subject.isRemembered());

        //这个方法本来只返回 subject.isAuthenticated() 现在我们加上 subject.isRemembered() 让它同时也兼容remember这种情况
        return subject.isAuthenticated() || subject.isRemembered();
    }

}

package com.jing.librarymanagementsystem.filter;

import com.jing.librarymanagementsystem.bean.User;
import com.jing.librarymanagementsystem.config.UpdateShiroAuthenticationAndAuthorization;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.servlet.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**记住我功能初始化上下文的第二种，继承OncePerRequestFilter类来实现
 * 和第一种对比，你发现不同的是这里的方法会重复执行，虽然代码内判断了ipAdress不为空，
 * 而第一种在总的过程中只会访问一次，因此，建议使用第一种。
 * */
public class RememberAuthenticationFilter2 extends OncePerRequestFilter {


    @Resource
    UpdateShiroAuthenticationAndAuthorization updateShiroAuthenticationAndAuthorization;

    @Override
    protected void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {

        Subject subject = SecurityUtils.getSubject();
        //如果 isAuthenticated 为 false 证明不是登录过的，同时 isRememberd 为true 证明是没登陆直接通过记住我功能进来的
        // 这样判断也为了让正常登录不受这里影响。
        if(!subject.isAuthenticated() && subject.isRemembered()){

            User user = (User) subject.getPrincipal();
            System.out.println("haaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            //获取session看看是不是空的
            Session session = subject.getSession(true);
            //随便拿session的一个属性来看session当前是否是空的，不为空就不需初始化session
            if(session.getAttribute("ipAddr") == null){

                // 调用刷新subject的方法，内有自动刷新session
                updateShiroAuthenticationAndAuthorization.AuthenticationInfoUpdateUtil();
                // 至此，session在记住我功能下完成了初始化
            }
        }
        chain.doFilter(request,response);
    }
}

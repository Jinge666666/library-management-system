package com.jing.librarymanagementsystem.config;

import com.jing.librarymanagementsystem.filter.RememberAuthenticationFilter;
import com.jing.librarymanagementsystem.filter.RememberAuthenticationFilter2;
import com.jing.librarymanagementsystem.filter.ShiroPermsOrFilter;
import com.jing.librarymanagementsystem.filter.ShiroRolesOrFilter;
import com.jing.librarymanagementsystem.shiroAndRedis.RedisCacheMananger;
import com.jing.librarymanagementsystem.shiroRealm.UserRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authz.RolesAuthorizationFilter;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.servlet.Filter;
import java.awt.image.ImageFilter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    // 拦截配置
    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("getDefaultWebSecurityManager") DefaultWebSecurityManager defaultWebSecurityManager) {

        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        /** 设置安全管理器 */
        bean.setSecurityManager(defaultWebSecurityManager);

        /** 添加Shiro内置过滤器
         *
         *  Shiro内置过滤器，可以实现权限相关的拦截器
         *  注意！！！ 有顺序要求
         *      常见的过滤器：
         *          anon: 无需认证（登录）就可以访问
         *          authc: 必须登录认证才可以访问
         *          user: 如果使用remember功能可以直接访问
         *          perms: 该资源必须获得资源权限才可以访问 多个权限时，以逗号分割，默认以“并”的逻辑关系
         *          roles: 该资源必须获得角色权限才可以访问
         */

        //由于过滤器链是一个链结构，因此我们这里使用linkedHashMap来创建
        /**该过滤器链自顶向下过滤，一旦匹配，则不再执行下面的过滤,如果下面的定义与上面冲突，那按照了谁先定义谁说了算
         * 因此，下面提到了，authc必须放在最后面的原因*/
        Map<String, String> filterMap = new LinkedHashMap<>();
        // 放行静态资源（放行要在认证之前）
        filterMap.put("/js/**","anon");
        filterMap.put("/css/**","anon");
        filterMap.put("/data/**","anon");
        filterMap.put("/images/**","anon");
        filterMap.put("/verifyCode","anon"); // 放行验证码图片
        filterMap.put("/login","anon"); //放行登录验证的这个请求，若被拦截，你登录时提交了还是会跳到登录页。
        filterMap.put("/toRegister","anon"); //放行注册页
        filterMap.put("/register","anon");

        filterMap.put("/actuator","anon");
        filterMap.put("/actuator/**","anon");
        filterMap.put("/druid/**","anon");
        //配置登出,操作logout为shiro提供的一个默认的登出过滤器(会默认跳转到login.html默认页面)
        // 还可以调用subject的logout退出
//        filterMap.put("/toLogin","logout");


        //授权--正常情况下，没有授权会跳转到未授权页面
        // 前台拦截
//         filterMap.put("/home","roles[黄金]");
         filterMap.put("/home","or-roles[青铜,白银,黄金,砖石,王者]");

         // 后台拦截
//        filterMap.put("/backstage/**","roles[超级管理员]");
        filterMap.put("/backstage/**","or-roles[超级管理员,普通管理员]");


        //对没有认证登录（登录）的用户拦截 该行代码必须放在授权的代码下，否则授权的拦截无效！
        /**注意：设置记住我功能后 这里的authc应改为user*/
//        filterMap.put("/**","authc");
        filterMap.put("/**","user");
        // 将拦截规则添加进来
        bean.setFilterChainDefinitionMap(filterMap);

        /** 如果添加了自定义权限拦截器 (注意，有顺序要求，shiro是按顺序加载拦截器的，路径大的最好放在后面)*/
        // 注意，最好不要交给spring容器管理，而是自己new一个。
        ShiroPermsOrFilter shiroPermsOrFilter = new ShiroPermsOrFilter();//自定义拦截器
        Map<String, Filter> myFilterMap = new HashMap<>();
        // 自定义的or-perms,在原来的perms基础上，增加了或的关系，详细语法参见该类文件
        myFilterMap.put("or-perms",shiroPermsOrFilter);//可以配置RoleOrFilter的Bean
        // 自定义的or-roles控制器，该功能和roles对应，是专门支持或关系的。
        ShiroRolesOrFilter shiroRolesOrFilter = new ShiroRolesOrFilter();
        myFilterMap.put("or-roles",shiroRolesOrFilter);

        // 解决记住我功能下session初始化的过滤器放进来
        myFilterMap.put("rememberAuthenticationFilter",rememberAuthenticationFilter());
        //将所有自定义拦截器添加进来
        bean.setFilters(myFilterMap);

        //未认证提示页面
        bean.setLoginUrl("/toLogin");
        // 首页 登录成功的页面跳转交给了前端去完成，这里不用设置
//        bean.setSuccessUrl("/index");
        //未授权提示页面 {该配置无效，已使用全局异常捕获器解决。}
//        bean.setUnauthorizedUrl("/unauthorizedPage");
        return bean;
    }


    // 安全管理器，可以理解为shiro的大脑，控制着整个shiro的功能。
    @Bean
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("userRealm") UserRealm userRealm) {
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();

        // 创建realm使用的hash凭证器
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        // 告诉凭证器加密的类型
        matcher.setHashAlgorithmName("MD5");
        // 设置散列次数（加密次数），不能随便修改，若这里改，那么工具类中给注册用户加密的方法内也要是这个值
        matcher.setHashIterations(10);
        // 注入realm
        userRealm.setCredentialsMatcher(matcher);
        // 给securityMananger设置realm
        defaultWebSecurityManager.setRealm(userRealm);

        /** shiro整合redis */
//         开启缓存
//        userRealm.setCacheManager(new RedisCacheMananger());
//        // 开启全局缓存
//        userRealm.setCachingEnabled(true);
////        // 开启认证缓存,开启后，不会第二次执行UserRealm中的doGetAuthenticationInfo认证方法，而是从缓存中取，下面也一样道理
//        userRealm.setAuthenticationCachingEnabled(true);
//        // 开启授权缓存
//        userRealm.setAuthorizationCachingEnabled(true);

        // 设置授权缓存的名称，设置验证缓存的名称，可以不用设置，默认有
//		realm.setAuthenticationCacheName("authenticationCacheName");
//		realm.setAuthorizationCacheName("authorizationCacheName");


        return defaultWebSecurityManager;
    }
    //1. 创建realm对象，需要自定义类

    @Bean
    public UserRealm userRealm() {

        return new UserRealm();
    }

    /**
     * 开启shiro aop 注解支持，若不开启，则注解方式的权限设置无效
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(
            // 要把你上面自定义的安全管理器bean传进来
            @Qualifier("getDefaultWebSecurityManager")SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        // 拿到上面配置好的安全管理器bean对象
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }


    // 解决rememberMe功能每次重启程序第一次报错问题
    /**
     * 加了rememberMe后，第一次启动程序shiro使用A密钥加密了cookie，第二次启动程序shiro重新生成了密钥B，
     * 当用户访问页面时，shiro会用密钥B去解密上一次用密钥A加密的cookie，导致解密失败
     * 解决办法就是既然每次重启都会重新生成一对密钥，那我们就手动设置一个加解密密钥
     * **/
    @Bean
    public CookieRememberMeManager cookieRememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        simpleCookie.setMaxAge(259200000);
        cookieRememberMeManager.setCookie(simpleCookie);
        cookieRememberMeManager.setCipherKey(Base64.decode("6ZmI6I2j5Y+R5aSn5ZOlAA=="));
        return cookieRememberMeManager;
    }

//    // 将记住我拦截器在config中实例化-----第一种方法
    @Bean
    public RememberAuthenticationFilter rememberAuthenticationFilter() {

        return new RememberAuthenticationFilter();
    }

    // 将记住我拦截器在config中实例化-----第二种方法
//    @Bean
//    public RememberAuthenticationFilter2 rememberAuthenticationFilter2() {
//
//        return new RememberAuthenticationFilter2();
//    }



    /**
     * @desc 方法名字可以随便取
     * @param filter
     * @return
     */
//    @Bean
//    public FilterRegistrationBean<CheckSessionControlFilter> shiroCheckSessionRegistration(CheckSessionControlFilter filter) {
//        FilterRegistrationBean<CheckSessionControlFilter> registration = new FilterRegistrationBean<CheckSessionControlFilter>(filter);
//        registration.setEnabled(false);
//        return registration;
//
//    }

}



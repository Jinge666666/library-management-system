package com.jing.librarymanagementsystem.config;

import org.springframework.context.annotation.Bean;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;


//@EnableWebSecurity // 开启WebSecurity模式
//public class SecurityConfig  {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity.authorizeRequests()
//                .antMatchers("/toLogin").permitAll() // 登录页和注册页不应被拦截
//                .antMatchers("registerPage").permitAll()
//                .anyRequest().authenticated() //代表所有请求，必须认证之后才能访问.注意：放行资源必须放在所有认证请求之前！
//                .and()
//                .formLogin() ;      // 代表开启表单认证
////                .and()
////                .formLogin()
////                .disable()
////                .csrf()
////                .disable();
//        return httpSecurity.build();
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        // 首页所有人可以访问
//        // 其他界面只有对应的角色（权限）才可以访问
//        http.authorizeRequests().antMatchers("loginPage").permitAll()
//                .antMatchers("registerPage").permitAll()
////                .antMatchers("/level1/**").hasRole("vip1")
////                .antMatchers("/level2/**").hasRole("vip2")
////                .antMatchers("/level3/**").hasRole("vip3");
//
//        // 开启自动配置的登录功能
//        // 如果没有权限则跳转到 /login 登录页
//        // /login?error 重定向到这里表示登录失败
//        http.formLogin();
//
//
//    }
//}


package com.jing.librarymanagementsystem.controller;

import com.jing.librarymanagementsystem.bean.User;
import com.jing.librarymanagementsystem.mappers.UserMapper;
import com.jing.librarymanagementsystem.service.BeanService.UserBeanService;
import com.jing.librarymanagementsystem.service.UtilService;
import com.jing.librarymanagementsystem.util.SysResultVo;
import com.jing.librarymanagementsystem.util.UtilMethods;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
// 解决跨域
//@CrossOrigin(originPatterns = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class LoginController {

    @Resource
    UtilService utilService;

    @Resource
    SysResultVo sysResultVo;

    @Resource
    UtilMethods utilMethods;

    @Autowired
    UserBeanService userBeanService;
    @Autowired
    UserMapper userMapper;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    // 登录页面
    @GetMapping(value = {"toLogin","login"})
    public String loginPage(){

        // 判断是否注册登录过
        Subject subject = SecurityUtils.getSubject();
        /**
         * 如果登录成功调用isAuthenticated就会返回true，即已经通过身份验证；
         * 如果isRemembered返回true，表示是通过记住我功能登录的而不是调用login方法登录的。
         * isAuthenticated/isRemembered是互斥的，即如果其中一个返回true，另一个返回false。
         * **/

        if(subject.isAuthenticated()||subject.isRemembered()){
            User user = (User) subject.getPrincipal();
            if(user.getIsAdmin()){
                System.out.println(user.toString());
                System.out.println("haaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                return "redirect:/backstage/main";
            }
            return "redirect:/home";
        }
        return "login";
    }


    // 登录校验 登录后页面跳转逻辑在前端，前端主要根据后端传的sysResultVo数据载体做逻辑跳转。
    @ResponseBody
    @RequestMapping(value = "login",method=RequestMethod.POST)
    public SysResultVo login(@RequestBody User user,HttpServletRequest request) {

        //获取IP 原生的getRemoteAddr()无法获得代理的ip，下面第三方的好点
//        System.out.println(request.getRemoteAddr());
//        String ipAddress =UtilMethods.getIpAddr(request);

        //你可以把Subject当做一个用户对象,Shiro会把认证后的用户信息保存在Subject 中供程序使用
        Subject currentUser = SecurityUtils.getSubject();
        /**shiro的session和httpsession最大区别是不需要依赖http服务器，建议完全使用shiro的session，可以脱离传统web存值取值
         * httpsession和shiro的session内的值是互通的。
         * */
        Session session = currentUser.getSession();
        // 用户身份保存
        if(user.getIsAdmin()){
            session.setAttribute("isAdmin",1);
        }else {
            session.setAttribute("isAdmin",0);
        }
        // 使用如下方式生成token（相当于创建一个用户），其中会自动将密码（表单来的）使用shiro设置的加密方式进行加密。shiro默认是md5加密
        UsernamePasswordToken token = new UsernamePasswordToken(user.getUserName(), user.getPassword());
        //是否记住用户
        if(user.getIsRemember()){
            token.setRememberMe(true);
        }else {
            token.setRememberMe(false);
        }

        // 先校验验证码
        if(!user.getCaptcha().equals(session.getAttribute("verify_code"))){
            return sysResultVo.captchaFail("验证码输入错误！",null);
        }

        try {
            /**  登录 */
            // 得到ip地址,存入session
//            String ip =UtilMethods.getIpAddr(request);
//            Map ipAdress = UtilMethods.getAdressInfo(ip);
//            session.setAttribute("ipAddr",ipAdress.get("pro"));
//            if(ipAdress.get("city")!=null){
//                session.setAttribute("ipAddr",(String)ipAdress.get("pro")+(String)ipAdress.get("city"));
//            }

            session.setAttribute("ipAddr","湖南省");



            // 调用这个方法，shiro匹配的过程是比较存储在上方的AuthenticationToken和userRealm中AuthenticationInfo存储的用户名密码（数据库拿的）
            currentUser.login(token);//账号密码校验，如果没有异常就ok
            // 每登录一次，刷新用户的ip地址，记住我功能不用刷新，也不能刷新。
            userMapper.updateAdress((String) session.getAttribute("ipAddr"));
            // 封装响应数据给前端的ajax 比如用户身份信息，前端可能需要用到（认证时已经存储自己查出的user对象到Subject）
            Map<String, Object> data = new HashMap<>();
            data.put("isAdmin",user.getIsAdmin());

            /**
             * 登录成功才会来到这且执行一次，在这里存sessionId最好不过
             * 已存处：记住我拦截器，还待存处：修改密码、退出登录、注销
             * */

            /**注意可能相同类型转换发生 java.lang.ClassCastException，这是因为devtools惹的祸，
             * 因为刷新程序后，会造成类加载器的不同，最好在session中取use对象
             * */
//            User user1 = (User) SecurityUtils.getSubject().getPrincipal();
            User user1 = (User) session.getAttribute("user");
//            String userId = user1.getUserId();
            String userId = "43224443";
            String sessionId = (String) SecurityUtils.getSubject().getSession().getId();
            System.out.println("哈哈哈哈哈哈哈"+userId);
            // 先删除前登陆者key（key不存在不会报错）
            Set<String> sessionKeys = stringRedisTemplate.keys("user:" + userId + "*");
            for (String  sessionKey: sessionKeys) {
                // 因为怕多人同时登录一个账号，反正前面的账号对应的key都得删除
                stringRedisTemplate.expire(sessionKey,20,TimeUnit.SECONDS);
//                stringRedisTemplate.opsForValue().set(sessionKey,sessionId,60, TimeUnit.MINUTES);
//                stringRedisTemplate.delete(sessionKey);
            }
            // 然后再写入key
            // 设置过期时间60分钟，新的key会覆盖旧的key
            stringRedisTemplate.opsForValue().set("user:"+userId+":"+sessionId,sessionId,60, TimeUnit.MINUTES);
            // 统计访问量
            stringRedisTemplate.opsForValue().increment("visitorNum");
            return sysResultVo.ok("登录成功！",data);
        }catch (UnknownAccountException ignored) { // 用户名不存在
            String msg = "用户不存在！";
            if((int)session.getAttribute("isAdmin")==1){
                msg="管理员不存在！";
            }
            return sysResultVo.userNotExist(msg,null);
        } catch (LockedAccountException e){ // 账号被锁定，即黑名单
            return  sysResultVo.userLocked("您的账号已封，请联系管理员解决!",null);
        } catch (IncorrectCredentialsException ice) { // 密码错误
            return sysResultVo.psdNotExist("密码错误！",null);
        }// shiro内部问题
        catch (AuthenticationException e) {
            return sysResultVo.serverError("系统繁忙，请稍后重试。。。",Thread.currentThread().getStackTrace());
        }

        /**
         * Subject对象的其他基本用法如下：
         * 1，得到当前登录的用户名
         *   String currentUser = subject.getPrincipal().toString();
         * 2，校验当前用户的权限
         *   判断用户是否是拥有某种角色
         *       boolean isRole = subject.hasRole( "admin" );
         *   是否拥有某种功能
         *       boolean isPer = subject.isPermitted("xiaoming:run");
         * 3，退出登录
         *  subject.logout();
         *
         * */



    }

    // 点击刷新验证码图片
    @ResponseBody
    @GetMapping (value = "verifyCode")
    public void verifyCode(HttpServletRequest request, HttpServletResponse resp) throws IOException {

        String text =  utilService.createCaptcha();
        HttpSession session = request.getSession(true);
        // 刷新页面，img标签也会触发该方法刷新验证码
        session.setAttribute("verify_code", text);
        utilService.outputVerifyCodeImg(resp.getOutputStream());
    }

    // 注册页
    @GetMapping({"toRegister","register"})
    public String registerPage(){

        // 判断是否注册登录过
        Subject subject = SecurityUtils.getSubject();
        if(subject.isAuthenticated()||subject.isRemembered()){
            return "redirect:/home";
        }
        return "registered";
    }

    // 用户注册逻辑
    @ResponseBody
    @RequestMapping(value = "register", method = RequestMethod.POST)
    public Map<String,String> register(@RequestBody User user){
        System.out.println("ahahahh");
        System.out.println("hahah"+user.toString());
        HashMap<String, String> res = new HashMap<>();
        res.put("msg", "注册成功，即将跳转到登录页");
        try {

            // 对注册用户生成id和加密盐
            User newUser = utilMethods.setIdAndSalt(user);
            // 对密码盐值加密
            newUser.setPassword(utilMethods.psdHash(user.getPassword(),user.getSalt()));
            try {
                //存数据库
                userBeanService.userRegister(user);
                System.out.println(user.toString());
            }catch (Exception e){
                e.printStackTrace();
                res.put("msg", "服务器繁忙，请稍后再注册。。。");
                return res;
            }
        }catch (Exception e){
            res.put("msg", "服务器繁忙，请稍后再注册。。。");
        }


        return res;
    }

    // 登出
    @RequestMapping(value ="logout")
    public String logout(){

        /**
         *  存sessionId：已存处：记住我拦截器、登录成功，还待存处：修改密码、注销
         * */
        Subject subject = SecurityUtils.getSubject();
        // 登出应删除redis的key
        User user = (User) subject.getPrincipal();
        String sessionId = (String) subject.getSession().getId();
        stringRedisTemplate.delete("user:"+user.getUserId()+":"+sessionId);
        // 再退出登录
        subject.logout();
        return "redirect:/toLogin";
    }


    // 注销
    @ResponseBody
    @RequestMapping(value = "zhuxiao",method = RequestMethod.POST)
    public String zhuxiao(@RequestBody String sign ){

        // 验证是否正常聚道注销账号
        if(!("lock".equals(sign.replace("=","")))){
            return "1"; // 不正常渠道
        }
        try {
            try {
                // 锁定账号
                userBeanService.lockUser();
            }catch (Exception e){
                e.printStackTrace();
                return "2"; // 失败
            }
            // 踢出
            Subject subject = SecurityUtils.getSubject();
            subject.logout();
        }catch (Exception e){
            e.printStackTrace();
            return "3"; // 也算成功
        }

        /**
         *  注销时应删除redis的key
         *  存sessionId：已存处：记住我拦截器、登录成功，还待存处：修改密码
         * */
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        String sessionId = (String) subject.getSession().getId();
        stringRedisTemplate.delete("user:"+user.getUserId()+":"+sessionId);
        return "3";  // 跳转交给前端
    }

    // 密码修改，分普通用户和管理员
    @ResponseBody
    @RequestMapping(value = "/updatePsd",method = RequestMethod.POST)
    public Map<String,Object> updatePsd(@RequestBody Map<String,Object> body){

        HashMap<String, Object> result = new HashMap<>();

        try {
            String oldPsd = (String) body.get("oldPsd");
            String newPsd= (String) body.get("newPsd");
            Boolean isAdmin= (Boolean) body.get("isAdmin");
            result = (HashMap<String, Object>) userBeanService.updatePsd(oldPsd, newPsd, isAdmin);
        }catch (Exception e){

            result.put("state",3);
            e.printStackTrace();
            return result;

        }
        /**
         *  成功修改密码时应删除redis的key
         *  存sessionId：已存处：记住我拦截器、登录成功，还待存处：修改密码
         * */
        return  result;
    }
}

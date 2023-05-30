package com.jing.librarymanagementsystem.shiroRealm;

import com.jing.librarymanagementsystem.bean.OperatePermissionResources;
import com.jing.librarymanagementsystem.bean.User;
import com.jing.librarymanagementsystem.shiroAndRedis.MyByteSource;
import com.jing.librarymanagementsystem.mappers.UserMapper;
import com.jing.librarymanagementsystem.util.UtilMethods;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

//自定义的UserRealm
//@Component
public class UserRealm extends AuthorizingRealm {

    @Resource
    UserMapper userMapper;
    @Resource
    UtilMethods utilMethods;

    /** 负责获取认证信息并封装  但是具体的密码比对不在此方法 是shiro框架底层帮我们完成*/
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        System.out.println("执行了=>认证doGetAuthorizationInfo");
        // userToken得到的是表单提交的用户名（或邮箱）和明文密码
        UsernamePasswordToken userToken = (UsernamePasswordToken) token;
        Session session1 = SecurityUtils.getSubject().getSession();
        User user = userMapper.selectUserInfo(userToken.getUsername(), (int) session1.getAttribute("isAdmin"));

        // 将用户信息存在session中，以后有用到(下面中user对象已经存储到Subject中，不用存到session）
//        Subject currentUser = SecurityUtils.getSubject();
//        Session session = currentUser.getSession();
//        session.setAttribute("userObject",user);
        // 先判断用户是否存在
        if (user  == null) {
            return null;//会抛出异常 UnknownAccountException 用户不存在异常
        }
        // 再判断用户是否被锁定，即拉黑
        if (user.getIsLock()){
            throw new LockedAccountException("您的账号已被锁定，请联系管理员解决！");
        }
        //将ip信息封装到user
        Session session = SecurityUtils.getSubject().getSession();
        String addr = (String) session.getAttribute("ipAddr");
        if(addr == null){
            addr="湖南省";
        }
        user.setAdress(addr);
        // user对象虽然后台可以通过subject取，但前台也需要
        session.setAttribute("user", user);
        /**密码认证，shiro做，错误会抛出IncorrectCredentialsException
         传入的credentials是自己从数据库拿到的密码，shiro只是帮你将表单填的密码加密，然后和这个对比。*/
        //ByteSource对盐做一个编码处理
//        ByteSource credentialsSalt = ByteSource.Util.bytes(user.getSalt());
        // 若shiro整合redis，由于shiro默认的ByteSource没有序列化，因此须使用自定义的MyBateSource
        MyByteSource credentialsSalt = new MyByteSource(user.getSalt());

        SimpleAuthenticationInfo info =new SimpleAuthenticationInfo(
            /**principal 表示登录用户身份,会充当下面授权方法的参数。也会封装到subject对象中供我们使用，
             * 。它可以是字符串,也可以是一个user对象，也可以是一个集合，但不能是数组对象。*/
                user,
                user.getPassword(), //Credentials 数据库中已加密的密码
                credentialsSalt, //加密盐,ByteSource对盐值对象的处理
                /**该值也会传到授权方法中，由方法参数的getRealmNames()可获取到*/
                this.getName()); //realmName 值：com.jing.librarymanagementsystem.shiroRealm.UserRealm_0
        return  info; //将封装好的数据交给securityManager进行认证
    }

    //授权,分两类，资源权限和角色权限，自己查到的权限和角色都封装到SimpleAuthorizationInfo对象中，供shiro的安全管理器security management去实现拦截功能。
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("执行了=>授权doGetAuthorizationInfo");
        /** principalCollection就等于new SimpleAuthenticationInfo的第一个参数对象
         * 注意：若开启了redis缓存，对于同一个用户，只要第二次登录（就算你项目重启也一样，数据还是在redis），那么就不会执行认证，因为第一次认证后你的信息就
         * 在redis，第二次登录时，在缓存中记录的用户中你是否存在，若存在交，则不会执行登录认证，即上面方法，若不存在，当然会认证。
         * 因此，principalCollection这个参数以后会在缓存中拿。
         * 该对象的使用：
         * 1，若该对象上面方法传的是一个字符串或user对象
         *   1.1：getPrimaryPrincipal可获取到该值
         * 2，若是一个集合对象
         *    1.1：可直接对principalCollection类型转化成该集合对象得到。
         *    1.2:可通过getPrimaryPrincipal得到集合第一个值。
         * 3，getRealmNames()：可得到认证方法中SimpleAuthenticationInfo的第四个参数对象。
         * */
        //1.获取登录用户信息
        User user = (User)principalCollection.getPrimaryPrincipal();

        // 2，该对象是封装用户权限及角色信息，供shiro的安全管理使用。
        SimpleAuthorizationInfo info=new SimpleAuthorizationInfo();

        // 根据用户ID查询角色（role），放入到Authorization里。由于user对象中就有角色，不需要再往数据库查
        /** 通过subject对象获取认证方法中SimpleAuthenticationInfo的第一个参数即用户身份的方法：
         * getPrincipal：获得当前身份,如果认证中SimpleAuthenticationInfo的那个参数是集合类型，那么获取的是第一个值
         * getPrincipals:得到的就是PrincipalCollection这个对象，也可以获得用户身份信息。
         * getPreviousPrincipals()：不管是集合还是字符串或user对象，返回的是空值
         * */

        Set<String> roles = new HashSet<String>();
        roles.add(user.getRole());
         // 封装用户具有的角色信息到info对象
        info.setRoles(roles);

        //3.基于登录用户获取用户权限(根据用户id去数据库查询到当前用户权限)
        Set<String> permissions= userMapper.selectUserPermissions(user.getUserId());
        System.out.println(permissions.toString());
        //3.封装用户权限并返回
        info.setStringPermissions(permissions);


        // 4 由于subject不会对权限和角色信息封装，我们调用自定义的工具方法operatePermissionResourceResolver，将操作型权限封装到自定义的OperatePermissionResource类中，存入session，供以后使用
        OperatePermissionResources operatePermissionResources = utilMethods.operatePermissionResourceResolver(permissions);
        SecurityUtils.getSubject().getSession().setAttribute("permissionValues",operatePermissionResources);

        return info;
    }


    /**
     * ShiroConfig中配置redis时已经配置了，这里不用配
     * */
//    /*获取凭证匹配器(加密策略)，使用什么算法和策略对密码进行加密*/
//    @Override
//    public CredentialsMatcher getCredentialsMatcher( ) {
//        //创建密码匹配器对象
//        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
//        //设置加密算法
//        matcher.setHashAlgorithmName("MD5");
//        //设置散列次数（加密次数），不能随便修改，若这里改，那么工具类中给注册用户加密的方法内也要是这个值
//        matcher.setHashIterations(10);
//
//        return matcher;
//    }


    /**
     * 清空已经放入缓存的授权信息。
     * */
    public void clearCache() {
        PrincipalCollection principals=SecurityUtils.getSubject().getPrincipals();
        super.clearCache(principals);
    }

}


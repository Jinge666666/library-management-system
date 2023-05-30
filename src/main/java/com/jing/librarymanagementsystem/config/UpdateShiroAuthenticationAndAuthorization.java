package com.jing.librarymanagementsystem.config;


import com.jing.librarymanagementsystem.bean.User;
import com.jing.librarymanagementsystem.mappers.UserMapper;
import com.jing.librarymanagementsystem.shiroRealm.UserRealm;
import com.xiaoleilu.hutool.json.JSON;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.LogoutAware;
import org.apache.shiro.authz.Authorizer;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;


/**
 * 用户信息更新并删除授权缓存，重新授权+认证
 *  需执行用户重新登录/授权的操作，或者一次性将用户不同系统的所有权限给出,其实主要是为了变更缓存的用户信息（用户所在系统空间）
 * */
@Component
public class UpdateShiroAuthenticationAndAuthorization implements Serializable {

    @Resource
    UserMapper userMapper;
    @Resource
    UserRealm userRealm;


    // 更新认证信息 主要是更新new SimpleAuthenticationInfo的第一个参数
    /**
     * 注意：new SimplePrincipalCollection(newUser, realmName);的newUser必须是从SecurityUtils拿的，你若在外从数据库返回的User再放入进来，是不会更新subject的，别看都是User对象，但不同的
     * 因此，只能对SecurityUtils得出的user修改，再放入new SimplePrincipalCollection中。
     * */

    // 该方法参数必须从数据库得出的user。
    public void AuthenticationInfoUpdate(User user)  {

//        RealmSecurityManager rsm = (RealmSecurityManager) SecurityUtils.getSecurityManager();
//        //AccountAuthorizationRealm为在项目中定义的realm类
//        UserRealm shiroRealm = (UserRealm) rsm.getRealms().iterator().next();

        Subject subject = SecurityUtils.getSubject();
        PrincipalCollection principals = subject.getPrincipals();
        //realName认证信息的key，对应的value就是认证的user对象
        String realmName = principals.getRealmNames().iterator().next();
        // 拿到subject的user
        User newUser = (User) SecurityUtils.getSubject().getPrincipal();
        // 将subject的user所有参数替换为要更新的user参数
        newUser.setUserName(user.getUserName());
        newUser.setPassword(user.getPassword());
        newUser.setBackgroundImageUrl(user.getBackgroundImageUrl());
        newUser.setAdress(user.getAdress());
        newUser.setSalt(user.getSalt());
        newUser.setAvatarUrl(user.getAvatarUrl());
        newUser.setEmail(user.getEmail());
        newUser.setExp(user.getExp());
        newUser.setIsAdmin(user.getIsAdmin());
        newUser.setIsLock(user.getIsLock());
        newUser.setRole(user.getRole());
        newUser.setMotto(user.getMotto());
        newUser.setIsRemember(user.getIsRemember());
        newUser.setIsUpdateName(user.getIsUpdateName());

        // 生成新的principal
        PrincipalCollection newPrincipalCollection = new SimplePrincipalCollection(newUser, realmName);
        // 用户身份切换 调用subject的runAs方法，把新的PrincipalCollection放到session里面
        subject.runAs(newPrincipalCollection);

//        //用realm删除principle
//        shiroRealm.getAuthorizationCache().remove(subject.getPrincipals());
//        //切换身份也就是刷新了
//        subject.releaseRunAs();


    }


    /**在上面方法更新subject的基础上再次封装，增加session的user更新，并自动查询数据库拿到用户最新的信息，
     * 无需外部传任何数据,外部只需要负责更新数据库，该方法会自动刷新subject和session的用户信息。
     * */
    public void AuthenticationInfoUpdateUtil(){

        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        // 根据id查值得到最新用户信息，id是不可能被修改的。
        User newUser = userMapper.selectUserInfoById(user.getUserId());
        // 1,更新session的user
        subject.getSession().setAttribute("user",user);
        //2,更新subject的user
        this.AuthenticationInfoUpdate(newUser);
    }

    // 重新授权，清除shiro的授权缓存，使下次访问授权接口能再次触发授权认证
    public void  AuthorizationInfoUpdate(){

        // 拿到以前的授权信息
        PrincipalCollection principals = SecurityUtils.getSubject().getPrincipals();
        // 需要根据变更前的principals删除之前的授权信息
        this.deleteCacheBySimplePrincipalCollection((SimplePrincipalCollection) principals);

        // 用户权限改变必然同步到数据库了，我们把subject和session刷新
        this.AuthenticationInfoUpdateUtil();
    }

    // 根据principalCollection删除授权缓存
    public void deleteCacheBySimplePrincipalCollection(SimplePrincipalCollection attribute) {
        //删除Cache，再访问受限接口时会重新授权
        DefaultWebSecurityManager securityManager = (DefaultWebSecurityManager) SecurityUtils.getSecurityManager();
        Authenticator authc = securityManager.getAuthenticator();
//        Authorizer authorizer = securityManager.getAuthorizer();
//        ((LogoutAware) authorizer).onLogout(attribute);
        ((LogoutAware) authc).onLogout(attribute);
//        userRealm.clearCache();

    }

}

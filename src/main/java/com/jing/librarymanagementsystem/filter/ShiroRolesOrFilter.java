package com.jing.librarymanagementsystem.filter;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * shiro权限控制的roles也是并的关系，我们自定义一个支持或的关系的角色控制器
 * */
public class ShiroRolesOrFilter extends AuthorizationFilter {
    @Override
    protected boolean isAccessAllowed(ServletRequest req, ServletResponse resp, Object mappedValue) throws Exception {
        Subject subject = getSubject(req, resp);
        String[] rolesArray = (String[]) mappedValue;

        if (rolesArray == null || rolesArray.length == 0) { //没有角色限制，有权限访问
            return true;
        }
        //若当前用户是rolesArray中的任何一个，则有权限访问
        for (String s : rolesArray) {
            if (subject.hasRole(s)) {
                return true;
            }
        }

        return false;
    }

}

package com.jing.librarymanagementsystem.filter;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 自定义一个shiro的权限控制规则——"or-perms",该过滤器完美包含了shiro自带的perms的所有功能，还新添了以|为分割符的“或”的关系。
 * 举例：
 * 1，or-perms[a,b] a和b权限都要满足才通过
 * 2，or-perms[a|b] a或者b权限满足一个就通过
 * 3，or-perms[a|b,c|d] 这解析为数组是两个元素，这表示a|b和c|d都要满足才返回true，而其中如a|b,a和b权限有一个满足就返回true。
 *
* */
public class ShiroPermsOrFilter extends AuthorizationFilter {
   @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        Subject subject = this.getSubject(request, response);
        String[] perms =  (String[]) mappedValue;
        boolean isPermitted = true;
        if (perms != null && perms.length > 0) {
            if (perms.length == 1) {
                if (!isOneOfPermitted(perms[0], subject)) {
                    isPermitted = false;
                }
            } else if (!isAllPermitted(perms,subject)) {
                isPermitted = false;
            }
        }
        return isPermitted;
    }

    /**
     * 以“，”分割的权限为并列关系的权限控制，分别对每个权限字符串进行“|”分割解析
     * 若并列关系的权限有一个不满足则返回false，表示权限的与关系，这也满足了shiro自带的perms的特性
     *
     * @param permStrArray 以","分割的权限集合
     * @param subject      当前用户的登录信息
     * @return 是否拥有该权限
     */
    private boolean isAllPermitted(String[] permStrArray, Subject subject) {
        boolean isPermitted = true;
        for (String s : permStrArray) {
            if (!isOneOfPermitted(s, subject)) {
                isPermitted = false;
                break;
            }
        }
        return isPermitted;
    }

    /**
     * 判断以“|”分割的权限有一个满足的就返回true，表示权限的或者关系
     *
     * @param permStr 权限数组种中的一个字符串，就是没有逗号
     * @param subject 当前用户信息
     * @return 是否有权限
     */
    private boolean isOneOfPermitted(String permStr, Subject subject) {
        boolean isPermitted = false;
        String[] permArr = permStr.split("\\|");
        if (permArr.length > 0) {
            for (String s : permArr) {
                // 这就是我们自定义的or-perms的核心功能，如果没有逗号，那么数组只有一个字符串，该字符串以“|"分割，表示只要有一个|分割的权限
                // 满足，那么就返回true
                if (subject.isPermitted(s)) {
                    isPermitted = true;
                    break;
                }
            }
        }
        return isPermitted;
    }

}


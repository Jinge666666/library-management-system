package com.jing.librarymanagementsystem.controller;

import com.jing.librarymanagementsystem.bean.User;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 专门将系统中需要相关视图型权限才能开放的资源集成到此controller，比如超级管理员独有的资源
 */
public class PermissionController {

    // 管理员账号信息展示
    @GetMapping({"adminInfoPage"})
    @RequiresPermissions({"view:/backstage/admin-management"})
    // 其实单个权限判断或角色判断在这里都行，但加个角色判断就是都要满足才能进入，更安全吧
    @RequiresRoles({"超级管理员"})
    public String adminInfoPage(){

        return "/backstage/admin-management";
    }

    // 操作型权限的展示
    @GetMapping("operatePermissionsPage")
    @RequiresPermissions({"view:/backstage/permission-management"})
//    @RequiresRoles({"超级管理员"})
    public String operatePermissionsPage(){

        return "/backstage/permission-management";
    }
}

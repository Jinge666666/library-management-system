package com.jing.librarymanagementsystem.service.impl;

import com.jing.librarymanagementsystem.bean.OperatePermissionResources;
import com.jing.librarymanagementsystem.bean.User;
import com.jing.librarymanagementsystem.mappers.PermissionsVerifyMapper;
import com.jing.librarymanagementsystem.service.PermissionsVerifyService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class PermissionsVerifyServiceImpl implements PermissionsVerifyService {

    @Resource
    PermissionsVerifyMapper permissionsVerifyMapper;

    // 收藏 先验证是否有收藏书的权限，然后再在方法内验证是否数量达到上限。
    @Override
        @RequiresPermissions(value = {"operate:addbook:10","operate:addbook:20","operate:addbook:40","operate:addbook:100","operate:addbook:无上限"},logical = Logical.OR)
    // 不建议使用上面的方式把匹配规则写死，这不利于后面添加角色了，比如添加一个星耀段位。
//    @RequiresPermissions(value = {"operate:addbook:*"},logical = Logical.OR)
    public Boolean bookMarkedVerify() {

        Subject subject = SecurityUtils.getSubject();
        // 拿到权限
        OperatePermissionResources permissionValues = (OperatePermissionResources) subject.getSession().getAttribute("permissionValues");
        // 拿到userId
        User user = (User) subject.getPrincipal();
        String userId = user.getUserId();
        // 查询数量
        int alCollectedNum = permissionsVerifyMapper.bookMarkCount(userId);

        return  alCollectedNum<permissionValues.getOperateAddBookPermission();
    }

    // 借书时相关权限验证
    @Override
    @RequiresPermissions(value = {"operate:borrowbook:3","operate:borrowbook:6","operate:borrowbook:10","operate:borrowbook:20","operate:borrowbook:50"},logical = Logical.OR)
//    @RequiresPermissions(value = {"operate:borrowbook:*"},logical = Logical.OR)
    public Boolean bookBorrowedVerify() {

        Subject subject = SecurityUtils.getSubject();
        // 拿到权限
        OperatePermissionResources permissionValues = (OperatePermissionResources) subject.getSession().getAttribute("permissionValues");
        // 拿到userId
        User user = (User) subject.getPrincipal();
        String userId = user.getUserId();
        // 查询数量
        int alBorrowedNum = permissionsVerifyMapper.bookBorrowCount(userId);

        return  alBorrowedNum<permissionValues.getOperateBorrowBookPermission();
    }



    //今日评论数是否封顶
    @Override
    @RequiresPermissions(value = {"operate:comments:10","operate:comments:20","operate:comments:40","operate:comments:100","operate:comments:无上限"},logical = Logical.OR)
//    @RequiresPermissions(value = {"operate:comments:*"},logical = Logical.OR)
    public Boolean commentsVerify() {

        Subject subject = SecurityUtils.getSubject();
        // 拿到权限
        OperatePermissionResources permissionValues = (OperatePermissionResources) subject.getSession().getAttribute("permissionValues");
        // 拿到userId
        User user = (User) subject.getPrincipal();
        String userId = user.getUserId();
        // 获得今日时间
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        String time = sf.format(new Date())+" 00:00:00";
        // 查询数量
        int commentUpLimit = permissionsVerifyMapper.commentsCount(userId,time);

        return  commentUpLimit<permissionValues.getOperateCommentsPermission();
    }

    @Override
    @Transactional // 修改操作型权限 index表示由青铜到王者从1开始间隔为1的下标
    public void editPermission(String role,String collectCeiling, String borrowCeiling, String commentsCeiling, int exp) {
        int index=1;
        switch (role){
            case "青铜":
                index=1;
                break;
            case "白银":
                index=2;
                break;
            case "黄金":
                index=3;
                break;
            case "砖石":
                index=4;
                break;
            case "王者":
                index=5;
                break;
        }
        permissionsVerifyMapper.editPermission(role,index,collectCeiling,borrowCeiling,commentsCeiling,exp);
    }


}

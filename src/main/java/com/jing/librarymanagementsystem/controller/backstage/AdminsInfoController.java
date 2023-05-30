package com.jing.librarymanagementsystem.controller.backstage;

import com.jing.librarymanagementsystem.mappers.UserMapper;
import com.jing.librarymanagementsystem.service.BeanService.UserBeanService;
import com.jing.librarymanagementsystem.service.PermissionsVerifyService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/backstage")
public class AdminsInfoController {

    @Autowired
    UserMapper userMapper;
    @Autowired
    UserBeanService userBeanService;
    @Autowired
    PermissionsVerifyService permissionsVerifyService;

    @GetMapping("/showAdmins")
    public String showAdminPage(){

        return "backstage/admin-management";
    }

    @GetMapping("/showPermissions")
    public String showPermissionsPage(){

        return "backstage/permission-management";
    }

    // 修改密码
    @RequestMapping("/toPsdUpdate")
    public String toPsdUpdate(){

        return "backstage/password-update";
    }


    // 删除单个用户
    @ResponseBody
    @RequestMapping(value ="/deleteOneUser",method= RequestMethod.POST)
    public Boolean deleteOneUser(@RequestBody String id){

        try {
            userMapper.deleteUser(id.replace("=",""));
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // 晋级超级管理员
    @ResponseBody
    @RequestMapping(value = "/toSuper",method = RequestMethod.POST)
    public Boolean toSuper(@RequestBody String id){

        try {
            userBeanService.toSuper(id.replace("=",""));
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }




    // 权限管理
    @ResponseBody
    @RequestMapping(value = "/saveEditPermissions",method = RequestMethod.POST)
    public Boolean saveEditPermissions(@RequestBody Map<String,Object> body){

        System.out.println(body.toString());
        try {
            String collectCeiling= (String) body.get("collectCeiling");
            String borrowCeiling= (String) body.get("borrowCeiling");
            String commentsCeiling= (String) body.get("commentsCeiling");
            int exp = Integer.parseInt((String)body.get("exp"));
            String role= (String) body.get("role");
            try {
                permissionsVerifyService.editPermission(role,collectCeiling,borrowCeiling,commentsCeiling,exp);
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    // 登出
    @RequestMapping(value ="/logout")
    public String logout(){

        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "redirect:/toLogin";
    }
}

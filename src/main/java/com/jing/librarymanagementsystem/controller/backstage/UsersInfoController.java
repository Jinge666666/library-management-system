package com.jing.librarymanagementsystem.controller.backstage;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jing.librarymanagementsystem.bean.User;
import com.jing.librarymanagementsystem.mappers.PermissionsVerifyMapper;
import com.jing.librarymanagementsystem.mappers.UserMapper;
import com.jing.librarymanagementsystem.service.BeanService.UserBeanService;
import com.jing.librarymanagementsystem.util.UtilMethods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/backstage")
public class UsersInfoController {

    @Resource
    UserMapper userMapper;
    @Resource
    UtilMethods utilMethods;
    @Autowired
    UserBeanService userBeanService;
    @Resource
    PermissionsVerifyMapper permissionsVerifyMapper;

    @RequestMapping(value = "/toUserInfo")
    public String toUserInfo(){

        return "/backstage/user-management";
    }

    @ResponseBody // 展示用户，可按条件
    @RequestMapping(value = "/showUser",method = RequestMethod.POST)
    public Map<String,Object> showUsers(@RequestBody Map<String,Object> body){

        Map<String, Object> result = new HashMap<>();
        String userName = "%"+body.get("userName")+"%";
        String email = "%"+body.get("email")+"%";
        Boolean isActive = (Boolean) body.get("isActive");
        Boolean isLock = (Boolean) body.get("isLock");
        Boolean isAdmin = (Boolean) body.get("isAdmin");
        int pageNum = (int)body.get("pageNum");
        int pageSize = (int) body.get("pageSize");
        PageHelper.startPage(pageNum,pageSize);
        List<User> users = userMapper.selectAllUser(userName, email, isActive, isLock, isAdmin);
        PageInfo<User> pageInfo = new PageInfo<>(users);
        result.put("users",users);
        result.put("total",pageInfo.getTotal());
        return result;
    }

    // 添加用户
    @ResponseBody
    @RequestMapping(value = "/addUser",method = RequestMethod.POST)
    public Boolean addUser(@RequestBody User user){

        try {
            // 生成用户id、加密盐
            // 对注册用户生成id和加密盐
            User newUser = utilMethods.setIdAndSalt(user);
            // 对密码盐值加密
            newUser.setPassword(utilMethods.psdHash(user.getPassword(),user.getSalt()));
            try {
                //存数据库
                userBeanService.userRegister(user);
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

    // 搜索全部角色
    @ResponseBody
    @RequestMapping(value = "/getAllRoles",method=RequestMethod.POST)
    public List<String> getAllRoles(){

        return permissionsVerifyMapper.getAllRoles();
    }

    // 删除用户
    @ResponseBody
    @RequestMapping(value = "/deleteUsers",method=RequestMethod.POST)
    public Boolean deleteUsers(@RequestBody List<String> ids){

        try {
            userBeanService.deleteUsers(ids);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // 用户信息修改 角色和经验
    @ResponseBody
    @RequestMapping(value = "/updateUserInfo",method = RequestMethod.POST)
    public Boolean updateUserInfo(@RequestBody Map<String,String> body){

        try {
            String role = body.get("role");
            int exp=Integer.parseInt(body.get("exp"));
            String userId = body.get("userId");
            System.out.println(body.toString());
            try {
                userBeanService.updateRoleAndExp1(role,exp,userId);
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

    @ResponseBody // 拉黑
    @RequestMapping(value = "/lockUser",method = RequestMethod.POST)
    public Boolean lockUser(@RequestBody String userId){

        try {
            userBeanService.lockUser(userId.replace("=",""));
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // 取消拉黑
    @ResponseBody // 拉黑
    @RequestMapping(value = "/unLockUser",method = RequestMethod.POST)
    public Boolean unLockUser(@RequestBody String userId){

        try {
            userBeanService.unLockUser(userId.replace("=",""));
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

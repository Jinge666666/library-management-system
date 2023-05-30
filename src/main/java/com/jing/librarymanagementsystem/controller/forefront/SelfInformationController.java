package com.jing.librarymanagementsystem.controller.forefront;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jing.librarymanagementsystem.bean.User;
import com.jing.librarymanagementsystem.config.UpdateShiroAuthenticationAndAuthorization;
import com.jing.librarymanagementsystem.mappers.UserMapper;
import com.jing.librarymanagementsystem.service.PersonalInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;



/*
* 个人信息controller
* */
@Controller
@Slf4j
@RequestMapping("/selfInfo")
public class SelfInformationController {


    // 头像相对static的目录路径
    @Value("${pro.avatarUploadPath}")
    private String uploadPath;

    // 项目相对路径
    public final static String UPLOAD_PATH_PREFIX = "src/main/resources/static";
    @Autowired
    PersonalInfoService personalInfoService;

    @Autowired // 自定义的shiro的用户信息修改
    UpdateShiroAuthenticationAndAuthorization updateShiroAuthenticationAndAuthorization;

    @RequestMapping
    public String toSelfInfo(@RequestParam(value = "toUpdateInfo",required = false) String toUpdateInfo,@RequestParam(value = "toLevelUp",required = false)String toLevelUp,Model model) {

        if("updateAvatar".equals(toUpdateInfo)){
            model.addAttribute("updateAvatar",true);
        }
        if("toLevelUp".equals(toLevelUp)){
            model.addAttribute("toLevelUp",true);
        }
        return "forefront/personal-information";
    }


    //个人中心概览
    // 获取用户信息
    @ResponseBody
    @RequestMapping(value = "/personalInfo", method = RequestMethod.POST)
    public Map<String,Object> getPersonalInfo(){

        // 不能把私密信息返回
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        HashMap<String, Object> info = new HashMap<>();
        info.put("userId", user.getUserId());
        info.put("userName", user.getUserName());
        info.put("adress",user.getAdress());
        info.put("motto",user.getMotto());
        info.put("avatarUrl",user.getAvatarUrl());
        info.put("backgroundImageUrl",user.getBackgroundImageUrl());
        info.put("role",user.getRole());
        info.put("exp",user.getExp());
        info.put("isUpdateName",user.getIsUpdateName()); // 是否修改过用户名
        return info;

    }





    // 修改个人资料-------------------
    // 保存信息（上传头像除外）
    @ResponseBody
    @RequestMapping(value = "saveUserInfo",method = RequestMethod.POST)
    public Map<String,String> saveInfo(@RequestBody Map<String,Object> body, HttpSession session){

        Map<String, String> result = new HashMap<>();
        result.put("msg","保存成功！！！");
        try {
            String newUserName = (String) body.get("newUserName");
            String textarea = (String) body.get("textarea");
            String choiceBgImg = (String) body.get("choiceBgImg");
            // 判断是否修改过名字，若是，我们修改为null，不允许更新
            // 与修改前重名也需过滤，防止没修改名字但is_update_name字段却更新了
            User user = (User) SecurityUtils.getSubject().getPrincipal();
            if(user.getIsUpdateName()&&user.getUserName().equals(newUserName)){
                newUserName=null;
            } // sql已经设置了第一次更新用户名时is_update_name字段。
            try {
                // 更新数据库
                personalInfoService.saveUserEditInfo(newUserName,textarea,choiceBgImg);

                // 使用自定义方法一键同步数据库和subject、session对象的用户信息。
                updateShiroAuthenticationAndAuthorization.AuthenticationInfoUpdateUtil();

            }catch (Exception e){
                e.printStackTrace();
                result.put("msg","保存失败，请稍后再试。。。");
                e.printStackTrace();
                return result;
            }
        }catch (Exception e){
            result.put("msg","保存失败，请稍后再试。。。");
            e.printStackTrace();
            return result;
        }

        // 查看是否名字修改，
        return result;
    }
    // 校验昵称是否存在
    @ResponseBody
    @RequestMapping(value = "/verifyName",method = RequestMethod.POST)
    public Boolean verifyName(@RequestBody String name){

        return personalInfoService.isNameExist(name);
    }

    // 上传头像
    @ResponseBody
    @RequestMapping(value = "/avatarUpload",method = RequestMethod.POST)
    public Map<String,Object> saveAvatar(@RequestPart("avatar") MultipartFile avatarImg, HttpServletRequest request){

        Map<String, Object> map = new HashMap<>();
        map.put("msg", "上传成功");

        if (avatarImg.isEmpty()) {
            map.put("msg", "不能上传空文件");
            return map;
        }

        // 获取上传原始文件名
        String fileName = avatarImg.getOriginalFilename();
        if ("".equals(fileName)) {
            map.put("msg", "文件名不能为空");
            return map;
        }

        // 使用uuid生成新头像文件名
//        String newFileName = UUID.randomUUID().toString().replaceAll("-", "") + fileName.substring(fileName.lastIndexOf("."), fileName.length());
//        System.out.println("保存的文件的新名字: " + newFileName);
//
//        // 获取年月日的日期格式
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        String format = simpleDateFormat.format(new Date());
//
//        // 生成以日期分割的头像文件路径 File.separator是路径分隔符
//        File readPath = new File(UPLOAD_PATH_PREFIX + uploadPath + File.separator + format);
//        System.out.println("存放的文件夹: " + readPath);
//        System.out.println("存放文件的绝对路径: " + readPath.getAbsolutePath());
//        // 判断文件夹是否存在
//        if (!readPath.isDirectory()) {
//            // 创建文件夹
//            readPath.mkdirs();
//        }

        // 由于新的头像须替代旧头像，命名不能用uuid，就以用户id命名
        File readPath = new File(UPLOAD_PATH_PREFIX + uploadPath);
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        String newFileName=user.getUserId()+".jpg";
        // 文件真实的保存路径
        File file = new File(readPath.getAbsolutePath() + File.separator + newFileName);
        try {
            // 存
            avatarImg.transferTo(file);
            // 同时更新数据库
            personalInfoService.saveAvatar(newFileName);
            // 刷新subject和session的用户数据
            updateShiroAuthenticationAndAuthorization.AuthenticationInfoUpdateUtil();

            // 获取存储路径
//            fileName = format + "/" + newFileName;
//            String filePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + uploadPath + "/" + fileName;
//            // 前端img设置该路径就能显示你的头像
//            map.put("avatarPath", filePath);
        } catch (IOException e) {
            e.printStackTrace();
            map.put("msg", "上传失败，请稍后再试。");
        }

        return map;
    }








    // 等级信息展示
    @ResponseBody
    @RequestMapping(value="/levelShow",method = RequestMethod.POST)
    public List<Map<String,Object>> levelShow(){

        return personalInfoService.getAllPermissionsInfos();

    }
    // 存储晋级角色信息
    @ResponseBody
    @RequestMapping(value = "/levelUp",method = RequestMethod.POST)
    public Map<String,String> levelUp(@RequestBody Map<String,Object> body){

        HashMap<String, String> result = new HashMap<>();
        result.put("msg","晋级成功");
        try {
            String roleName = (String) body.get("roleName");
            try {
                personalInfoService.updateLevelUpInfo(roleName);
            }catch (Exception e){
                result.put("msg","晋级失败，请稍后再试。。。");
                return result;
            }
        }catch(Exception e){
            result.put("msg","晋级失败，请稍后再试。。。");
            return result;
        }

        return result;
    }





    // 我的书评

    @ResponseBody
    @RequestMapping(value ="/myBookComments",method= RequestMethod.POST)
    public Map<String,Object> myBookComments(@RequestBody Map<String, Object> body) {
        String searchBookName = "%"+(String) body.get("bookName")+"%";
        String startTime = (String) body.get("startTime");
        String endTime = (String) body.get("endTime");
        int pageSize = (int) body.get("pageSize");
        int pageNum = (int) body.get("pageNum");
        // 分页
        PageHelper.startPage(pageNum,pageSize);
        // 查数据
        List<Map<String,Object>> comments = personalInfoService.getComments(searchBookName, startTime, endTime);
        // 时间格式化
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Map<String,Object> comment : comments){
            comment.put("publish_time",sf.format((Date)comment.get("publish_time")));
        }
        PageInfo<Map<String, Object>> pageInfo= new PageInfo<Map<String, Object>>(comments);
        HashMap<String, Object> result = new HashMap<>();
        result.put("bookCommentsData",comments);
        result.put("total",pageInfo.getTotal());
        return  result;
    }
}

package com.jing.librarymanagementsystem.service.impl;

import com.jing.librarymanagementsystem.bean.OperatePermissionResources;
import com.jing.librarymanagementsystem.bean.User;
import com.jing.librarymanagementsystem.config.UpdateShiroAuthenticationAndAuthorization;
import com.jing.librarymanagementsystem.mappers.PersonalInfoMapper;
import com.jing.librarymanagementsystem.service.PersonalInfoService;
import com.jing.librarymanagementsystem.util.UtilMethods;
import org.apache.ibatis.annotations.MapKey;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PersonalInfoServiceImpl implements PersonalInfoService {

    @Autowired
    PersonalInfoMapper mapper;
    @Autowired
    UtilMethods utilMethods;

    @Autowired
    UpdateShiroAuthenticationAndAuthorization updateShiroAuthenticationAndAuthorization;

    @Override
    public List<Map<String,Object>> getComments(String searchBookName, String startTime, String endTime) {

        User user = (User) SecurityUtils.getSubject().getPrincipal();
        return mapper.selectComments(searchBookName, startTime, endTime, user.getUserId());


    }

    // 等级展示，须得到每个角色对应的操作型权限信息
    @Override
    public List<Map<String, Object>> getAllPermissionsInfos() {

        List<Map<String, Object>> result = new ArrayList<>();
        // 获取角色和经验值
        List<Map<String, Object>> roleAndExps = mapper.selectRoleAndExp();
        for (Map<String, Object> roleAndExp : roleAndExps) {
            String roleName = (String) roleAndExp.get("name");
            String exp =String.valueOf((int)roleAndExp.get("exp"));
            // 根据角色获得权限
            List<String> permission = mapper.selectPermissionsByRole("%"+roleName+"%");
            // 调用权限解析器
            HashSet<String> setPermission = new HashSet<>(permission);
            OperatePermissionResources operatePermissionResources = utilMethods.operatePermissionResourceResolver(setPermission);
            // 将得到的权限对象和角色、经验值一起封装到map
            HashMap<String, Object> data = new HashMap<>();
            data.put("roleName",roleName);
            data.put("permission",operatePermissionResources);
            data.put("exp",exp);
            result.add(data);
        }
        System.out.println(result.toString());
        return result;
    }

    @Override
    public Boolean isNameExist(String name) {

        return mapper.isNameExist(name)!=null;
    }

    @Override
    @Transactional
    public void saveAvatar(String avatar) {

        User user = (User) SecurityUtils.getSubject().getPrincipal();
        mapper.saveAvatar(avatar,user.getUserId());
    }

    @Override
    @Transactional // 加入事务
    public void saveUserEditInfo(String name, String motto, String bgImgName) {

        User user = (User) SecurityUtils.getSubject().getPrincipal();
        mapper.saveEditInfo(name,motto,bgImgName,user.getUserId());
    }

    @Override
    @Transactional
    public void updateLevelUpInfo(String roleName) {

        User user = (User) SecurityUtils.getSubject().getPrincipal();
        mapper.updateLevelUpInfo(roleName,user.getUserId());
        // 刷新缓存权限，下次访问权限接口时会自动再次授权
        updateShiroAuthenticationAndAuthorization.AuthorizationInfoUpdate();
    }
}

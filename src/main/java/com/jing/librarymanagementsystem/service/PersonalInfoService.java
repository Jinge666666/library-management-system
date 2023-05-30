package com.jing.librarymanagementsystem.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface PersonalInfoService {

    // 获得个人评论信息
    public List<Map<String, Object>> getComments(String searchBookName, String startTime, String endTime);

    // 获得所有角色和权限信息 管理员可扩展
    public List<Map<String, Object>> getAllPermissionsInfos();

    // 判断昵称是否存在
    public Boolean isNameExist(String name);

    // 保存头像信息
    public void saveAvatar(String avatar);

    // 保存用户编辑信息
    public void saveUserEditInfo(String name,String motto,String bgImgName);

    // 晋级
    public void updateLevelUpInfo(String roleName);
}

package com.jing.librarymanagementsystem.service.BeanService;

import com.jing.librarymanagementsystem.bean.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface UserBeanService {

    // 用户注册
    public void userRegister(User user);

    // 删除账号
    public void deleteUser();

    // 锁定账号
    public void lockUser();

    // 可传用户id的锁定
    public void lockUser(String userId);
    // 取消锁定
    public void unLockUser(String userId);

    // 批量删除
    public void deleteUsers(List<String> ids);



    // 升级超级管理员
    public void toSuper(String id);

    // 修改密码
    public Map<String,Object>  updatePsd(String oldPsd, String newPsd, Boolean isAdmin);

//    // 用户角色和经验更新
    public void updateRoleAndExp1(String role,int exp,String userId);
}

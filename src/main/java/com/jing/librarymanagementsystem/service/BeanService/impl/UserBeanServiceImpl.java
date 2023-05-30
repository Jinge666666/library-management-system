package com.jing.librarymanagementsystem.service.BeanService.impl;

import com.jing.librarymanagementsystem.bean.User;
import com.jing.librarymanagementsystem.mappers.UserMapper;
import com.jing.librarymanagementsystem.service.BeanService.UserBeanService;
import com.jing.librarymanagementsystem.util.UtilMethods;
import org.apache.ibatis.ognl.security.UserMethod;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserBeanServiceImpl implements UserBeanService {

    @Resource
    UserMapper mapper;
    @Resource
    UtilMethods utilMethods;


    @Override
    @Transactional
    public void userRegister(User user) {

        mapper.userRegistered(user);
    }

    @Override
    @Transactional
    public void deleteUser() {

        User user = (User) SecurityUtils.getSubject().getPrincipal();
        mapper.deleteUser(user.getUserId());
    }

    @Override
    @Transactional
    public void lockUser() {

        User user = (User) SecurityUtils.getSubject().getPrincipal();
        mapper.lockUser(user.getUserId());
    }

    @Override
    @Transactional
    public void lockUser(String userId) {
        mapper.lockUser(userId);
    }

    @Override
    @Transactional
    public void unLockUser(String userId) {
        mapper.unLockUser(userId);
    }

    @Override
    @Transactional
    public void deleteUsers(List<String> ids) {

        mapper.deleteUsers(ids);
    }


    @Override
    @Transactional
    public void toSuper(String id) {

        mapper.toSuper(id);
    }

    @Override // 该密码修改只适合登录后的修改
    @Transactional
    public Map<String,Object> updatePsd(String oldPsd, String newPsd, Boolean isAdmin) {

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("state",1);
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        // 拿到加密盐
        String salt = user.getSalt();
        // 对旧密码和新密码按照shiro方式加密
        String encryOldPsd = utilMethods.psdHash(oldPsd,salt);
        String encryNewPsd = utilMethods.psdHash(newPsd,salt);
        // 校验旧密码
        if(encryOldPsd.equals(user.getPassword())){
            // 修改密码
            mapper.updatePsd(user.getUserId(),encryNewPsd);
        }else {
            result.put("state",2);
            return result;
        }
        return result;

    }

    @Override
    @Transactional
    public void updateRoleAndExp1(String role, int exp, String userId) {

        mapper.updateRoleAndExp1(role,exp,userId);
    }
}

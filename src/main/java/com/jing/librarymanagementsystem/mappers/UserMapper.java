package com.jing.librarymanagementsystem.mappers;

import com.jing.librarymanagementsystem.bean.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Set;

@Mapper
public interface UserMapper {


    // 根据用户名或邮箱查找用户信息
    @Select("select * from `user` where (`user_name`=#{name} or `email`=#{name}) and is_admin=${isAdmin}")
    public User selectUserInfo(String name,int isAdmin);
    // 根据用户id查
    @Select("select * from `user` where `user_id` =#{userId}")
    public User selectUserInfoById(String userId);


    // 获取权限
    @Select("select `resource` from `permissions` where `id` in (select `permissions_id` from `role_permissions` where `role_id` in (select `id` from `role` where `name` in (select `role` from `user` where `user_id`=#{userId})));")
//    // 或者使用内连接
//    @Select("select a.resource from permissions a inner join role_permissions b on b.permissions_id=a.id inner join role c on c.id=b.role_id inner join user d on d.role=c.name and d.user_id=#{userId};")
//    // 等价于内连接
//    @Select("select a.resource from permissions a,role_permissions b,role c,user d where a.id=b.permissions_id and b.role_id=c.id and c.name=d.role and d.user_id=#{userId};")
    public Set<String> selectUserPermissions(String userId);

    // 用户注册
    @Insert("insert into user  values(#{userId},#{userName},#{password},#{email},#{isAdmin},#{adress},#{motto},#{avatarUrl},#{backgroundImageUrl},#{role},#{salt},#{isLock},#{exp},#{isUpdateName})")
    public void userRegistered(User user);

    // 更新用户ip地址
    @Update("update user set adress = #{adress}")
    public void updateAdress(String adress);

    // 锁定账号
    @Update("update user set is_lock=1 where user_id=#{userId}")
    public void lockUser(String userId);

    // 解绑账号
    @Update("update user set is_lock=0 where user_id=#{userId}")
    public void unLockUser(String userId);


    // 注销账号
    @Delete("delete from user where user_id=#{userId}")
    public void deleteUser(String userId);


    // 搜索全部用户
    public List<User> selectAllUser(String userName,String email,Boolean isActive, Boolean isLock,Boolean isAdmin);

    // 只展示拉黑用户
    @Select("select * from user where is_lock=1")
    public List<User> selectAllUserOfLock();

    // 根据id批量删除用户
    public void deleteUsers(List<String> ids);

    // 更新用户角色和经验
    @Update("update user set role=#{role},exp=${exp} where user_id=#{userId}")
    public void updateRoleAndExp1(String role,int exp,String userId);

    // 晋级超级管理员
    @Update("update user set role='超级管理员' where user_id=#{id}")
    public void toSuper(String id);

    // 修改密码
    @Update("update user set password=#{encryPsd} where user_id=#{userId}")
    public void updatePsd(String userId,String encryPsd);


}

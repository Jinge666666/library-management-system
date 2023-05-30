package com.jing.librarymanagementsystem.mappers;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * 个人信息mapper
 * */
@Mapper
public interface PersonalInfoMapper {

    // 如果返回类型不是bean，且多表操作了，那么需要改注解指定一个能代表查询数据的key就行。
    @MapKey("comments_id")
    public List<Map<String, Object>> selectComments(@Param("searchBookName") String searchBookName, @Param("startTime") String startTime, @Param("endTime") String endTime,@Param("userId") String userId);

    // 获取角色名和经验值
    @Select("select name,exp from role where exp!='' or exp=0")
    public List<Map<String, Object>> selectRoleAndExp();

    // 获取某一角色的所有操作型权限
    @Select("select resource from permissions where name like #{roleName}")
    public List<String> selectPermissionsByRole(String roleName);

    // 晋级
    @Update("update user set role = #{roleName} where user_id = #{userId}")
    public void updateLevelUpInfo(@Param("roleName") String roleName,@Param("userId") String userId);

    // 判断昵称是否存在
    @Select("select user_name from user where user_name=#{name}")
    public String isNameExist(String name);

    // 保存头像
    @Update("update user set avatar_url=#{avatar} where user_id=#{userId}")
    public void saveAvatar(@Param("avatar") String avatar,@Param("userId") String userId);

    // 保存用户编辑信息
    public void saveEditInfo(@Param("name") String name,@Param("textarea") String textarea,@Param("bgImgName")String bgImgName,@Param("userId") String userId);
}

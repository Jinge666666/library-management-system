package com.jing.librarymanagementsystem.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import javax.sql.rowset.serial.SerialStruct;
import java.util.List;

/**
 * 权限认证mapper
 * */
@Mapper
public interface PermissionsVerifyMapper {

    // 借阅权限
    @Select("select count(*) from borrow where user_id=#{userId}")
    public int bookBorrowCount(String userId);

    // 收藏权限
    @Select("select count(*) from bookshelf where user_id=#{userId}")
    public int bookMarkCount(String userId);

    // 评论权限
    @Select("select count(*) from comments where user_id=#{userId} and publish_time>= #{time}")
    public int commentsCount(String userId,String time);

    // 获取全部角色
    @Select("select name from role")
    public List<String> getAllRoles();

    // 修改操作型权限 index表示由青铜到王者从1开始间隔为1的下标
    public void editPermission(String role,int index,String collectCeiling,String borrowCeiling,String commentsCeiling,int exp);

    // 修改经验
    @Update("update role set exp=${exp} where name=#{role}")
    public void editExp(String role,int exp);
}

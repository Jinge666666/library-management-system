package com.jing.librarymanagementsystem.service;

import org.springframework.stereotype.Service;

/**
 * 权限验证
 * */

@Service
public interface PermissionsVerifyService {

    // 借书上限验证
    public Boolean bookBorrowedVerify();

    // 收藏上限验证
    public Boolean bookMarkedVerify();

    // 评论上限验证
    public Boolean commentsVerify();


    // 权限修改
    public void editPermission(String role,String collectCeiling,String borrowCeiling,String commentsCeiling,int exp);
}

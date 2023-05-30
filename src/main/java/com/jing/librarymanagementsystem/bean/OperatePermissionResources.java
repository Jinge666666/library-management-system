package com.jing.librarymanagementsystem.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 操作型权限资源解析器，将权限资源对应的字符串解析出目的值
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperatePermissionResources implements Serializable {

    private int operateAddBookPermission; // 收藏图书数上限
    private int operateBorrowBookPermission; // 借阅数上限
    private int operateCommentsPermission;   // 每日评论上限
//    private String[] viewPathPermissions;   // 所有视图类权限

    @Override
    public String toString() {

        return "收藏上限："+this.operateAddBookPermission+",借阅上限："+this.operateBorrowBookPermission+",每日评论上限："+
                this.operateCommentsPermission;
    }
}

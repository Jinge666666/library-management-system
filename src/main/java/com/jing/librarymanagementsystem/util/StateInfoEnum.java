package com.jing.librarymanagementsystem.util;

import java.io.Serializable;

/*
* 自定义状态码,方便与前端交互
* */
public enum StateInfoEnum implements Serializable {
    OK("1"),
    USERNOTEXISTERROR("2"), // 用户不存在
    CAPTCHAERROR("3"),  // 验证码错误
    SERVERERROR("4"), //服务器内部错误
    PSDNOTEXISTERROR("5"),  //密码错误
    ISLOCK("6"); //账号被锁定


    private String name;

    private StateInfoEnum(String name){
        this.name=name;
    }

    @Override
    public String toString() {
        return name;
    }
}

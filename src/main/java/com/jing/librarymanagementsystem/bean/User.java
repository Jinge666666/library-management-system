package com.jing.librarymanagementsystem.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    private String userId;
    private String userName;
    private String password;
    private String email;
    private Boolean isAdmin=false; // 是否是管理员，true表示是
    private String adress; // 地址
    private String motto;  // 座右铭
    private String avatarUrl; // 用户头像
    private String backgroundImageUrl; // 资料墙背景图
    private String role="青铜"; // 用户所属角色，默认是青铜
    private String salt; // 随机加密盐
    private Boolean isLock=false; // 是否被拉黑，true表示被拉黑
    private int exp=0;  // 经验值
    private Boolean isUpdateName=false; // 是否曾经修改过名字

    private String captcha; // 外加一个验证码，方便数据接受
    private Boolean isRemember=false;  //记住我


}

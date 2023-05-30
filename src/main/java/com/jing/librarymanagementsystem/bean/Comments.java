package com.jing.librarymanagementsystem.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

// 评论信息Bean
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comments implements Serializable {

    private int commentsId;
    private String bookId;
    private String userId;
    private String content;
    private String publishTime;
    private int superCommentsId;
    private String userName;
    private String avatarUrl;// 图像
    private String replayUserName;  // 回复的用户名
}

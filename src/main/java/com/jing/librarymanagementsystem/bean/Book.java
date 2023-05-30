package com.jing.librarymanagementsystem.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book  implements Serializable {

    String bookId;
    String bookName;
    String bookAuthor;
    String bookType;
    String clicks="0";
    String wordCount="0";
    String borrowedTimes="0";
    String finish="连载中";
    String bookDesc;
    Boolean isBorrowed=false;
    String bookfaceName="defaultBookface.jpg";
    String tag="[]";
    String favorites="0"; //收藏数
    String comment_times="0";  // 评论数量

    
}

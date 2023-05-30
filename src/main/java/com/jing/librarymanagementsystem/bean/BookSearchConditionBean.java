package com.jing.librarymanagementsystem.bean;


import com.jing.librarymanagementsystem.util.BookSearchOrderConditionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.omg.PortableInterceptor.Interceptor;

import java.io.Serializable;
import java.util.List;

/*
* 此bean是为了按不同条件搜索时统一到一条sql，将所有搜索条件集成，用来和前端交互时封装搜索条件的Bean。
* */

@Data
@AllArgsConstructor
public class BookSearchConditionBean implements Serializable {
    String bookName;
    String bookAuthor;
    List<String> bookType;
    String finish;
    String tag;
    String isBorrowed; // 是否被借阅 0表示未借阅，1表示已被借阅

    String minWordCount; // 最小字数
    String maxWordCount; // 最大字数
    String initial;  //书名首字母

    // 下面三参数也放该类，方便前端传值
    String sortType;  // 排序
    int pageNum; // 分页开始页
    int pageSize; // 每页显示条数

    public BookSearchConditionBean(){
        // 默认搜索条件%%表示全部通过。
        this.bookName=null;
        this.bookAuthor=null;
        this.bookType=null;
        this.finish=null;
        this.tag=null;
        this.isBorrowed=null; // 也能同时获得借阅和未借阅书籍。
        this.minWordCount=  null;
//        this.maxWordCount = String.valueOf(Integer.MAX_VALUE);
        this.maxWordCount = null;
        this.initial = null; // 单词首字母
        this.sortType= BookSearchOrderConditionEnum.CLICK.toString(); // 默认点击量排序
        this.pageNum=0; // 默认第一页，每页20条数据
        this.pageSize=20;

    }


    public void setBookName(String bookName) {
        // 防止前端传null
        if(bookName!=null){
            this.bookName = "%"+bookName +"%";
        }
    }

    public void setBookAuthor(String bookAuthor) {
        if(bookAuthor!=null){
            this.bookAuthor = "%"+bookAuthor +"%";
        }
    }

    public void setBookType(List<String> bookType) {
        this.bookType = bookType;
    }

    public void setFinish(String finish) {
        if(finish!=null){
            this.finish = "%"+finish +"%";
        }
    }

    public void setTag(String tag) {
        if(tag!=null){
            this.tag = "%"+tag +"%";
        }
    }





}

package com.jing.librarymanagementsystem.service;

import com.jing.librarymanagementsystem.bean.Book;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface RankPageService {

    // 排行榜数据公共转化方法，以便和前端参数类型对应 totalType表示总量类型，各个排行榜的总量类型不尽相同
    public List<Map<String,String>> commonBookListConverter(List<Book> bookList,String totalType);

    //排行榜页面相关service
    /**
     * rankByBookType 基于书籍类型的全局排序
     *rankByOrderType 基于order类型
     * start:limit偏移量
     * num:查询数量
     */
    public  List<Map<String,String>> getBookRankPageData(String rankByBookType,String rankByOrderType,int start,int num);

    // 得到上条sql的总量
    public int  FOUND_ROWS();

}

package com.jing.librarymanagementsystem.service;

import com.jing.librarymanagementsystem.bean.Book;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BookFrequencyPageService {

    // 新书推荐 sex字段是区分男频女频页面的关键词
    public List<Book> getBookRecommendBooks(int start, int bookNum,String sex);

    // 编辑推荐
    public List<Book> getBookEditRecommendBooks(int start, int bookNum,String sex);

    // 点击榜
    public List<Book> getClickRankBooks(int start,int bookNum,String sex);

    // 潜力榜
    public List<Book> getQianliRankBooks(int start,int bookNum,String sex);
}

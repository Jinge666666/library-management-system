package com.jing.librarymanagementsystem.service;

import com.jing.librarymanagementsystem.bean.Book;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MainPageService {

    // 新书推荐
    public List<Book> newBookRecommend(int start, int bookNum);

    // 专区
    public List<Book> bookZone(int start,String zoneType);

    // 男生频道，根据书籍类型推荐
    public List<Book> getBookByMaleChannel(List<String> book_type,int bookNum,int start);

    //强推
    public List<Book> getBookByRank(List<String> book_type,int start);
}

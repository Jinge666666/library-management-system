package com.jing.librarymanagementsystem.service;

import com.jing.librarymanagementsystem.bean.Book;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BookShelfService {

    // 添加书籍
    public void addBook();

    // 展示书架
    public List<Book> getBookMark(String orderType,String searchValue);

    // 取消收藏 // bookId须从前端拿
    public void cancelCollect(String bookId);

    // 判断书籍是否已经收藏
    public Boolean getBookOfBookMark();
}

package com.jing.librarymanagementsystem.service.impl;

import com.jing.librarymanagementsystem.bean.Book;
import com.jing.librarymanagementsystem.bean.User;
import com.jing.librarymanagementsystem.mappers.BookShelfMapper;
import com.jing.librarymanagementsystem.service.BookShelfService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class BookShelfServiceImpl implements BookShelfService {

    @Resource
    BookShelfMapper mapper;

    // 收藏书籍
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addBook() {

        Subject subject = SecurityUtils.getSubject();
        // 基于收藏按钮只在详情页触发，使用session的bookId
        String bookId = (String) subject.getSession().getAttribute("bookIdForComments");
        User user = (User) subject.getPrincipal();
        String userId = user.getUserId();
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        mapper.addBook(userId,bookId,currentTime);
    }

    // 书架展示
    @Override
    public List<Book> getBookMark(String orderType,String searchValue) {

        User user = (User) SecurityUtils.getSubject().getPrincipal();
        String userId = user.getUserId();
        return mapper.getBookMark(userId, orderType,searchValue);
    }

    // 取消收藏
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelCollect(String bookId) {

        User user = (User) SecurityUtils.getSubject().getPrincipal();
        String userId = user.getUserId();
        mapper.cancelCollect(bookId,userId);
    }

    @Override
    public Boolean getBookOfBookMark() {

        Subject subject = SecurityUtils.getSubject();
        String bookId = (String) subject.getSession().getAttribute("bookIdForComments");
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        Map<String, Object> bookOfBookMark = mapper.getBookOfBookMark(user.getUserId(), bookId);

        // 查到值，就表名已经收藏了，返回true反之false
        return bookOfBookMark != null;
    }
}

package com.jing.librarymanagementsystem.controller.forefront;

import com.jing.librarymanagementsystem.bean.Book;
import com.jing.librarymanagementsystem.bean.BookSearchConditionBean;
import com.jing.librarymanagementsystem.service.BeanService.BookBeanService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/*
* 书库controller
* */
@Controller
@Slf4j
@RequestMapping("/bookstore")
public class BookStoreController {
    @Autowired
    BookBeanService bookBeanService;

    // 即作为访问书库的路径，又根据路径param 作为其他页面访问书库页的“中转站” 作者、书类型、书关键词
    @RequestMapping
    public String toBookStorePage(
            @RequestParam(name = "author",required = false) String author,
            @RequestParam(name = "bookType",required = false) String bookType,
            @RequestParam(name = "bookTag",required = false) String bookTag
            ){

        return "forefront/bookstore";
    }




}

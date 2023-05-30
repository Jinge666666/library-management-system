package com.jing.librarymanagementsystem.controller.forefront;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jing.librarymanagementsystem.bean.Book;
import com.jing.librarymanagementsystem.bean.BookSearchConditionBean;
import com.jing.librarymanagementsystem.service.BeanService.BookBeanService;
import com.jing.librarymanagementsystem.service.UtilService;
import com.jing.librarymanagementsystem.util.BookSearchOrderConditionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
* 前台公共页面controller，主要是左边侧栏、头部、尾部
* */
@Slf4j
@Controller
public class CommonPageController {

    @Autowired
    BookBeanService bookBeanService;

    @Autowired
    UtilService utilService;



    // 搜索建议controller 搜索所有书籍名和书籍作者
    @ResponseBody
    @RequestMapping(value = "selectAllBookNameAndBookAuthor", method = RequestMethod.POST)
    public Map<String, List<String>> selectBookName(@RequestBody Map<String, String> queryString) {
        String queryStr = queryString.get("queryString");
        Map<String, List<String>> stringHashMap = new HashMap<>();
        List<String> bookNames = new ArrayList<>();
        List<String> bookAuthors = new ArrayList<>();

        // 搜索词为空或只有一个字符，都不给值,前端判断了为空不会走该接口
            queryStr = "%" + queryStr + "%";
            bookNames = bookBeanService.selectBookName(queryStr);
            bookAuthors = bookBeanService.selectBookAuthor(queryStr);
            stringHashMap.put("bookNames", bookNames);
            stringHashMap.put("bookAuthors", bookAuthors);
            return stringHashMap;

    }

    // 搜索引擎controller，可以基于各种条件搜索，主要供书库页面使用
    @ResponseBody
    @RequestMapping(value = "selectBookBySearchWord")
    public Map<String,Object> selectBookBySearchWord(@RequestBody BookSearchConditionBean bookSearchConditionBean){

        //分页 当前页+每页显示的条数 需要设置在查询book语句的前面
        PageHelper.startPage(bookSearchConditionBean.getPageNum(),bookSearchConditionBean.getPageSize());

        // 排序方式有默认值
        String orderTypeStr = bookSearchConditionBean.getSortType();
        // 对带有首字母条件单独处理，须经过过滤，若取反，需要去重，建议转为set后利用removeAll方法去重效率高
        String initial =bookSearchConditionBean.getInitial();
        List<Book> books = null;
        // mybatis使用枚举
//        orderTypeStr = "@com.jing.librarymanagementsystem.util.BookSearchOrderConditionEnum@CLICK.name";
        books = bookBeanService.selectBookBySearchStr(bookSearchConditionBean, orderTypeStr);
        // 含有首字母条件时，过滤
        if(initial!=null){
            books = utilService.bookListFilter(books, initial);
        }

        // 到这里，boos实际条数是分页后的，底层就是物理分页。
        PageInfo<Book> pageInfo = new PageInfo<Book>(books);
//        System.out.println("总条数："+pageInfo.getTotal());
//        System.out.println("总页数："+pageInfo.getPages());
//        System.out.println("当前页："+pageInfo.getPageNum());
//        System.out.println("每页显示长度："+pageInfo.getPageSize());
//        System.out.println("上一页："+pageInfo.getPrePage());
//        System.out.println("下一页："+pageInfo.getNextPage());
//        System.out.println("是否第一页："+pageInfo.isIsFirstPage());
//        System.out.println("是否最后一页："+pageInfo.isIsLastPage());
//        System.out.println(bookSearchConditionBean.toString());
        Map<String, Object> data = new HashMap<>();
        data.put("books",books);
        data.put("total",pageInfo.getTotal());
        data.put("pageSize",pageInfo.getPageSize());
        data.put("pageNum",pageInfo.getPageNum());
        return data;

    }

}

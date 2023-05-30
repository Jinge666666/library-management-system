package com.jing.librarymanagementsystem.controller.forefront;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jing.librarymanagementsystem.bean.Book;
import com.jing.librarymanagementsystem.service.BookShelfService;
import com.jing.librarymanagementsystem.service.PermissionsVerifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
* 书架Controller
* */
@Controller
@Slf4j
@RequestMapping("/bookmark")
public class BookMarkController {

    @Autowired
    BookShelfService bookShelfService;
    @Autowired
    PermissionsVerifyService permissionsVerifyService;

    @RequestMapping
    public String toBookMarkPage(){

        return  "forefront/bookmark";
    }

    // 收藏书籍
    @ResponseBody
//    @Transactional //事务不能嵌套
    @RequestMapping(value ="/bookmarkAddBook", method = RequestMethod.POST)
    public  String addBookMark(){

        String status = "书籍收藏成功，快去书架中看看吧~~~";
        try {
            // 权限验证
            if(!permissionsVerifyService.bookMarkedVerify()){
                return  "您的书架已满，升级到更高段位能获得更多容量";
            }
            try {
                bookShelfService.addBook();
            }catch (Exception e){
                return "书籍收藏操作失败，请稍后再试。。。";
            }
        }catch (Exception e){ // 若权限验证发生异常，添加书籍也不会执行
           return "书籍收藏操作失败，请稍后再试。。。";
        }
        return  status;
    }

    // 展示书架
    @ResponseBody
    @RequestMapping(value = "/bookMarkShow",method = RequestMethod.POST)
    public Map<String,Object> showBookMark(@RequestBody Map<String,Object>body){
        String orderType = (String) body.get("orderType");
        String searchValue = "%"+(String) body.get("searchValue")+"%";
        int pageNum = (int) body.get("pageNum");
        int pageSize = (int) body.get("pageSize");
        // 分页 当前页+每页显示的条数
        PageHelper.startPage(pageNum,pageSize);
        orderType=orderType.replace("=","");
        List<Book> books = bookShelfService.getBookMark(orderType,searchValue);
        PageInfo<Book> pageInfo = new PageInfo<Book>(books);
        long total = pageInfo.getTotal();
        HashMap<String, Object> result = new HashMap<>();
        result.put("books",books);
        result.put("total",total);
        return result;

    }


    // 移除书籍
    @ResponseBody
    @RequestMapping(value = "/cancelCollect",method = RequestMethod.POST)
    public Boolean cancelCollect(@RequestBody String bookId){
        // 前端传的若不是对象，而是字符串的话，由于json，须将等号去除
        bookId = bookId.replace("=","");
        System.out.println(bookId);
        try {
            bookShelfService.cancelCollect(bookId);
        }catch (Exception e){
           return  false;
        }
        return true;
    }

    // 判断书籍是否已收藏
    @ResponseBody
    @RequestMapping(value ="/isCollect",method = RequestMethod.POST)
    public boolean isCollect(){

        return bookShelfService.getBookOfBookMark();
    }
}

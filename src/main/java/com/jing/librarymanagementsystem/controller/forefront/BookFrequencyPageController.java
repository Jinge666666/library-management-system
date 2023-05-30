package com.jing.librarymanagementsystem.controller.forefront;


import com.jing.librarymanagementsystem.bean.Book;
import com.jing.librarymanagementsystem.service.BookFrequencyPageService;
import com.jing.librarymanagementsystem.service.CommonTimerTaskService;
import com.jing.librarymanagementsystem.service.MainPageService;
import com.jing.librarymanagementsystem.service.RankPageService;
import com.jing.librarymanagementsystem.timerTask.CommonTask01;
import com.jing.librarymanagementsystem.util.BookSearchOrderConditionEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.*;

/*
* 男频女频页面controller
* */
@Controller
@Slf4j
@RequestMapping("/frequency")
public class BookFrequencyPageController {

    @Autowired
    CommonTimerTaskService commonTimerTaskService;
    @Resource
    BookFrequencyPageService bookFrequencyPageService;
    @Autowired
    RankPageService rankPageService;

    @Autowired // 热门推荐使用mainPage的就行
    MainPageService mainPageService;

    // 通过sex参数控制显示男频和女频页面
    @RequestMapping
    public String toFrequencyPage(@RequestParam("sex") String sex,HttpSession session) {

        if (sex.equals("male")){
            session.setAttribute("sex","male");
        }
        if (sex.equals("female")){
            session.setAttribute("sex","female");
        }
        return "forefront/book-frequency";
    }


    //新书推荐 根据书籍类型全局搜索，按字数倒序
    @ResponseBody
    @RequestMapping(value = "/newBookCommend",method = RequestMethod.POST)
    public List<Map<String, String>> frequencyPageBookCommend(HttpSession session) {
        String sex = (String) session.getAttribute("sex");
        CommonTask01 frequencyPageBookCommendTask = new CommonTask01();
        if(sex.equals("male")){
            frequencyPageBookCommendTask = commonTimerTaskService.commonTimerTaskService01("maleFrequencyPageBookCommendTask", 300, 8,1000 );
        }
        if(sex.equals("female")){
            frequencyPageBookCommendTask = commonTimerTaskService.commonTimerTaskService01("femaleFrequencyPageBookCommendTask", 100, 8, 400);
        }
        List<Book> books = bookFrequencyPageService.getBookRecommendBooks(frequencyPageBookCommendTask.getStart(), frequencyPageBookCommendTask.getNum(),sex);
        // 转化封装，与前端参数对接
        List<Map<String, String>> datas = new LinkedList<>();
        for (Book book : books) {
            HashMap<String, String> stringStringHashMap = new HashMap<>();
            stringStringHashMap.put("searchByType","/bookstore?bookType="+book.getBookType());
            stringStringHashMap.put("bookType",book.getBookType());
            stringStringHashMap.put("bookDetail","/book/"+book.getBookId());
            stringStringHashMap.put("bookName",book.getBookName());
            stringStringHashMap.put("searchByAuthor","/bookstore?author="+book.getBookAuthor());
            stringStringHashMap.put("bookAuthor",book.getBookAuthor());
            datas.add(stringStringHashMap);
        }
//        log.info("新书推荐:----------------------------"+mainPageCommendTask.getStart());
        return datas;
    }

    // 编辑推荐 根据书籍类型全局搜索，不排序
    @ResponseBody
    @RequestMapping(value = "/editRecommend",method = RequestMethod.POST)
    public List<Book> editRecommend(HttpSession session){
        String sex = (String) session.getAttribute("sex");
        CommonTask01 frequencyPageEditRecommendTask = new CommonTask01();
        if(sex.equals("male")){
            frequencyPageEditRecommendTask = commonTimerTaskService.commonTimerTaskService01("maleFrequencyPageEditRecommendTask", 200, 7, 1800);
        }
        if(sex.equals("female")){
            frequencyPageEditRecommendTask = commonTimerTaskService.commonTimerTaskService01("femaleFrequencyPageEditRecommendTask", 100, 7, 800);
        }
        return bookFrequencyPageService.getBookEditRecommendBooks(frequencyPageEditRecommendTask.getStart(), frequencyPageEditRecommendTask.getNum(),sex);
    }

    // 编辑推荐下面部分，使用编辑推荐的service
    @ResponseBody
    @RequestMapping(value = "/editBottomRecommend",method = RequestMethod.POST)
    public List<Book> editBottomRecommend(HttpSession session){
        String sex = (String) session.getAttribute("sex");
        CommonTask01 frequencyPageEditBottomRecommendTask = new CommonTask01();
        if(sex.equals("male")){
            frequencyPageEditBottomRecommendTask = commonTimerTaskService.commonTimerTaskService01("maleFrequencyPageEditBottomRecommendTask", 100, 6, 1800);
        }
        if(sex.equals("female")){
            frequencyPageEditBottomRecommendTask = commonTimerTaskService.commonTimerTaskService01("femaleFrequencyPageEditBottomRecommendTask", 50, 6, 800);
        }
        return bookFrequencyPageService.getBookEditRecommendBooks(frequencyPageEditBottomRecommendTask.getStart(), frequencyPageEditBottomRecommendTask.getNum(),sex);
    }

    // 点击榜，根据clicks倒排
    @ResponseBody
    @RequestMapping(value = "/frequencyPageClickRank",method = RequestMethod.POST)
    public List<Map<String,String>> clickRank(HttpSession session){
        String sex = (String) session.getAttribute("sex");
        CommonTask01 task = new CommonTask01();
        if(sex.equals("male")){
            task = commonTimerTaskService.commonTimerTaskService01("maleFrequencyPageClickRank", 150, 13, 1000);
        }
        if(sex.equals("female")){
            task = commonTimerTaskService.commonTimerTaskService01("femaleFrequencyPageQianliRank", 70, 13, 400);
        }
        // 对RankBoks转化，对接前端接口
        return rankPageService.commonBookListConverter(bookFrequencyPageService.getClickRankBooks(task.getStart(), task.getNum(), sex), BookSearchOrderConditionEnum.CLICK.toString());
    }

    // 潜力榜，根据clicks顺排
    @ResponseBody
    @RequestMapping(value = "/frequencyPageQianliRank",method = RequestMethod.POST)
    public List<Map<String,String>> QianliRank(HttpSession session){
        String sex = (String) session.getAttribute("sex");
        CommonTask01 task = new CommonTask01();
        if(sex.equals("male")){
            task = commonTimerTaskService.commonTimerTaskService01("maleFrequencyPageQianliRank", 250, 15, 600);
        }
        if(sex.equals("female")){
            task = commonTimerTaskService.commonTimerTaskService01("femaleFrequencyPageQianliRank", 80, 15, 400);
        }
        // 对RankBoks转化，对接前端接口
        return rankPageService.commonBookListConverter(bookFrequencyPageService.getQianliRankBooks(task.getStart(), task.getNum(), sex), BookSearchOrderConditionEnum.CLICK.toString());
    }

    // 男频女频热门推荐调用了mainPage的service接口
    @ResponseBody
    @RequestMapping(value = "/frequencyPageRecommend")
    public List<Book> getBookByMaleChannel(HttpSession session){
        String sex = (String) session.getAttribute("sex");
        CommonTask01 task = new CommonTask01();
        List<String> book_types = new ArrayList<String>();
        if(sex.equals("male")){
            task = commonTimerTaskService.commonTimerTaskService01("maleFrequencyPageRecommend", 120, 6, 1800);
            String[] data={"转世重生","异界大陆","东方玄幻","奇幻修真","古典仙侠","都市异能","现代修真","科幻灵异","虚拟网游"};
             book_types = new ArrayList<>(data.length);
            Collections.addAll(book_types, data);
        }
        if(sex.equals("female")){
            task = commonTimerTaskService.commonTimerTaskService01("femaleFrequencyPageRecommend", 120, 6, 800);
            String[] data={"青春校园","穿越时空","总裁豪门","架空历史","悬疑推理"};
            book_types = new ArrayList<>(data.length);
            Collections.addAll(book_types, data);
        }// 数组转list，支持增删改

        return  mainPageService.getBookByMaleChannel(book_types,task.getNum(), task.getStart());

    }


}

package com.jing.librarymanagementsystem.controller.forefront;

import com.jing.librarymanagementsystem.bean.Book;
import com.jing.librarymanagementsystem.service.CommonTimerTaskService;
import com.jing.librarymanagementsystem.service.MainPageService;
import com.jing.librarymanagementsystem.service.RankPageService;
import com.jing.librarymanagementsystem.timerTask.CommonTask01;
import com.jing.librarymanagementsystem.timerTask.MainPageBookZoneTask;
import com.jing.librarymanagementsystem.timerTask.MainPageCommendTask;
import com.jing.librarymanagementsystem.util.BookSearchOrderConditionEnum;
import com.jing.librarymanagementsystem.util.TimerTaskUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;


/*
* 书城首页controller
* */
@Controller
@Slf4j
@RequestMapping("/home")
public class MainPageController {

    @Autowired
    MainPageService mainPageService;

    @Autowired
    CommonTimerTaskService commonTimerTaskService;

    @Autowired
    RankPageService rankPageService;

    @GetMapping
    public String toMainPage(){

        return "forefront/main";
    }


    // 新书推荐，根据字数较少推荐
    @ResponseBody
    @RequestMapping(value = "/mainPageBookCommend",method = RequestMethod.POST)
    public List<Map<String, String>> mainPageBookCommend(HttpSession session){

//        // 设置任务名：
//        String taskName="mainPageBookCommendTask";
//        // 开始偏移量
//        int start=1000;
//        if(session.getAttribute(taskName)==null){
//            // 内部会自动帮你将一个公共任务放在session，key就是你设定的任务名
//            TimerTaskUtil timerTaskUtil = new TimerTaskUtil(start, 8, 2000,taskName);
//
//        }
//
//        // 内部帮你设定好的公共任务对象
//        CommonTask01 mainPageCommendTask = (CommonTask01) session.getAttribute(taskName);
//        // 判断任务是否一个轮回达到了被停止
//        if(!mainPageCommendTask.getState()){
//            // 检测停止了，那么就是偏移量达到了上限，我们重置它，同时清除session，就可以再次运行
//            mainPageCommendTask.setStart(start);
//            session.setAttribute(taskName,null);
//        }
        // 公共代码放到service层，浓缩为一行代码，大大减少上面重复代码！！！
        CommonTask01 mainPageCommendTask = commonTimerTaskService.commonTimerTaskService01("mainPageBookCommendTask", 1000, 8, 2000);

        List<Book> books = mainPageService.newBookRecommend(mainPageCommendTask.getStart(), mainPageCommendTask.getNum());
        // 封装，与前端参数对接
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

    // 专区
    @ResponseBody
    @RequestMapping(value = "/mainPageBookZone",method = RequestMethod.POST)
    public  List<Map<String, String>> mainPageZone(@RequestBody Map<String,String> body,HttpSession session){
        // 设置定时 大神专区和完本专区的查询数量不同，因此不同共用一个task对象
        CommonTask01 mainPageZoneTask = new CommonTask01();
        if(body.get("zoneType").equals("大神专区")){
            mainPageZoneTask = commonTimerTaskService.commonTimerTaskService01("mainPageBookFirstZoneTask", 0, 7, 4000);
//            log.info("大神专区:----------------------------"+mainPageZoneTask.getStart());
        }
        if(body.get("zoneType").equals("完本专区")){ // 依据完本量计算最大偏移量
            mainPageZoneTask = commonTimerTaskService.commonTimerTaskService01("mainPageBookSecondZoneTask", 0, 7, 1930);
//            log.info("完本专区:----------------------------"+mainPageZoneTask.getStart());
        }


        List<Book> books = mainPageService.bookZone(mainPageZoneTask.getStart(), body.get("zoneType"));

        // 封装，与前端参数对接
        List<Map<String, String>> datas = new LinkedList<>();
        for (Book book : books) {
            HashMap<String, String> stringStringHashMap = new HashMap<>();
            stringStringHashMap.put("imgUrl","images/bookface/"+book.getBookfaceName());
            stringStringHashMap.put("bookDetailUrl","/book/"+book.getBookId());
            stringStringHashMap.put("bookName",book.getBookName());
            stringStringHashMap.put("searchAuthorUrl","/bookstore?author="+book.getBookAuthor());
            stringStringHashMap.put("author",book.getBookAuthor());
            datas.add(stringStringHashMap);
        }
        return datas;
    }

    // 男生频道
    @ResponseBody
    @RequestMapping(value = "/mainPageBookMaleChannel")
    public List<Book> getBookByMaleChannel(HttpSession session){

        // 设置定时
        CommonTask01 mainPageBookMaleChannelTask = commonTimerTaskService.commonTimerTaskService01("mainPageBookMaleChannelTask", 0, 6, 1090);

        String[] data={"转世重生","异界大陆","东方玄幻","奇幻修真","古典仙侠","都市异能","现代修真","科幻灵异","虚拟网游"};
        // 数组转list，支持增删改
        List<String> book_types = new ArrayList<>(data.length);
        Collections.addAll(book_types, data);
//        log.info("男生频道:----------------------------"+mainPageBookMaleChannelTask.getStart());
        return  mainPageService.getBookByMaleChannel(book_types,mainPageBookMaleChannelTask.getNum(), mainPageBookMaleChannelTask.getStart());

    }




    // 女生频道 可直接调用男生频道service
    @ResponseBody
    @RequestMapping(value = "/mainPageBookWomanChannel",method = RequestMethod.POST)
    public List<Book> WomanChannel(){

        // 设置定时 注意最大偏移量要根据你能查到的数据总量确定
        CommonTask01 mainPageBookWomanChannelTask = commonTimerTaskService.commonTimerTaskService01("mainPageBookWomanChannelTask", 0, 6, 870);

        String[] data={"青春校园","穿越时空","总裁豪门","架空历史","悬疑推理"};
        // 数组转list，支持增删改
        List<String> book_types = new ArrayList<>(data.length);
        Collections.addAll(book_types, data);
//        log.info("女生频道:----------------------------"+mainPageBookWomanChannelTask.getStart());
        return  mainPageService.getBookByMaleChannel(book_types, mainPageBookWomanChannelTask.getNum(),mainPageBookWomanChannelTask.getStart());
    }

    // 主页面强推 按书籍类型，偏移量从0开始 而男频女频页的强推榜单，不同的是排序后从0开始
    @ResponseBody
    @RequestMapping(value = "/mainPageBookStrongPush",method = RequestMethod.GET)
    public List<Map<String,String>> mainPageStrongPush(@RequestParam("type") String type){
        CommonTask01 mainPageBookStrongPushTask = new CommonTask01();
        // 数组转list，支持增删改
        List<String> book_types = new ArrayList<>();
        // 设置定时
        if(type.equals("男生强推")){
           mainPageBookStrongPushTask = commonTimerTaskService.commonTimerTaskService01("mainPageBookMaleStrongPushTask", 0, 15, 590);
           String[] data={"转世重生","异界大陆","东方玄幻","奇幻修真","古典仙侠","都市异能","现代修真","科幻灵异","虚拟网游"};
            book_types = new ArrayList<>(data.length);
           Collections.addAll(book_types, data);
            log.info("男生强推:----------------------------"+mainPageBookStrongPushTask.getStart());
        }
        if(type.equals("女生强推")){
            mainPageBookStrongPushTask = commonTimerTaskService.commonTimerTaskService01("mainPageBookWomanStrongPushTask", 0, 15, 370);
            String[] data={"青春校园","穿越时空","总裁豪门","架空历史","悬疑推理"};
            book_types = new ArrayList<>(data.length);
            Collections.addAll(book_types, data);
            log.info("女生强推:----------------------------"+mainPageBookStrongPushTask.getStart());
        }

        List<Book> bookByRank = mainPageService.getBookByRank(book_types, mainPageBookStrongPushTask.getStart());
        // 调用service将数据转化，对接前端数据格式
        return rankPageService.commonBookListConverter(bookByRank, BookSearchOrderConditionEnum.CLICK.toString());

    }
}

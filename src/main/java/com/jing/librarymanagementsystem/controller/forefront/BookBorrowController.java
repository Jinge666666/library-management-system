package com.jing.librarymanagementsystem.controller.forefront;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jing.librarymanagementsystem.bean.Book;
import com.jing.librarymanagementsystem.bean.User;
import com.jing.librarymanagementsystem.mappers.PermissionsVerifyMapper;
import com.jing.librarymanagementsystem.service.BorrowManagementService;
import com.jing.librarymanagementsystem.service.PermissionsVerifyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/*
* 借阅页面Controller
* */
@Controller
@Slf4j
@RequestMapping("/borrow")
public class BookBorrowController {

    @Autowired
    BorrowManagementService borrowManagementService;
    @Autowired // 权限验证
    PermissionsVerifyService permissionsVerifyService;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @RequestMapping
    public String toBookBorrowPage() {

        return "forefront/book-borrowing-info";
    }

    // 借书
    @ResponseBody
    @RequestMapping(value = "/borrowBook",method = RequestMethod.POST)
//    @Transactional(rollbackFor = Exception.class)
    public String borrowBook(HttpSession session){

        String operateStatus = "借书成功，可在我的借阅中查看借书详情";

        /**
         *
         * // 对于事务回滚怎样接着返回response，一个好的办法就是事务还是写在service，然后这里，外层try是捕获非service的controller
        *  // 层异常，这时可算执行成功了，而内层try是捕获service异常，事务肯定会回滚，那么只要事务回滚了，借书操作就算失败，返回失败信息
         *  其实之所以需要内层try，是为了区分是否是service异常还是controller异常，若servie没一异常，那么无论如何，借书操作都算成功的。
         *  但是请注意:若没有执行到service，controller就发生异常，这时也是操作失败的，即内层try的上面部分发生异常，此时可不需要内层try，
         *  但若内层catch下方发生异常，那么就得需要内层try来区分是否是service异常了。
         * */
        try{
            // 判断是否有权限借书 session获取自定义权限对象
            if(!permissionsVerifyService.bookBorrowedVerify()){
                // 如果没有权限，那么拦截器会使其跳到没授权的提示页面，而若有权限，但借阅量达到达到上限,才会来到这
                return  "您当前剩余可借书籍数量为0，提升等级可获得更多的借阅数量哦~~~";
            }
            // 获取bookId
            String bookId = (String) session.getAttribute("bookIdForComments");
            try {
                borrowManagementService.borrowBook(bookId);
            }catch (Exception e) {
                return "借书操作失败，请稍后再试。。。";
            }
        }catch (Exception e){
            e.printStackTrace();
            // 事务不能嵌套，其实只要操作数据库的servie有了事务，事务若没问题，说明插入成功了嘛，controller的问题可以不用回滚，

            return "借书操作失败，请稍后再试。。。";
        }
        // 统计借书量
        stringRedisTemplate.opsForValue().increment("borrowedNum");
        return operateStatus;
    }

    // 还书 bookId不能从session拿，必须从后端具体项拿
    @ResponseBody
    @RequestMapping(value = "/returnBook",method = RequestMethod.POST)
    public String  returnBook(@RequestBody Map<String,String> body){

        String operateStatus = "图书已归还成功！！！";
        try {

            int borrowId =  Integer.parseInt(body.get("borrowId"));
            String bookId = body.get("bookId");
            try {
                borrowManagementService.returnBook(borrowId,bookId);
            }catch (Exception e){
                e.printStackTrace();
                return  "系统繁忙，请稍后再试。。。";
            }
        }catch (Exception e){
            e.printStackTrace();
            return "系统繁忙，请稍后再试。。。";
        }
       return operateStatus;
    }

    //我的借阅展示书籍
    @ResponseBody
    @RequestMapping(value ="/showBookBorrowed",method = RequestMethod.POST)
    public Map<String,Object> showBookBorrowed(@RequestBody Map<String,Object> data) {
        int pageNum = (int) data.get("pageNum");
        String searchValue ="%" +(String) data.get("searchValue")+"%";
        System.out.println(pageNum);
        // 分页 当前页+每页显示的条数 需要设置在查询book语句的前面 不控制每页显示条数，因此pageSize固定为2
        PageHelper.startPage(pageNum,2);
        // 获取用户id
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        String userId = user.getUserId();
        List<Map<String,Object>> books = borrowManagementService.showBookBorrowed(userId,searchValue);
        // 时间格式化下
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Map<String,Object> book : books){
            Date date1 = (Date)book.get("borrow_time");
            Date date2 = (Date)book.get("return_time");
            book.put("borrow_time",sf.format(date1));
            book.put("return_time",sf.format(date2));
        }
        PageInfo<Map<String,Object>> pageInfo = new PageInfo<Map<String,Object>>(books);
        long total = pageInfo.getTotal();
        HashMap<String, Object> result = new HashMap<>();
        result.put("books",books);
        result.put("total",total);
        return result;
    }

    // 查询该书是否被借阅以及返回借阅时间,这由vue初始化钩子调用
    @ResponseBody
    @RequestMapping("/showBorrowReturnTime")
    public Map<String, Object> showBorrowReturnTime(HttpSession session){

        User user = (User) SecurityUtils.getSubject().getPrincipal();
        // 该操作只在书籍详情页，可在session拿到当前书籍id
        String bookId = (String) session.getAttribute("bookIdForComments");
        // 查询一条数据，用map接受，且注意，尤其有时间字段的，map的v一定是Object类型，且先将时间转为Date，再转字符串。
        Map<String,Object> data = borrowManagementService.showBorrowReturnTime(bookId);

        if(data==null){ // 没人借阅此书
            HashMap<String, Object> map = new HashMap<>();
            map.put("state", "3");
            return map;
        }else { // 您已经借阅
            Date date = (Date) data.get("return_time");
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if(data.get("user_id").equals(user.getUserId())){
                data.put("state", "2");
            }else {
                // 别人已经借阅
                data.put("state", "1");
            }
            data.put("borrow_time",sf.format(date));
        }


        return data;

    }
}

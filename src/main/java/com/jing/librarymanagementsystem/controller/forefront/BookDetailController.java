package com.jing.librarymanagementsystem.controller.forefront;

import com.jing.librarymanagementsystem.bean.Book;
import com.jing.librarymanagementsystem.bean.Comments;
import com.jing.librarymanagementsystem.bean.User;
import com.jing.librarymanagementsystem.service.BeanService.BookBeanService;
import com.jing.librarymanagementsystem.service.BookDetailService;
import com.jing.librarymanagementsystem.service.PermissionsVerifyService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class BookDetailController {

    @Autowired
    BookBeanService bookBeanService;
    @Autowired
    BookDetailService bookDetailService;
    @Autowired // 授权
    PermissionsVerifyService permissionsVerifyService;

    // 书籍详情页
    @RequestMapping("/book/{bookId}")
    public String toBookDetail(@PathVariable("bookId") String bookId, Model model, HttpSession session){

        Book book =bookBeanService.selectBookById(bookId);
        // 由于el中字符串无法与变量拼接：书籍id改为路径
        // 封面名更改为路径
        model.addAttribute("bookfaceUrl","/images/bookface/"+book.getBookfaceName());

        // tagUrlPre 由于el变量无法与字符串拼接，只好将tag访问前缀放在request中
        model.addAttribute("tagUrlPre","/bookstore?bookTag=");
        // bookTypeUrl
        model.addAttribute("bookTypeUrl","/bookstore?bookType="+book.getBookType());

        // 将字符串型标签tag解析为数组,放入model中
        Matcher matcher = Pattern.compile("'[\\u4E00-\\u9FA5]*'").matcher(book.getTag());
        LinkedList<String> tags = new LinkedList<>();
        while (matcher.find()){
            tags.add(matcher.group(0).replace("'",""));
        }
        model.addAttribute("book",book);
        model.addAttribute("tags",tags);

        // session中设置一个bookId，供获取评论接口使用
        session.setAttribute("bookIdForComments",book.getBookId());

        return "forefront/bookDetail";
    }

    // axios访问该controller得到评论
    @ResponseBody
    @RequestMapping(value = "/selectComments",method = RequestMethod.POST)
    public List<Map<String, Object>> getComments (HttpSession session){

        String bookId = (String) session.getAttribute("bookIdForComments");
        return  bookDetailService.getComments(bookId);
    }

    // 提交评论存储并查询评论返回
    @ResponseBody
    @RequestMapping(value = "/submitComments",method = RequestMethod.POST)
    // Object可替代任何返回类型，如List<Map<String, Object>>，axios接受参数不会受影响，会自动解析
    public Object submitComments(@RequestBody Comments comments, HttpSession session) {

        String bookId;
        try {
            // 判断是否有评论权限，判断评论量是否达到上限
            if(!permissionsVerifyService.commentsVerify()){
                return "您今日评论数量已达上限，请明日再来评论吧~~~";
            }
            Subject subject = SecurityUtils.getSubject();
            // 我们需要用到用户id，subject拿
            User user = (User) subject.getPrincipal();
            comments.setUserId(user.getUserId());
            // 重新获取数据响应
            bookId = (String) session.getAttribute("bookIdForComments");
            // 设置bookId
            comments.setBookId(bookId);
            //存储
            try {
                bookDetailService.setComments(comments);
            }catch (Exception e){
                e.printStackTrace();
                return "系统繁忙，请稍后再提交。。。";
            }
        }catch (Exception e){
            e.printStackTrace();
            return "系统繁忙，请稍后再提交。。。";

        }


        return  bookDetailService.getComments(bookId);
    }
}

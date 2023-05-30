package com.jing.librarymanagementsystem.controller.backstage;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jing.librarymanagementsystem.bean.Book;
import com.jing.librarymanagementsystem.bean.User;
import com.jing.librarymanagementsystem.mappers.BookMapper;
import com.jing.librarymanagementsystem.service.BeanService.BookBeanService;
import com.jing.librarymanagementsystem.util.UtilMethods;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;

@Controller
@RequestMapping("/backstage")
public class libraryInfoController {


    // 项目相对路径
    public final static String UPLOAD_PATH_PREFIX = "src/main/resources/static";
    // 书籍封面相对static的目录路径
    @Value("${pro.bookfaceUploadPath}")
    private String uploadPath;

    @Autowired
    UtilMethods utilMethods;

    @Resource
    BookBeanService bookBeanService;

    @RequestMapping("toLibraryInfo")
    public String toLibraryInfo(){

        return "/backstage/library-information-management";
    }
    @RequestMapping("toLibraryType")
    public String toLibraryType(){

        return "/backstage/library-type-management";
    }


    @ResponseBody // 书籍展示
    @RequestMapping(value ="/bookInfo" ,method = RequestMethod.POST)
    public Map<String,Object> selectAllBooks(@RequestBody Map<String,Object> body){

        String bookName = (String) body.get("bookName");
        String bookType = (String) body.get("bookType");
        String bookAuthor= (String) body.get("bookAuthor");
        int pageNum = (int) body.get("pageNum");
        int pageSize = (int) body.get("pageSize");
        PageHelper.startPage(pageNum,pageSize);
        List<Book> books = bookBeanService.selectAllBook(bookName,bookType,bookAuthor);
        PageInfo<Book> bookPageInfo = new PageInfo<>(books);
        int total = (int) bookPageInfo.getTotal();
        HashMap<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("books", books);
        System.out.println(result.toString());
        return result;
    }


    // 所有类型
    @ResponseBody
    @RequestMapping(value = "/getAllBookType",method = RequestMethod.POST)
    public Map<String, Object> getAllBooks(@RequestBody(required = false) Map<String,Object> body){

        HashMap<String, Object> data = new HashMap<>();
        if(body==null){
            data.put("bookTypes",bookBeanService.selectAllType("%%"));
            return data;
        }else {
            String searchKey = "%"+(String) body.get("searchKey")+"%";
            int pageNum = (int)body.get("pageNum");
            int pageSize = (int) body.get("pageSize");
            PageHelper.startPage(pageNum,pageSize);
            List<Map<String, String>> result = bookBeanService.selectAllType(searchKey);
            PageInfo<Map<String, String>> page = new PageInfo<>(result);
            data.put("bookTypes",result);
            data.put("total",page.getTotal());
            return data;

        }

    }

    // 所有tag
    @ResponseBody
    @RequestMapping(value = "/getAllBookTag",method = RequestMethod.POST)
    public List<String> getAllBookTags(){

        return bookBeanService.selectAllBookTag(30);
    }


    // 封面上传
    @ResponseBody
    @RequestMapping(value = "/bookfaceUpload",method = RequestMethod.POST)
    public Map<String, Object> bookfaceUpload(@RequestPart("bookface")MultipartFile bookfaceImg){

        Map<String, Object> map = new HashMap<>();
        map.put("msg", "上传成功");

        if (bookfaceImg.isEmpty()) {
            map.put("msg", "不能上传空文件");
            return map;
        }

        // 获取上传原始文件名
        String fileName = bookfaceImg.getOriginalFilename();
        if ("".equals(fileName)) {
            map.put("msg", "文件名不能为空");
            return map;
        }

        File readPath = new File(UPLOAD_PATH_PREFIX + uploadPath);
        // 使用uuid生成新文件名
        String newFileName = UUID.randomUUID().toString().replaceAll("-", "") + fileName.substring(fileName.lastIndexOf("."), fileName.length());
        // 文件真实的保存路径
        File file = new File(readPath.getAbsolutePath() + File.separator + newFileName);
        try {
            // 存
            bookfaceImg.transferTo(file);
            System.out.println(newFileName);
            // 返还文件名
           map.put("newBookface",newFileName);
//            // 刷新subject和session的用户数据
//            updateShiroAuthenticationAndAuthorization.AuthenticationInfoUpdateUtil();

        } catch (IOException e) {
            e.printStackTrace();
            map.put("msg", "上传失败，请稍后再试。");
        }

        return map;


    }

    // 修改图书提交
    @ResponseBody
    @RequestMapping("/handleUpdateBookInfo")
    public Boolean handleUpdateBookInfo(@RequestBody Map<String,String> body) {

        try {
            String bookType = body.get("bookType");
            String tagStr = body.get("tag");
            String bookfaceName = body.get("bookfaceName");
            String bookId = body.get("bookId");
            System.out.println(bookfaceName);
            System.out.println(tagStr);
            System.out.println(bookType);
            System.out.println(bookId);
            bookBeanService.updateBookInfo(bookId, bookType, bookfaceName, tagStr);
        } catch (Exception e) {
            e.printStackTrace();
            return false;

        }
        return true;

    }

    // 删除书籍
    @ResponseBody
    @RequestMapping("/deleteBook")
    public Boolean deleteBook(@RequestBody List<String> bookIds) {

        try {
            bookBeanService.deleteBook(bookIds);
        }catch (Exception e) {
            return false;
        }
        return true;
    }

    // 添加图书
    @ResponseBody
    @RequestMapping(value = "/addBook",method = RequestMethod.POST)
    public Boolean addBook(@RequestBody Book book){

        try {
            // 生成booId
            String bookId = utilMethods.growBookId();
            book.setBookId(bookId);
            System.out.println("=========================================");
            System.out.println(book.toString());
            try {
                bookBeanService.addBook(book);
            }catch (Exception e) {
               e.printStackTrace();
               return false;
            }
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // 添加图书类型
    @ResponseBody
    @RequestMapping(value = "/addBookType",method = RequestMethod.POST)
    public Boolean addBookType(@RequestBody Map<String,String> body){

        try {
            String bookType=body.get("bookType");
            String bookDesc=body.get("bookDesc");
            bookBeanService.addBookType(bookType,bookDesc);
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    // 修改图书类型
    @ResponseBody
    @RequestMapping(value = "/editBookType",method = RequestMethod.POST)
    public Boolean editBookType(@RequestBody Map<String,String> body){

        try {
        String bookType=body.get("bookType");
        String typeDesc=body.get("bookDesc");
        String id=body.get("id");
        bookBeanService.editBookType(bookType,typeDesc,id);
    }catch (Exception e) {
        e.printStackTrace();
        return false;
    }
        return true;
    }

    // 删除书籍类型
    @ResponseBody
    @RequestMapping(value = "/deleteBookType",method = RequestMethod.POST)
    public Boolean deleteBookType(@RequestBody Map<String,List<Integer>> body){

        List<Integer> ids = body.get("ids");
        try {
            bookBeanService.deleteBookType(ids);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }
}

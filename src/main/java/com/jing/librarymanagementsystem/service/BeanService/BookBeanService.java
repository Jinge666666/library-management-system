package com.jing.librarymanagementsystem.service.BeanService;

import com.jing.librarymanagementsystem.bean.Book;
import com.jing.librarymanagementsystem.bean.BookSearchConditionBean;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface BookBeanService {

    // 查询全部书籍名
    public List<String> selectBookName(String bookName);

    // 查询书籍作者
    public List<String> selectBookAuthor(String bookAuthor);

    // 通过搜索词对书籍名和作者名模糊查询，查询书籍全部数据
    public List<Book> selectBookBySearchStr(BookSearchConditionBean searchCondition, String orderCondition);

    // 通过id查询书籍
    public Book selectBookById(String bookId);


    // 查询全部图书
    public List<Book> selectAllBook(String bookName,String bookType,String bookAuthor);

    // 查找全部类型
    public List<Map<String,String>> selectAllType( String searchkey);
    // 查找book_type 表内容
    public List<Map<String,String>> selectTableAllType(String searchkey);
    // 查找书籍全部tag
    public List<String> selectAllBookTag(int num);

    // 根据id批量删除
    public void deleteBook(List<String> ids);

    // 编辑图书
    public void updateBookInfo(String id,String bookType,String bookfaceName,String tag);

    // 添加书籍
    public void addBook(Book book);

    // 添加书籍类型
    public void addBookType(String bookType,String bookDesc);

    // 修改图书类型
    public void editBookType(String bookType,String bookDesc,String id);

    // 删除书籍类型
    public void deleteBookType(List<Integer> id);

}

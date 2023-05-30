package com.jing.librarymanagementsystem.mappers;

import com.jing.librarymanagementsystem.bean.Book;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface BookShelfMapper {

    // 收藏书籍
    @Insert("insert into bookshelf values (#{userId},#{bookId},#{createTime})")
    public void addBook(String userId,String bookId,String createTime);

    // 获得收藏书籍
    public List<Book> getBookMark(@Param("userId") String userId, @Param("orderType") String orderType,@Param("searchValue") String searchValue);


    // 取消收藏
    @Delete("delete from bookshelf where user_id=#{userId} and book_id=#{bookId}")
    public void cancelCollect(String bookId,String userId);

    // 搜索单条书籍
    @Select("select * from bookshelf where book_id=#{bookId} and user_id=#{userId}")
    public Map<String, Object> getBookOfBookMark(@Param("userId") String userId, @Param("bookId") String bookId);
}

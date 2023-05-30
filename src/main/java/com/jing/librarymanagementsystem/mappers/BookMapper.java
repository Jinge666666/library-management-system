package com.jing.librarymanagementsystem.mappers;

import com.jing.librarymanagementsystem.bean.Book;
import com.jing.librarymanagementsystem.bean.BookSearchConditionBean;
import com.jing.librarymanagementsystem.bean.User;
import com.jing.librarymanagementsystem.util.BookSearchOrderConditionEnum;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
public interface BookMapper {
    // 中文全文检索
//    @Select("select `book_name`, MATCH (`book_name`) AGAINST ('诚实') as score from `book_info` where MATCH (`book_name`) AGAINST ('诚实' IN NATURAL LANGUAGE MODE);")
    // 查书籍名-搜索推荐
    @Select("select `book_name` from `book_info` where book_name like #{book_name} order by clicks desc limit 20")
    public List<String> selectBookName(String bookName);

    // 查询书籍作者-搜索推荐
    @Select("select `book_author` from `book_info` where `book_author` like #{book_author}")
    public List<String> selectBookAuthor(String bookAuthor);

    // 查询所有书籍，可根据不同条件和排序条件查书
    public List<Book> selectBookBySearchStr(@Param("bo1") BookSearchConditionBean searchCondition, @Param("bo2") String orderCondition);

    // 书籍详情页 根据id查询
    @Select("select * from `book_info` where `book_id` =#{bookId}")
    public Book selectBookById(String bookId);

//    // 根据书籍tag关键词查询
//    @Select("select  book_id,book_name,book_author, book_type,clicks,word_count,finish,book_desc,is_borrowed,bookface_name from book_info where tag like #{tag}")
//    public List<Book> selectBookByTag(String tag);

    // 查询全部图书,可按条件
    public List<Book> selectAllBook(String bookName,String bookType,String bookAuthor);



    // 查找全部书籍类型+描述
    @Select("select book_type_id,book_type_name,book_type_desc from book_type where book_type_name like #{searchKey} or  book_type_desc like #{searchKey}")
    public List<Map<String,String>> selectTableBookType(String searchkey);

    // 查找书籍全部tag
    @Select("select distinct tag from book_tag group by tag order by count(*) desc limit ${num}")
    public List<String> selectAllBookTag(int num);

    // 根据id批量删除
    public void deleteBook(List<String> ids);

    // 编辑图书 类型、封面、tag
    public void updateBookInfo(String id,String bookType,String bookfaceName,String tag);

    // 添加图书
    @Insert("insert into book_info values(#{bookId},#{bookName},#{bookAuthor},#{bookType},#{clicks},#{wordCount},#{borrowedTimes},#{finish},#{bookDesc},#{isBorrowed},#{bookfaceName},#{tag},#{favorites},#{comment_times})")
    public void addBook(Book book);

    // 添加书籍类型
    @Insert("insert into book_type values(null,#{bookType},#{bookDesc})")
    public void addBookType(String bookType,String bookDesc);

    // 修改书籍类型
    @Update("update book_type set book_type_name=#{bookType},book_type_desc=#{bookDesc} where book_type_id=#{id}")
    public void updateBookType(String bookType,String bookDesc,String id);

    // 删除书籍类型
    public void deleteBookType(List<Integer> id);

}

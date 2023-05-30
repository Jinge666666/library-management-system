package com.jing.librarymanagementsystem.mappers;

import com.jing.librarymanagementsystem.bean.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MainPageMapper {

    // 新书推荐
    @Select("select  book_id,book_name,book_author, book_type ,bookface_name from book_info order by word_count limit #{start},#{bookNum}")
    public List<Book> newBookRecommend(int start,int bookNum);

    // 专区 大神专区：依据点击量 完本专区:完本的都行
    public List<Book> ZoneType(@Param("start") int start, @Param("zoneType") String zoneType);

    // 频道，根据书籍类型推荐
    public List<Book> selectBookByMaleChannel(@Param("bookType") List<String> book_type,@Param("bookNum") int bookNum,@Param("start") int start);

    // 强推
    public List<Book> selectBookRankMale(@Param("book_type") List<String> book_type,@Param("start") int start);

}

package com.jing.librarymanagementsystem.mappers;

import com.jing.librarymanagementsystem.bean.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BookFrequencyMapper {

    // 新书推荐
    public List<Book> newBookRecommend(@Param("start") int start, @Param("bookNum") int bookNum, @Param("sex") String sex);

    // 编辑推荐
    public List<Book> bookEditRecommend(@Param("start") int start, @Param("bookNum") int bookNum, @Param("sex") String sex);

    //点击榜
    public List<Book> clicksRank(@Param("start") int start, @Param("bookNum") int bookNum, @Param("sex") String sex);

    // 潜力榜
    public List<Book> qianliRank(@Param("start") int start, @Param("bookNum") int bookNum, @Param("sex") String sex);
}

package com.jing.librarymanagementsystem.mappers;

import com.jing.librarymanagementsystem.bean.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RankMapper {


    public List<Book> selectForBookRank(@Param("rankByBookType") String rankByType, @Param("rankByOrderType") String rankByOrderType, @Param("start") int start, @Param("num") int num);

    // 该条sql专统计上条sql带有SQL_CALC_FOUND_ROWS的总数量，且不受limit影响，并且执行的实际是一条sql，这可以帮我们自动算出上面sql执行时的最大偏移量
    @Select(" SELECT FOUND_ROWS() as total")
    public int FOUND_ROWS();

}

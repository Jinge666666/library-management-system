package com.jing.librarymanagementsystem.mappers;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface BorrowMapper {

    // 存储借阅信息

    @Insert("insert into borrow values(null,#{userId},#{bookId},#{borrowTime},#{returnTime})")
    public void saveBorrowInfo(String userId,String bookId,String borrowTime,String returnTime);


    // 查询用户所有借阅信息
    @Select("select a.book_id,a.bookface_name,a.book_name,b.borrow_id,b.borrow_time,b.return_time from book_info a inner join borrow b on a.book_id=b.book_id where b.user_id=#{userId} and a.book_name like #{searchValue}")
    public List<Map<String,Object>> selectBorrowInfo(String userId,String searchValue);

    // 归还图书，删除借阅表某记录
    @Delete("delete from borrow where borrow_id=#{borrowId}")
    public void returnBook(int borrowId);

    // 修改图书是否借阅状态 0表示未被借阅，1表示已借阅
    @Update("update book_info set is_borrowed=#{state} where book_id=#{bookId}")
    public void updateBookBorrowState(String bookId,int state);

    // 查询已被借阅书籍的还书时间  bookId和userId联合能替代borrow_id
    @Select("select user_id,return_time from borrow where book_id=#{bookId}")
    public Map<String,Object> selectBorrowReturnTime(String bookId);


    // 查询所有用户借阅信息
    @MapKey("user_name")
    public List<Map<String,Object>> selectAllBorrowInfo(String bookName,String userName,boolean isOverdue);

}

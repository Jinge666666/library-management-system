package com.jing.librarymanagementsystem.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface BorrowManagementService {

    // 借书
    public void borrowBook(String bookId);

    // 还书
    public void returnBook(int borrowId,String bookId);

    // 展示所有已借书籍信息
    public List<Map<String,Object>> showBookBorrowed(String userId,String searchValue);

    // 显示已被借阅书籍的还书时间
    public Map<String, Object> showBorrowReturnTime(String bookId);


    // 查询所有借阅信息
    List<Map<String,Object>> selectAllBorrowInfo(String bookName,String userName,boolean isOverdue);

    public void batchReturnBook(List<Map<String,Object>> datas);
}

package com.jing.librarymanagementsystem.service.BeanService.impl;

import com.jing.librarymanagementsystem.bean.Book;
import com.jing.librarymanagementsystem.bean.BookSearchConditionBean;
import com.jing.librarymanagementsystem.mappers.BookMapper;
import com.jing.librarymanagementsystem.service.BeanService.BookBeanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class BookBeanServiceImpl implements BookBeanService {
    @Resource
    BookMapper bookMapper;

    @Override
    public List<String> selectBookName(String bookName) {
        return bookMapper.selectBookName(bookName);
    }

    @Override
    public List<String> selectBookAuthor(String bookAuthor) {
        return bookMapper.selectBookAuthor(bookAuthor);
    }

    @Override
    public List<Book> selectBookBySearchStr(BookSearchConditionBean searchCondition, String orderCondition) {
        return bookMapper.selectBookBySearchStr(searchCondition,orderCondition);
    }

    @Override
    public Book selectBookById(String bookId) {
        return bookMapper.selectBookById(bookId);
    }

    @Override
    public List<Book> selectAllBook(String bookName,String bookType,String bookAuthor) {

        if(bookName!=null){
            bookName = "%" + bookName +"%";
        }else {
            bookName = "%%";
        }
        if(bookType!=null){
            bookType = "%" + bookType +"%";
        }else {
            bookType="%%";
        }
        if(bookAuthor!=null){
            bookAuthor = "%" + bookAuthor +"%";
        }else {
            bookAuthor="%%";
        }
        return bookMapper.selectAllBook(bookName,bookType,bookAuthor);
    }

    @Override
    public List<Map<String,String>> selectAllType(String searchkey) {
        return bookMapper.selectTableBookType(searchkey);
    }

    @Override
    public List<Map<String, String>> selectTableAllType(String searchkey) {
        return bookMapper.selectTableBookType(searchkey);
    }

    @Override
    public List<String> selectAllBookTag(int num) {
        return bookMapper.selectAllBookTag(num);
    }

    @Override
    @Transactional
    public void deleteBook(List<String> ids) {

        bookMapper.deleteBook(ids);
    }

    @Override
    @Transactional
    public void updateBookInfo(String id, String bookType, String bookfaceName, String tag) {
        bookMapper.updateBookInfo(id, bookType, bookfaceName, tag);
    }

    @Override
    @Transactional
    public void addBook(Book book) {
        bookMapper.addBook(book);
    }

    @Override
    @Transactional
    public void addBookType(String bookType, String bookDesc) {

        bookMapper.addBookType(bookType, bookDesc);
    }

    @Override
    @Transactional
    public void editBookType(String bookType, String bookDesc, String id) {

        bookMapper.updateBookType(bookType,bookDesc,id);
    }

    @Override
    @Transactional
    public void deleteBookType(List<Integer> id) {

        bookMapper.deleteBookType(id);
    }
}

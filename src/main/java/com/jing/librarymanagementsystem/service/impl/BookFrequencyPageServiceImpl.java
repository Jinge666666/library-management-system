package com.jing.librarymanagementsystem.service.impl;

import com.jing.librarymanagementsystem.bean.Book;
import com.jing.librarymanagementsystem.mappers.BookFrequencyMapper;
import com.jing.librarymanagementsystem.service.BookFrequencyPageService;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BookFrequencyPageServiceImpl implements BookFrequencyPageService {

    @Resource
    BookFrequencyMapper bookFrequencyMapper;

    @Override
    public List<Book> getBookRecommendBooks(int start, int bookNum, String sex) {
        return bookFrequencyMapper.newBookRecommend(start, bookNum, sex);
    }

    @Override
    public List<Book> getBookEditRecommendBooks(int start, int bookNum, String sex) {
        return bookFrequencyMapper.bookEditRecommend(start, bookNum, sex);
    }

    @Override
    public List<Book> getClickRankBooks(int start, int bookNum, String sex) {
        return bookFrequencyMapper.clicksRank(start, bookNum, sex);
    }

    @Override
    public List<Book> getQianliRankBooks(int start, int bookNum, String sex) {
        return bookFrequencyMapper.qianliRank(start,bookNum,sex);
    }
}

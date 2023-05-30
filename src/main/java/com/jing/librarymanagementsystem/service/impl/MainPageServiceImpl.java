package com.jing.librarymanagementsystem.service.impl;

import com.jing.librarymanagementsystem.bean.Book;
import com.jing.librarymanagementsystem.mappers.MainPageMapper;
import com.jing.librarymanagementsystem.service.MainPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MainPageServiceImpl implements MainPageService {
    @Resource
    MainPageMapper mainPageMapper;

    @Override
    public List<Book> newBookRecommend(int start, int bookNum) {

        return  mainPageMapper.newBookRecommend(start,bookNum);
    }

    @Override
    public List<Book> bookZone(int start, String zoneType) {
        return mainPageMapper.ZoneType(start,zoneType);
    }

    @Override
    public List<Book> getBookByMaleChannel(List<String> book_type, int bookNum,int start) {
        return mainPageMapper.selectBookByMaleChannel(book_type,bookNum,start);
    }

    @Override
    public List<Book> getBookByRank(List<String> book_type, int start) {
        return mainPageMapper.selectBookRankMale(book_type,start);
    }
}

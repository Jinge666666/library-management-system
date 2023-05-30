package com.jing.librarymanagementsystem.service.impl;

import com.jing.librarymanagementsystem.bean.Book;
import com.jing.librarymanagementsystem.mappers.RankMapper;
import com.jing.librarymanagementsystem.service.RankPageService;
import com.jing.librarymanagementsystem.util.BookSearchOrderConditionEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RankPageServiceImpl implements RankPageService {
    @Autowired
    RankMapper rankMapper;

    @Override
    public List<Map<String, String>> commonBookListConverter(List<Book> bookList, String totalType) {

        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        for (Book book : bookList) {
            Map<String, String> bookInfo = new HashMap<String, String>();
            bookInfo.put("id",book.getBookId());
            bookInfo.put("bookName",book.getBookName());
            bookInfo.put("toUrl","/book/"+book.getBookId());
            switch (totalType){
                case "clicks":
                    bookInfo.put("total",book.getClicks());
                    break;
                case "word_count":
                    bookInfo.put("total",book.getWordCount());
                    break;
                case "favorites":
                    bookInfo.put("total",book.getFavorites());
                    break;
                case "borrowed_times":
                    bookInfo.put("total",book.getBorrowedTimes());
                    break;
                case "comment_times":
                    bookInfo.put("total",book.getComment_times());
                    break;
            }
            bookInfo.put("typeUrl","/bookstore?bookType="+book.getBookType());
            bookInfo.put("bookType",book.getBookType());
            bookInfo.put("authorUrl","/bookstore?author="+book.getBookAuthor());
            bookInfo.put("author",book.getBookAuthor());
            bookInfo.put("imgUrl","images/bookface/"+book.getBookfaceName());
            result.add(bookInfo);
        }
        return result;
    }

    @Override
    public List<Map<String,String>> getBookRankPageData(String rankByBookType, String rankByOrderType, int start, int num) {
        System.out.println(rankByBookType+rankByOrderType);
        List<Book> books = rankMapper.selectForBookRank(rankByBookType, rankByOrderType, start, num);
        // 定时任务中如果从原始最大偏移量突然转到实际小于它的总量的sql，那么会造成空白，在此处理下
//        if(books.size()<num){
//            books = rankMapper.selectForBookRank(rankByBookType, rankByOrderType, 0, num);
//        }
        return this.commonBookListConverter(books,rankByOrderType);
    }

    @Override
    public int FOUND_ROWS() {
        return rankMapper.FOUND_ROWS();
    }
}

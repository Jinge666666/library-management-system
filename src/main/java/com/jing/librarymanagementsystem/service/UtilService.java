package com.jing.librarymanagementsystem.service;

import com.jing.librarymanagementsystem.bean.Book;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Service
public interface UtilService {

    String createCaptcha();

    // 此方法是将验证码图片转为数据流响应出去，从而实现切换验证码图
    void outputVerifyCodeImg(OutputStream out) throws IOException;

    // 对book列表对象基于书籍名首字母过滤 initial表示首字母条件
    List<Book> bookListFilter(List<Book> bookList,String initial);
}

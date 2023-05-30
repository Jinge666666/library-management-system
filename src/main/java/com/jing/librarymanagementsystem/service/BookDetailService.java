package com.jing.librarymanagementsystem.service;

import com.jing.librarymanagementsystem.bean.Comments;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface BookDetailService {

    public List<Map<String, Object>> getComments(String bookId);

    public void setComments(Comments comments);
}

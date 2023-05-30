package com.jing.librarymanagementsystem.service.impl;

import com.jing.librarymanagementsystem.bean.Comments;
import com.jing.librarymanagementsystem.mappers.BookDetailMapper;
import com.jing.librarymanagementsystem.service.BookDetailService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class BookDetailServiceImpl implements BookDetailService {

    @Resource
    BookDetailMapper bookDetailMapper;

    // 查到书籍下全部评论
    @Override
    public List<Map<String, Object>> getComments(String bookId) {

        List<Comments> data=bookDetailMapper.selectComments(bookId);
        // 得到评论数
        String size = String.valueOf(data.size());
        Map<String, Object> sizeMap = new HashMap<>();
        sizeMap.put("size",size);

        List<Map<String, Object>> result = new ArrayList<>();
        // 对数据转化 mysql已对数据按照时间倒序
        for (Comments comment : data) {
            Map<String, Object> commentGroup = new HashMap<>();
            // 一级评论区
            if(comment.getSuperCommentsId()==0){
                commentGroup.put("superComments",comment);
                // 获取当前一级评论区的所有后代子评论区，封装到当前commentGruop
                List<Comments> descendantComments = new ArrayList<>();
                // 使用递归方法获得一级评论的全部后代评论，封装到descendantComments
                getChildComments(comment.getCommentsId(), data,descendantComments,comment.getUserName());
                // 后代子区顺序是乱的，根据时间对其倒序排序
                Collections.sort(descendantComments, new Comparator<Comments>() {
                    @Override
                    public int compare(Comments o1, Comments o2) {
                        // o2和o1换位就是顺排
                        return o2.getPublishTime().compareTo(o1.getPublishTime());
                    }
                });
                commentGroup.put("childComments",descendantComments);
                // 最后添加到result中
                result.add(commentGroup);
            }
        }

        result.add(sizeMap); // 添加最后一个值
        return result;
    }

    // 存用户输入的评论
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setComments(Comments comments) {

        bookDetailMapper.insertComments(comments);
    }

    // 递归，查到一级评论区下所有后代评论区
    public void getChildComments(int fatherCommentId,List<Comments> data,List<Comments> middleResult,String fatherName){
//        List<Comments> middleResult=result;
        for (Comments comment : data) { // 想象遍历目录，这是一层目录
            if(comment.getSuperCommentsId()==fatherCommentId){
                // 为后代添加回复的用户名
                comment.setReplayUserName(fatherName);
                middleResult.add(comment);
                getChildComments(comment.getCommentsId(),data,middleResult,comment.getUserName());
            }
        }


    }
}

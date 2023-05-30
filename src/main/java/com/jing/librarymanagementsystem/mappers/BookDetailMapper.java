package com.jing.librarymanagementsystem.mappers;

import com.jing.librarymanagementsystem.bean.Comments;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BookDetailMapper {

    // 根据bookId查询书籍下评论所需数据
    @Select("select a.comments_id,a.book_id,a.user_id,a.content, a.publish_time,a.super_comments_id,b.user_name,b.avatar_url from `comments` a inner join `user` b on a.user_id=b.user_id where a.book_id=#{bookId} order by a.publish_time desc")
    public List<Comments> selectComments(String bookId);

    // 存储用户输入的评论
    @Insert("insert into `comments` values(#{commentsId},#{bookId},#{userId},#{content},#{publishTime},#{superCommentsId})")
    public void insertComments(Comments comments);


}

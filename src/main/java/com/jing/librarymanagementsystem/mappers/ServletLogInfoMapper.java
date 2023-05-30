package com.jing.librarymanagementsystem.mappers;

import com.jing.librarymanagementsystem.bean.ServletLogBean;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

// 德鲁伊日志记录
@Mapper
public interface ServletLogInfoMapper {

    // 获取最近一条request请求信息
    @Select("select total from servlet_log order by end_time desc limit 1")
    public String selectCurrentLog();

    // 存新的日志记录
    @Insert("insert into servlet_log values(#{endTime},#{total},#{increase},#{sessionids},#{ips},#{requestNums})")
    public void saveLog(ServletLogBean servletLogBean);
}

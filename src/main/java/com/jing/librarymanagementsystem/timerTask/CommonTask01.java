package com.jing.librarymanagementsystem.timerTask;

import com.jing.librarymanagementsystem.bean.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

/**
 * 此task只根据limit偏移量周期改变页面book的信息
 * */
@Data
@NoArgsConstructor
public class CommonTask01 extends TimerTask {

    int start; // 开始偏移量
    int num;  // 查询数量
    int maxOffset;  // 偏移量最大值，start达到或超过该值就终止计时
    Boolean state=true;  // 判断此任务是否已终止
    String currentDate;  // 更新时时间搓

    public CommonTask01(int start,int num,int maxOffset){
        this.start=start;
        this.num=num;
        this.maxOffset=maxOffset;
        // 初始化
        this.currentDate=this.currentTime();
    }

    @Override
    public void run() {
        start = start +num;
        // 每次更新，刷新当前时间参数
        this.currentDate = this.currentTime();
        if(start>=maxOffset-2*num){
            this.state=false;
            cancel();  // 终止计时
        }

    }

    // 得到当前时间
    public String currentTime(){

        Calendar c = Calendar.getInstance();
        Date startDate = c.getTime();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return sf.format(c.getTime());
    }

}

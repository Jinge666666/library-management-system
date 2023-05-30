package com.jing.librarymanagementsystem.util;

import com.jing.librarymanagementsystem.timerTask.CommonTask01;
import com.jing.librarymanagementsystem.timerTask.MainPageCommendTask;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 所有timer定时任务统一类，定时更新数据，当然你的任务定时不同，可以setdelay或pereiod
 * */

@Data
@NoArgsConstructor

public class TimerTaskUtil implements Serializable {
    private  int start; // sql limit 偏移量
    private int bookNum;  // 查询数量
    /**注意：最大偏移量要根据你能查到的数据总量确定*/
    private int maxOffset;  // 最大偏移量，
    private long delay=1000L; //任务启动前延迟时间 单位：毫秒
    private Date startTime;  //任务启动前延迟时间 规定哪天开始运行，可以做定时秒杀
    private long period=1000*60*2L; // 任务运行周期 单位：毫秒
    private TimerTask timerTask; // 要运行的任务对象,规定只能使用公共任务commontask01
    // 保存该工具类实例到session的key，每个任务都对应一个实例，当任务终止时，若想再次从头运行，须重置session值为null
    /**注意如果任务的参数不是完全一样，那么各个任务的timerTaskSessionName不能有相同的*/
    private String timerTaskSessionName;

    // 设计这个不仅是节省代码，更是为了定时时能统一延迟或周期，因此不应放在构造方法，而且，
    // 若单独的任务时间不同，当然可以通过set去修改你想要的时间！！！

    public TimerTaskUtil(int start,int bookNum,int maxOffset,String timerTaskSessionName){
        this.start=start;
        this.bookNum=bookNum;
        this.maxOffset=maxOffset;
        this.timerTaskSessionName=timerTaskSessionName;

        // 初始化tineTask
        TimerTask timerTask = new CommonTask01(start,bookNum,maxOffset);
        // 绑定任务
        Timer timer = new Timer();
        timer.schedule(timerTask,delay,period);
        Subject currentUser = SecurityUtils.getSubject();
        Session session = currentUser.getSession();
        // 外部controller可以通过timerTask的state属性去判断任务是否停止，决定再次去实例化该类，重新刷新任务计划
        session.setAttribute(timerTaskSessionName,timerTask);
    }


//            // 获取当前时间
//            Calendar c = Calendar.getInstance();
//            // 当前时间增加一天
//            c.add(Calendar.DAY_OF_MONTH, 1);
//            Date startDate = c.getTime();
////        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
////        System.out.println("当前日期:"+sf.format(c.getTime()));

}

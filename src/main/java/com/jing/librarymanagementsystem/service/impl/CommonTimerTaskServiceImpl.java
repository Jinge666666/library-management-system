package com.jing.librarymanagementsystem.service.impl;

import com.jing.librarymanagementsystem.service.CommonTimerTaskService;
import com.jing.librarymanagementsystem.timerTask.CommonTask01;
import com.jing.librarymanagementsystem.util.TimerTaskUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;

@Service
public class CommonTimerTaskServiceImpl implements CommonTimerTaskService {
//    String taskName; // 任务名
//    int start;   // 开始偏移量
//    int num;  // 查询数量
    int maxOffs;   // 最大偏移量

    @Override
    public CommonTask01 commonTimerTaskService01(String taskName, int start, int num, int maxOffset) {
        Subject currentUser = SecurityUtils.getSubject();
        Session session = currentUser.getSession();
        if(session.getAttribute(taskName)==null){
            // 内部会自动帮你将一个公共任务放在session，key就是你设定的任务名
            TimerTaskUtil timerTaskUtil = new TimerTaskUtil(start, num, maxOffset,taskName);

        }
        // 内部帮你设定好的公共任务对象
        CommonTask01 task = (CommonTask01) session.getAttribute(taskName);
        // 判断任务是否一个轮回达到了被停止
        if(!task.getState()){
            // 检测停止了，那么就是偏移量达到了上限，我们重置它，同时清除session，就可以再次运行
            task.setStart(start);
            session.setAttribute(taskName,null);
        }
        return task;
    }
}

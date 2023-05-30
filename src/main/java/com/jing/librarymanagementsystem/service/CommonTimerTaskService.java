package com.jing.librarymanagementsystem.service;

import com.jing.librarymanagementsystem.timerTask.CommonTask01;
import org.springframework.stereotype.Service;

/**
 * 简单的把controller设置定时的代码抽离出来
 * */
@Service
public interface CommonTimerTaskService {
    public CommonTask01 commonTimerTaskService01(String taskName,int start,int num,int maxOffset);
}

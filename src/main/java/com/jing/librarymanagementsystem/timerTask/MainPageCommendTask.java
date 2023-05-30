package com.jing.librarymanagementsystem.timerTask;

import java.util.TimerTask;

/*
* Timer是一种定时器工具，用来在一个后台线程计划执行指定任务。它可以计划执行一个任务一次或反复多次。
TimerTask一个抽象类，它的子类代表一个可以被Timer计划的任务。
* */
public class MainPageCommendTask extends TimerTask {
    int start=1000;
    @Override
    public void run() {
        start = start +8; // 首页推荐展示8条
//        cancel();  // 终止计时
    }

    public int getStart(){
        return  start;
    }
}

package com.jing.librarymanagementsystem.timerTask;

import java.util.TimerTask;

/*
* 专区定时更新书籍
* */
public class MainPageBookZoneTask extends TimerTask {

    int start=0;
    @Override
    public void run() {

        start = start +7;
    }

    public int getStart(){
        return  start;
    }
}

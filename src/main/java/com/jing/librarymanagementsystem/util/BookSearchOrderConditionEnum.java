package com.jing.librarymanagementsystem.util;

import java.io.Serializable;

/**
 * 该枚举常用于选择哪种排序时用到。
 * */
public enum BookSearchOrderConditionEnum implements Serializable {

    CLICK("clicks"),
    WORDCOUNT("word_count"),
    FAVORITES("favorites"),
    BORROWEDTIMES("borrowed_times"),
    COMMENTTIMES("comment_times");


    private String name;

    private BookSearchOrderConditionEnum(String name){
        this.name=name;
    }

    @Override
    public String toString() {
        return name;
    }
//    public static String CLICK(){
//        return BookSearchOrderConditionEnum.CLICK.toString();
//    }
//    public static String WORDCOUNT(){
//        return BookSearchOrderConditionEnum.WORDCOUNT.toString();
//    }
//    public static String FAVORITES(){
//        return BookSearchOrderConditionEnum.FAVORITES.toString();
//    }
//    public static String BORROWEDTIMES(){
//        return BookSearchOrderConditionEnum.BORROWEDTIMES.toString();
//    }
}

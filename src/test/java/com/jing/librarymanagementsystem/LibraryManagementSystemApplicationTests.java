package com.jing.librarymanagementsystem;

import com.jing.librarymanagementsystem.service.BackstageActuatorMonitorService;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
class LibraryManagementSystemApplicationTests {

    @Resource
    BackstageActuatorMonitorService backstageMonitorService;

    @Test
    void contextLoads() {
        System.out.println(new SimpleHash("MD5", "123456", "1673077520993", 10));
    }

    @Test
    void pinyinTest() {
//        UtilMethods utilMethods = new UtilMethods();
//        System.out.println(utilMethods.getBookInitial("我的"));
        Matcher matcher = Pattern.compile("'[\\u4E00-\\u9FA5]*'").matcher("");
        while (matcher.find()){
            System.out.println("hah"+matcher.group(0).replace("'","")+"hah");
            System.out.println("天伦");
        }


    }

    @Test
    void test() {
            String[] strArray = new String[2];
            List< String> arrayList = new ArrayList<String>(strArray.length);
            Collections.addAll(arrayList, strArray);
            arrayList.add("1");
            System.out.println(arrayList);


    }

    @Test
    void test2() {
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        System.out.println(sf.format("Mon Jan 30 11:12:23 CST 2023"));
    }

    @Test
    void test3() {
        double d=1.675924217515E9;
        String k = "5758";
        System.out.println(Integer.parseInt(k.split("\\.")[0]));
        String s= "1.675924217515E9";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        BigDecimal d2 = new BigDecimal(s);
        System.out.println(simpleDateFormat.format(new Date(d2.longValue())));
    }

}


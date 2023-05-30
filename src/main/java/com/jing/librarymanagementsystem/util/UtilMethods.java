package com.jing.librarymanagementsystem.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.jing.librarymanagementsystem.bean.OperatePermissionResources;
import com.jing.librarymanagementsystem.bean.ServletLogBean;
import com.jing.librarymanagementsystem.bean.User;
import com.jing.librarymanagementsystem.mappers.ServletLogInfoMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;

import net.sourceforge.pinyin4j.PinyinHelper;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Component
public class UtilMethods implements Serializable {


    @Resource
    UtilMethods utilMethods;
    @Resource
    HttpClientUtil httpClientUtil;
    @Resource
    ServletLogInfoMapper servletLogInfoMapper;

    // shiro默认的hash+盐的密码加密方式，加盐是为了使相同明文密码有不同的密文。
    public String psdHash(String credentials, String salt) {
        String hashAlgorithmName = "MD5";
//        // 设置随机加密盐,
//        long salt= System.currentTimeMillis();
        int hashIterations = 10; // 加密次数，次数越多越安全，别人暴力破解就更费力了,注意要和shiro配置的加密次数一样
        Object obj = new SimpleHash(hashAlgorithmName, credentials, salt, hashIterations);
        return obj.toString();
    }


    // 将从数据库查出的所有操作型权限解析，封装到OperatePermissionResourceResolver对象并返回
    public OperatePermissionResources operatePermissionResourceResolver(Set<String> resources) {
        OperatePermissionResources operatePermissionResources = new OperatePermissionResources();
        for (String resource : resources) {
            if (resource.split("\\:")[0].equals("operate")) {
                String value = resource.split("\\:")[2];
                int maxLimit;
                if (value.equals("无上限")) {
                    maxLimit = Integer.MAX_VALUE;
                } else {
                    maxLimit = Integer.parseInt(value);
                }
                switch (resource.split("\\:")[1]) {
                    case "addbook":
                        operatePermissionResources.setOperateAddBookPermission(maxLimit);
                        break;
                    case "borrowbook":
                        operatePermissionResources.setOperateBorrowBookPermission(maxLimit);
                        break;
                    case "comments":
                        operatePermissionResources.setOperateCommentsPermission(maxLimit);
                }
            }
        }

        return operatePermissionResources;
    }

    // 对即将注册为新用户的user对象生成唯一账号和加密盐,加入到传入的user对象中并返回
    // 生成用户账号id和加密盐salt
    public User setIdAndSalt(User user) {

        // 生成用户账号
        String currentTime = String.valueOf(System.currentTimeMillis());
        String userId = currentTime.substring(currentTime.length() - 5) + String.valueOf((int) (Math.random() * 900) + 100);
        user.setUserId(userId);

        // 当前时间搓+账号
        user.setSalt(String.valueOf(System.currentTimeMillis() + Long.parseLong(userId)));
        return user;
    }

    // 生成书籍id
    public String growBookId() {
        String currentTime = String.valueOf(System.currentTimeMillis());
        String bookId = currentTime.substring(currentTime.length() - 6) + String.valueOf((int) (Math.random() * 90) + 10);
        return bookId;
    }

    // java获取书名拼音首字母
    public String getBookInitial(String bookName) {
        // 发生空指针是开头英文，排除它。
        try {
            char word = bookName.charAt(0);
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            String result = pinyinArray[0].charAt(0) + "";
            return result.toUpperCase();
        } catch (java.lang.NullPointerException ignored) {
            return "无"; //字母不可能和它相等
        }

    }


    // 得到用户ip
    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = null;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (ipAddress.equals("127.0.0.1")) {
                    // 根据网卡取本机配置的IP
                    try {
                        ipAddress = InetAddress.getLocalHost().getHostAddress();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                }
            }
            // 通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ipAddress != null) {
                if (ipAddress.contains(",")) {
                    return ipAddress.split(",")[0];
                } else {
                    return ipAddress;
                }
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // 对ip得到地址信息 使用太平洋IP地址查询接口
    public static Map getAdressInfo(String ip) {
        OkHttpClient httpClient = new OkHttpClient();
        String url = "http://whois.pconline.com.cn/ipJson.jsp?ip=" + ip + "&json=true";
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = httpClient.newCall(request).execute();
            String result = response.body().string();

//            ObjectMapper objectMapper = new ObjectMapper();
//            Map resultMap = objectMapper.readValue(result,Map.class);
            return JSONObject.parseObject(result, new TypeReference<Map<String, String>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    // 将int型的秒抓为几天几小时几分钟
    public String secondStrConverter(int eta) {

        int day;
        int hour;
        int minute;
        day = eta / (24 * 60 * 60);
        eta -= day * 24 * 60 * 60;
        hour = eta / (60 * 60);
        eta -= hour * 60 * 60;
        minute = eta / 60;
        eta -= minute * 60;

        return day + "天" + hour + "小时" + minute + "分钟" + eta + "秒";
    }


    // 将E科学计数法或浮点型str转long型字符串
    public String parseEStr(String str) {

        if (str.contains("E")) {
            return Long.toString(new BigDecimal(str).longValue());
        }
        str = str.split("\\.")[0];
        return Long.toString(new BigDecimal(str).longValue());
    }
}



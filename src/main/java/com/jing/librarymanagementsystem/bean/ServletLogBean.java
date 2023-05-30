package com.jing.librarymanagementsystem.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServletLogBean implements Serializable {

    String endTime; // 结束时间
    String total;  // 累计请求数量
    String increase;  // 请求增长数
    String  sessionids;  // 前三sessionid，用","分割
    String  ips;       // 前三ip,用"," 分割
    String  requestNums;   // 前三请求数 ，用"," 分割
}


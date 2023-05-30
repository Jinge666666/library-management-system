package com.jing.librarymanagementsystem.bean;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/*
* 这是个配置类，对验证码图片对象的属性配置
* */
@Data
@Component
@ConfigurationProperties("captcha")
public class Captcha implements Serializable {

    private int captchaImgWidth; // 图片宽度
    private int captchaImgHeight; // 高度
    private String captchaImgPath; // 验证码图片中间存储地址

}

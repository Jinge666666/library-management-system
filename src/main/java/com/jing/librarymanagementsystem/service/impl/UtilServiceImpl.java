package com.jing.librarymanagementsystem.service.impl;

import com.jing.librarymanagementsystem.bean.Book;
import com.jing.librarymanagementsystem.bean.Captcha;
import com.jing.librarymanagementsystem.service.UtilService;
import com.jing.librarymanagementsystem.util.UtilMethods;
import com.xiaoleilu.hutool.captcha.CaptchaUtil;
import com.xiaoleilu.hutool.captcha.LineCaptcha;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UtilServiceImpl implements UtilService {

    @Autowired
    Captcha captcha;
    @Autowired
    UtilMethods utilMethods;

    @Override
    public String createCaptcha(){
        //定义图形验证码的长和宽
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(captcha.getCaptchaImgWidth(), captcha.getCaptchaImgHeight());
        lineCaptcha.createCode();
        //图形验证码写出，可以写出到文件，也可以写出到流
        lineCaptcha.write(captcha.getCaptchaImgPath());
        String result = lineCaptcha.getCode();
        //输出code
        System.out.println(result);
        //验证图形验证码的有效性，返回boolean值
        //boolean verify = lineCaptcha.verify("1234");
        return result;

    }

    @Override
    public void outputVerifyCodeImg(OutputStream out) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(Files.newInputStream(Paths.get(captcha.getCaptchaImgPath())));
        ImageIO.write(bufferedImage, "PNG", out);
    }

    @Override
    public List<Book> bookListFilter(List<Book> bookList,String initial) {
        // 利用lambda表达式过滤，比for效率高
        List<Book> result = bookList.stream()
                .filter((Book b) -> initial.equals(utilMethods.getBookInitial(b.getBookName())))
                .collect(Collectors.toList());
        return result;
    }

}

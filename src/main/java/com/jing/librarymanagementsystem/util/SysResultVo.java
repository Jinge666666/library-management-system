package com.jing.librarymanagementsystem.util;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/*
* 它的作用就是向前端页面传递数据，对数据进行一次封装，并加入一些状态信息或者消息，方便前后端交互用的。
* */
@Data
@Component
public class SysResultVo implements Serializable {
    private static final long serialVersionUID = -7368493786259905794L;
    /**
     * 状态码
     */
    private String state = "1";
    /**
     * 状态码对应的消息
     */
    private String message = "OK";
    /**
     * 要呈现到客户端ajax中的数据
     */
    private Object data;

    public SysResultVo() {
    }

    public SysResultVo(String message,String state,Object data) {
        this.message = message;
        this.state = state;
        this.data = data;
    }


    // 堆栈信息构造方法,错误时打印,堆栈信息由data接受
//    public SysResultVo(String message,String state,Throwable e) {
//        this.state = StateInfoEnum.THROWABLEERROR.toString();
//        this.message = message;
//        this.data = e.getMessage();
//        this.state = state;
//    }

    //登录成功方法
    public SysResultVo ok(String message,Object data){
        return new SysResultVo(message, StateInfoEnum.OK.toString(),data);
    }

    // 验证码校验失败方法
    public SysResultVo captchaFail(String message,Object data){
        return new SysResultVo(message, StateInfoEnum.CAPTCHAERROR.toString(),data);
    }

    // 用户不存在
    public SysResultVo userNotExist(String message,Object data){
        return new SysResultVo(message, StateInfoEnum.USERNOTEXISTERROR.toString(),data);
    }

    // 密码错误
    public SysResultVo psdNotExist(String message,Object data){
        return new SysResultVo(message,StateInfoEnum.PSDNOTEXISTERROR.toString(),data);
    }

    // 黑名单
    public SysResultVo userLocked(String message,Object data){
        return new SysResultVo(message,StateInfoEnum.ISLOCK.toString(),data);
    }

    //

    // 服务器内部错误 一般data传入的是堆栈信息
    public SysResultVo serverError(String message,Object data){
        return new SysResultVo(message,StateInfoEnum.SERVERERROR.toString(),data);
    }


}

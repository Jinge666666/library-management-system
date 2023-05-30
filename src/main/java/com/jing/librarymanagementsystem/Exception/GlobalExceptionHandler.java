package com.jing.librarymanagementsystem.Exception;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.Serializable;

/**
 * 定义一个全局异常捕获类，解决shiro中未授权的跳转页面配置setUnauthorizedUrl方法不起作用。
 * shiro在访问没授权的资源时，会抛出AuthorizationException，我们可定义一个全局异常捕获这个异常，并指定跳转页面。
 *
 *
 * ControllerAdvice注解的作用：是一个Controller增强器，可对controller中被@RequestMapping注解的方法加一些逻辑处理，最常用的就是异常处理；【三种使用场景】全局异常处理。全局数据绑定，全局数据预处理
 * Order 注解@Order或者接口Ordered的作用是定义SpringIOC容器中Bean的执行顺序的优先级，而不是定义Bean的加载顺序，Bean的加载顺序不受@Order或Ordered接口的影响；
 * ExceptionHandler 指定捕获的目的异常
 *
 */
@ControllerAdvice
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler implements Serializable {

    @ExceptionHandler(value = {AuthorizationException.class, UnauthorizedException.class})
    public String handleAuthorizationException() {
        return "error/401";
    }
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public String handleHttpRequestMethodNotSupportedException() {
        return "error/404";
    }

    // 服务器内部错误

}


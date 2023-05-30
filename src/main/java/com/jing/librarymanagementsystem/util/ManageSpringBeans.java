package com.jing.librarymanagementsystem.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * 解决线程类中不能使用@Autowired注解注入Bean\new出来的线程类中无法使用@Autowired注入Bean
 * 如定时器是一个线程类，在里面的任务，只要含有自动注解的，都会注入失败，可通过如下工具类解决
 * 使用时调用getBean方法可获得指定的bean
 * */

@Component
public class ManageSpringBeans implements ApplicationContextAware {
    private static ApplicationContext context;

    public static <T> T getBean(final Class<T> requiredType) {
        return context.getBean(requiredType);
    }

    public static <T> T getBean(final String beanName) {
        @SuppressWarnings("unchecked")
        final T bean = (T) context.getBean(beanName);
        return bean;
    }

    public static <T> Map<String, T> getBeans(final Class<T> requiredType) {
        return context.getBeansOfType(requiredType);
    }

    public static Map<String, Object> getBeansWithAnnotation(final Class<? extends Annotation> annotationType) {
        return context.getBeansWithAnnotation(annotationType);
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) {
        context = applicationContext;
    }

}

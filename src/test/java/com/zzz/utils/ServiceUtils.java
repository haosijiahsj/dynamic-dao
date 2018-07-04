package com.zzz.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author hushengjun
 * @date 2018/1/11
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceUtils {

    private static final String XML_PATH = "classpath:config/app.xml";
    private static ApplicationContext applicationContext = new ClassPathXmlApplicationContext(XML_PATH);

    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public static <T> T getBean(String beanName, Class<T> clazz) {
        return applicationContext.getBean(beanName, clazz);
    }

}

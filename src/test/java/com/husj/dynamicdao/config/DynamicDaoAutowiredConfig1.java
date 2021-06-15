package com.husj.dynamicdao.config;

import com.husj.dynamicdao.spring.DynamicDaoScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author 胡胜钧
 * @date 7/4 0004.
 */
@Configuration
@DynamicDaoScan(value = "com.husj.dynamicdao.seconddao")
public class DynamicDaoAutowiredConfig1 {

}
